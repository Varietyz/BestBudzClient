package com.bestbudz.network;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.NetworkConfig;
import com.bestbudz.util.NodeList;
import com.bestbudz.util.NodeSubList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public final class CacheManager extends CacheManagerBase implements Runnable {

	public CacheManager() {
		requested = new NodeList();
		statusString = "";
		ioBuffer = new byte[500];
		fileStatus = new byte[4][];
		priorityQueue = new NodeList();
		running = true;
		waiting = false;
		completedQueue = new NodeList();
		activeRequests = new NodeSubList();
		versions = new int[4][];
		failedQueue = new NodeList();
		pendingQueue = new NodeList();
	}

	private void readData() {
		try {
			int available = inputStream.available();
			if (expectedSize == 0 && available >= 6) {
				waiting = true;
				// Read 6-byte header
				int totalRead = 0;
				while (totalRead < 6) {
					int read = inputStream.read(ioBuffer, totalRead, 6 - totalRead);
					if (read == -1) throw new IOException("Unexpected end of stream");
					totalRead += read;
				}

				int dataType = ioBuffer[0] & 0xff;
				int fileId = ((ioBuffer[1] & 0xff) << 8) + (ioBuffer[2] & 0xff);
				int fileSize = ((ioBuffer[3] & 0xff) << 8) + (ioBuffer[4] & 0xff);
				int blockIndex = ioBuffer[5] & 0xff;

				// Find matching request
				current = null;
				for (OnDemandData data = (OnDemandData) requested.reverseGetFirst();
					 data != null;
					 data = (OnDemandData) requested.reverseGetNext()) {
					if (data.dataType == dataType && data.ID == fileId) {
						current = data;
						data.loopCycle = 0;
						break;
					}
				}

				if (current != null) {
					loopCycle = 0;
					if (fileSize == 0) {
						// Request rejected
						Signlink.reporterror("Rej: " + dataType + "," + fileId);
						current.buffer = null;
						if (current.incomplete) {
							synchronized (completedQueue) {
								completedQueue.insertHead(current);
							}
						} else {
							current.unlink();
						}
						current = null;
					} else {
						// Allocate buffer for first block
						if (current.buffer == null && blockIndex == 0) {
							current.buffer = new byte[fileSize];
						}
						if (current.buffer == null) {
							throw new IOException("Missing start of file");
						}
					}
				}

				completedSize = blockIndex * 500;
				expectedSize = Math.min(500, fileSize - completedSize);
			}

			if (expectedSize > 0 && available >= expectedSize) {
				waiting = true;
				byte[] targetBuffer = ioBuffer;
				int offset = 0;

				if (current != null) {
					targetBuffer = current.buffer;
					offset = completedSize;
				}

				// Read data block
				int totalRead = 0;
				while (totalRead < expectedSize) {
					int read = inputStream.read(targetBuffer, offset + totalRead, expectedSize - totalRead);
					if (read == -1) throw new IOException("Unexpected end of stream");
					totalRead += read;
				}

				// Check if file is complete
				if (current != null && expectedSize + completedSize >= current.buffer.length) {
					if (Client.decompressors[0] != null) {
						Client.decompressors[current.dataType + 1].method234(
							current.buffer.length, current.buffer, current.ID);
					}

					if (!current.incomplete && current.dataType == 3) {
						current.incomplete = true;
						current.dataType = 93;
					}

					if (current.incomplete) {
						synchronized (completedQueue) {
							completedQueue.insertHead(current);
						}
					} else {
						current.unlink();
					}
				}
				expectedSize = 0;
			}
		} catch (IOException e) {
			closeConnection();
		}
	}

	private void closeConnection() {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception ignored) {}
		}
		socket = null;
		inputStream = null;
		outputStream = null;
		expectedSize = 0;
	}

	public void start(ArchiveLoader archiveLoader, Client client1) {
		byte[] indexData = archiveLoader.extractFile("map_index");
		Stream stream = new Stream(indexData);
		int mapCount = stream.readUnsignedWord();

		mapKeys = new int[mapCount];
		landscapeIndices = new int[mapCount];
		objectIndices = new int[mapCount];

		for (int i = 0; i < mapCount; i++) {
			mapKeys[i] = stream.readUnsignedWord();
			landscapeIndices[i] = stream.readUnsignedWord();
			objectIndices[i] = stream.readUnsignedWord();
		}
		System.out.println("Maps Loaded: " + mapCount);

		indexData = archiveLoader.extractFile("midi_index");
		stream = new Stream(indexData);
		int midiCount = Objects.requireNonNull(indexData).length;
		midiIndices = new int[midiCount];
		for (int i = 0; i < midiCount; i++) {
			midiIndices[i] = stream.readUnsignedByte();
		}

		client = client1;
		running = true;
		client.startRunnable(this, 2);
	}

	public int getNodeCount() {
		synchronized (activeRequests) {
			return activeRequests.getNodeCount();
		}
	}

	public void disable() {
		running = false;
		closeConnection();
	}

	public void preloadMaps(boolean flag) {
		int count = mapKeys.length;
		for (int i = 0; i < count; i++) {
			if (flag || mapStatus[i] != 0) {
				markFileLoaded((byte) 2, 3, objectIndices[i]);
				markFileLoaded((byte) 2, 3, landscapeIndices[i]);
			}
		}
	}

	public int getVersionCount(int j) {
		return versions[j].length;
	}

	private void closeRequest(OnDemandData request) {
		try {
			if (socket == null) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastConnectionTime < 4000L) return;

				lastConnectionTime = currentTime;
				socket = client.openSocket(NetworkConfig.SERVER_PORT + Client.portOff);
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				outputStream.write(15);

				// Skip 8 bytes
				for (int i = 0; i < 8; i++) {
					inputStream.read();
				}
				loopCycle = 0;
			}

			ioBuffer[0] = (byte) request.dataType;
			ioBuffer[1] = (byte) (request.ID >> 8);
			ioBuffer[2] = (byte) request.ID;
			ioBuffer[3] = request.incomplete ? (byte) 2 : (!Client.loggedIn ? (byte) 1 : (byte) 0);

			outputStream.write(ioBuffer, 0, 4);
			keepAliveCounter = 0;
			connectionErrors = -10000;
		} catch (IOException e) {
			closeConnection();
			connectionErrors++;
		}
	}

	public int getAnimCount() {
		return animationIndices.length;
	}

	public int getModelCount() {
		return 29191;
	}

	public void enqueueRequest(int dataType, int id) {
		synchronized (activeRequests) {
			// Check if already exists
			for (OnDemandData data = (OnDemandData) activeRequests.reverseGetFirst();
				 data != null;
				 data = (OnDemandData) activeRequests.reverseGetNext()) {
				if (data.dataType == dataType && data.ID == id) return;
			}

			OnDemandData newRequest = new OnDemandData();
			newRequest.dataType = dataType;
			newRequest.ID = id;
			newRequest.incomplete = true;

			synchronized (pendingQueue) {
				pendingQueue.insertHead(newRequest);
			}
			activeRequests.insertHead(newRequest);
		}
	}

	public int getModelIndex(int i) {
		return modelIndices[i] & 0xff;
	}

	public void run() {
		try {
			while (running) {
				onDemandCycle++;

				// Dynamic sleep timing
				int sleepTime = (maxPriority == 0 && client.decompressors[0] != null) ? 50 : 20;
				Thread.sleep(sleepTime);

				waiting = true;
				for (int i = 0; i < 100 && waiting; i++) {
					waiting = false;
					checkReceived();
					handleFailed();
					if (uncompletedCount == 0 && i >= 5) break;
					processQueue();
					if (inputStream != null) readData();
				}

				// Handle request timeouts
				boolean hasIncomplete = processRequestTimeouts();

				if (hasIncomplete) {
					loopCycle++;
					if (loopCycle > 750) {
						closeConnection();
					}
				} else {
					loopCycle = 0;
					statusString = "";
				}

				// Send keepalive
				if (Client.loggedIn && socket != null && outputStream != null &&
					(maxPriority > 0 || client.decompressors[0] == null)) {
					keepAliveCounter++;
					if (keepAliveCounter > 500) {
						keepAliveCounter = 0;
						try {
							ioBuffer[0] = ioBuffer[1] = ioBuffer[2] = 0;
							ioBuffer[3] = 10;
							outputStream.write(ioBuffer, 0, 4);
						} catch (IOException e) {
							loopCycle = 5000;
						}
					}
				}
			}
		} catch (Exception e) {
			Signlink.reporterror("od_ex " + e.getMessage());
		}
	}

	private boolean processRequestTimeouts() {
		boolean hasIncomplete = false;

		for (OnDemandData data = (OnDemandData) requested.reverseGetFirst();
			 data != null;
			 data = (OnDemandData) requested.reverseGetNext()) {
			if (data.incomplete) {
				hasIncomplete = true;
				data.loopCycle++;
				if (data.loopCycle > 50) {
					data.loopCycle = 0;
					closeRequest(data);
				}
			}
		}

		if (!hasIncomplete) {
			for (OnDemandData data = (OnDemandData) requested.reverseGetFirst();
				 data != null;
				 data = (OnDemandData) requested.reverseGetNext()) {
				hasIncomplete = true;
				data.loopCycle++;
				if (data.loopCycle > 50) {
					data.loopCycle = 0;
					closeRequest(data);
				}
			}
		}

		return hasIncomplete;
	}

	public void requestFile(int i, int j) {
		if (client.decompressors[0] == null || maxPriority == 0) return;

		OnDemandData request = new OnDemandData();
		request.dataType = j;
		request.ID = i;
		request.incomplete = false;

		synchronized (priorityQueue) {
			priorityQueue.insertHead(request);
		}
	}

	public OnDemandData getCompletedFile() {
		OnDemandData data;
		synchronized (completedQueue) {
			data = (OnDemandData) completedQueue.popHead();
		}
		if (data == null) return null;

		synchronized (activeRequests) {
			data.unlinkSub();
		}

		if (data.buffer == null) return data;

		// Decompress if needed
		try (GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(data.buffer))) {
			byte[] decompressed = gzipStream.readAllBytes();
			data.buffer = decompressed;
		} catch (IOException e) {
			System.out.println("Failed to unzip model [" + data.ID + "] type = " + data.dataType);
			e.printStackTrace();
			return null;
		}

		return data;
	}

	public int getMapIndex(int i, int k, int l) {
		int combined = (l << 8) + k;
		for (int j = 0; j < mapKeys.length; j++) {
			if (mapKeys[j] == combined) {
				if (i == 0) {
					return landscapeIndices[j] > 3535 ? -1 : landscapeIndices[j];
				} else {
					return objectIndices[j] > 3535 ? -1 : objectIndices[j];
				}
			}
		}
		return -1;
	}

	public void requestModel(int i) {
		enqueueRequest(0, i);
	}

	public void markFileLoaded(byte status, int type, int id) {
		if (client.decompressors[0] == null || versions[type][id] == 0) return;

		client.decompressors[type + 1].decompress(id);
		fileStatus[type][id] = status;
		if (status > maxPriority) maxPriority = status;
		totalFileCount++;
	}

	public boolean hasObjectData(int i) {
		for (int k = 0; k < mapKeys.length; k++) {
			if (objectIndices[k] == i) return true;
		}
		return false;
	}

	private void handleFailed() {
		uncompletedCount = 0;
		completedCount = 0;

		for (OnDemandData data = (OnDemandData) requested.reverseGetFirst();
			 data != null;
			 data = (OnDemandData) requested.reverseGetNext()) {
			if (data.incomplete) {
				uncompletedCount++;
				System.out.println("Error: model is incomplete or missing [type=" +
					data.dataType + "] [id=" + data.ID + "]");
			} else {
				completedCount++;
			}
		}

		while (uncompletedCount < 10) {
			OnDemandData failed = (OnDemandData) failedQueue.popHead();
			if (failed == null) break;

			if (fileStatus[failed.dataType][failed.ID] != 0) loadedFileCount++;
			fileStatus[failed.dataType][failed.ID] = 0;
			requested.insertHead(failed);
			uncompletedCount++;
			closeRequest(failed);
			waiting = true;

			System.out.println("Error: file is missing [type=" +
				failed.dataType + "] [id=" + failed.ID + "]");
		}
	}

	public void clearPriority() {
		synchronized (priorityQueue) {
			priorityQueue.removeAll();
		}
	}

	private void checkReceived() {
		OnDemandData data;
		synchronized (pendingQueue) {
			data = (OnDemandData) pendingQueue.popHead();
		}

		while (data != null) {
			waiting = true;
			byte[] result = null;

			if (client.decompressors[0] != null) {
				result = client.decompressors[data.dataType + 1].decompress(data.ID);
			}

			synchronized (pendingQueue) {
				if (result == null) {
					failedQueue.insertHead(data);
				} else {
					data.buffer = result;
					synchronized (completedQueue) {
						completedQueue.insertHead(data);
					}
				}
				data = (OnDemandData) pendingQueue.popHead();
			}
		}
	}

	private void processQueue() {
		while (uncompletedCount == 0 && completedCount < 10 && maxPriority > 0) {
			OnDemandData data;
			synchronized (priorityQueue) {
				data = (OnDemandData) priorityQueue.popHead();
			}

			while (data != null) {
				if (fileStatus[data.dataType][data.ID] != 0) {
					fileStatus[data.dataType][data.ID] = 0;
					requested.insertHead(data);
					closeRequest(data);
					waiting = true;
					if (loadedFileCount < totalFileCount) loadedFileCount++;
					statusString = "Loading extra files - " + (loadedFileCount * 100) / totalFileCount + "%";
					completedCount++;
					if (completedCount == 10) return;
				}

				synchronized (priorityQueue) {
					data = (OnDemandData) priorityQueue.popHead();
				}
			}

			// Process file status arrays
			for (int type = 0; type < 4; type++) {
				byte[] status = fileStatus[type];
				for (int id = 0; id < status.length; id++) {
					if (status[id] == maxPriority) {
						status[id] = 0;
						OnDemandData newRequest = new OnDemandData();
						newRequest.dataType = type;
						newRequest.ID = id;
						newRequest.incomplete = false;

						requested.insertHead(newRequest);
						closeRequest(newRequest);
						waiting = true;
						if (loadedFileCount < totalFileCount) loadedFileCount++;
						statusString = "Loading extra files - " + (loadedFileCount * 100) / totalFileCount + "%";
						completedCount++;
						if (completedCount == 10) return;
					}
				}
			}
			maxPriority--;
		}
	}

	public boolean isMidiEnabled(int i) {
		return midiIndices[i] == 1;
	}

	// Fields remain the same
	private int totalFileCount;
	private final NodeList requested;
	private int maxPriority;
	public String statusString;
	private int keepAliveCounter;
	private long lastConnectionTime;
	private int[] objectIndices;
	private final byte[] ioBuffer;
	public int onDemandCycle;
	private final byte[][] fileStatus;
	private Client client;
	private final NodeList priorityQueue;
	private int completedSize;
	private int expectedSize;
	private int[] midiIndices;
	public int connectionErrors;
	private int[] landscapeIndices;
	private int loadedFileCount;
	private boolean running;
	private OutputStream outputStream;
	private int[] mapStatus;
	private boolean waiting;
	private final NodeList completedQueue;
	private int[] animationIndices;
	private final NodeSubList activeRequests;
	private InputStream inputStream;
	private Socket socket;
	private final int[][] versions;
	private int uncompletedCount;
	private int completedCount;
	private final NodeList failedQueue;
	private OnDemandData current;
	private final NodeList pendingQueue;
	private int[] mapKeys;
	private byte[] modelIndices;
	private int loopCycle;
}