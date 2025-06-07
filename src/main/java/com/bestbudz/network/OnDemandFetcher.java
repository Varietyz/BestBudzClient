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

public final class OnDemandFetcher extends OnDemandFetcherParent implements Runnable {

	public OnDemandFetcher() {
		requested = new NodeList();
		statusString = "";
		ioBuffer = new byte[500];
		fileStatus = new byte[4][];
		aClass19_1344 = new NodeList();
		running = true;
		waiting = false;
		aClass19_1358 = new NodeList();
		nodeSubList = new NodeSubList();
		versions = new int[4][];
		aClass19_1368 = new NodeList();
		aClass19_1370 = new NodeList();
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
							synchronized (aClass19_1358) {
								aClass19_1358.insertHead(current);
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
						synchronized (aClass19_1358) {
							aClass19_1358.insertHead(current);
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

	public void start(StreamLoader streamLoader, Client client1) {
		byte[] indexData = streamLoader.getDataForName("map_index");
		Stream stream = new Stream(indexData);
		int mapCount = stream.readUnsignedWord();

		mapIndices1 = new int[mapCount];
		mapIndices2 = new int[mapCount];
		mapIndices3 = new int[mapCount];

		for (int i = 0; i < mapCount; i++) {
			mapIndices1[i] = stream.readUnsignedWord();
			mapIndices2[i] = stream.readUnsignedWord();
			mapIndices3[i] = stream.readUnsignedWord();
		}
		System.out.println("Maps Loaded: " + mapCount);

		indexData = streamLoader.getDataForName("midi_index");
		stream = new Stream(indexData);
		int midiCount = Objects.requireNonNull(indexData).length;
		anIntArray1348 = new int[midiCount];
		for (int i = 0; i < midiCount; i++) {
			anIntArray1348[i] = stream.readUnsignedByte();
		}

		clientInstance = client1;
		running = true;
		clientInstance.startRunnable(this, 2);
	}

	public int getNodeCount() {
		synchronized (nodeSubList) {
			return nodeSubList.getNodeCount();
		}
	}

	public void disable() {
		running = false;
		closeConnection();
	}

	public void method554(boolean flag) {
		int count = mapIndices1.length;
		for (int i = 0; i < count; i++) {
			if (flag || mapIndices4[i] != 0) {
				method563((byte) 2, 3, mapIndices3[i]);
				method563((byte) 2, 3, mapIndices2[i]);
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
				if (currentTime - openSocketTime < 4000L) return;

				openSocketTime = currentTime;
				socket = clientInstance.openSocket(NetworkConfig.SERVER_PORT + Client.portOff);
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
			writeLoopCycle = 0;
			anInt1349 = -10000;
		} catch (IOException e) {
			closeConnection();
			anInt1349++;
		}
	}

	public int getAnimCount() {
		return anIntArray1360.length;
	}

	public int getModelCount() {
		return 29191;
	}

	public void method558(int dataType, int id) {
		synchronized (nodeSubList) {
			// Check if already exists
			for (OnDemandData data = (OnDemandData) nodeSubList.reverseGetFirst();
				 data != null;
				 data = (OnDemandData) nodeSubList.reverseGetNext()) {
				if (data.dataType == dataType && data.ID == id) return;
			}

			OnDemandData newRequest = new OnDemandData();
			newRequest.dataType = dataType;
			newRequest.ID = id;
			newRequest.incomplete = true;

			synchronized (aClass19_1370) {
				aClass19_1370.insertHead(newRequest);
			}
			nodeSubList.insertHead(newRequest);
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
				int sleepTime = (anInt1332 == 0 && clientInstance.decompressors[0] != null) ? 50 : 20;
				Thread.sleep(sleepTime);

				waiting = true;
				for (int i = 0; i < 100 && waiting; i++) {
					waiting = false;
					checkReceived();
					handleFailed();
					if (uncompletedCount == 0 && i >= 5) break;
					method568();
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
					(anInt1332 > 0 || clientInstance.decompressors[0] == null)) {
					writeLoopCycle++;
					if (writeLoopCycle > 500) {
						writeLoopCycle = 0;
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

	public void method560(int i, int j) {
		if (clientInstance.decompressors[0] == null || anInt1332 == 0) return;

		OnDemandData request = new OnDemandData();
		request.dataType = j;
		request.ID = i;
		request.incomplete = false;

		synchronized (aClass19_1344) {
			aClass19_1344.insertHead(request);
		}
	}

	public OnDemandData getNextNode() {
		OnDemandData data;
		synchronized (aClass19_1358) {
			data = (OnDemandData) aClass19_1358.popHead();
		}
		if (data == null) return null;

		synchronized (nodeSubList) {
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

	public int method562(int i, int k, int l) {
		int combined = (l << 8) + k;
		for (int j = 0; j < mapIndices1.length; j++) {
			if (mapIndices1[j] == combined) {
				if (i == 0) {
					return mapIndices2[j] > 3535 ? -1 : mapIndices2[j];
				} else {
					return mapIndices3[j] > 3535 ? -1 : mapIndices3[j];
				}
			}
		}
		return -1;
	}

	public void method548(int i) {
		method558(0, i);
	}

	public void method563(byte status, int type, int id) {
		if (clientInstance.decompressors[0] == null || versions[type][id] == 0) return;

		clientInstance.decompressors[type + 1].decompress(id);
		fileStatus[type][id] = status;
		if (status > anInt1332) anInt1332 = status;
		totalFiles++;
	}

	public boolean method564(int i) {
		for (int k = 0; k < mapIndices1.length; k++) {
			if (mapIndices3[k] == i) return true;
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
			OnDemandData failed = (OnDemandData) aClass19_1368.popHead();
			if (failed == null) break;

			if (fileStatus[failed.dataType][failed.ID] != 0) filesLoaded++;
			fileStatus[failed.dataType][failed.ID] = 0;
			requested.insertHead(failed);
			uncompletedCount++;
			closeRequest(failed);
			waiting = true;

			System.out.println("Error: file is missing [type=" +
				failed.dataType + "] [id=" + failed.ID + "]");
		}
	}

	public void method566() {
		synchronized (aClass19_1344) {
			aClass19_1344.removeAll();
		}
	}

	private void checkReceived() {
		OnDemandData data;
		synchronized (aClass19_1370) {
			data = (OnDemandData) aClass19_1370.popHead();
		}

		while (data != null) {
			waiting = true;
			byte[] result = null;

			if (clientInstance.decompressors[0] != null) {
				result = clientInstance.decompressors[data.dataType + 1].decompress(data.ID);
			}

			synchronized (aClass19_1370) {
				if (result == null) {
					aClass19_1368.insertHead(data);
				} else {
					data.buffer = result;
					synchronized (aClass19_1358) {
						aClass19_1358.insertHead(data);
					}
				}
				data = (OnDemandData) aClass19_1370.popHead();
			}
		}
	}

	private void method568() {
		while (uncompletedCount == 0 && completedCount < 10 && anInt1332 > 0) {
			OnDemandData data;
			synchronized (aClass19_1344) {
				data = (OnDemandData) aClass19_1344.popHead();
			}

			while (data != null) {
				if (fileStatus[data.dataType][data.ID] != 0) {
					fileStatus[data.dataType][data.ID] = 0;
					requested.insertHead(data);
					closeRequest(data);
					waiting = true;
					if (filesLoaded < totalFiles) filesLoaded++;
					statusString = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
					completedCount++;
					if (completedCount == 10) return;
				}

				synchronized (aClass19_1344) {
					data = (OnDemandData) aClass19_1344.popHead();
				}
			}

			// Process file status arrays
			for (int type = 0; type < 4; type++) {
				byte[] status = fileStatus[type];
				for (int id = 0; id < status.length; id++) {
					if (status[id] == anInt1332) {
						status[id] = 0;
						OnDemandData newRequest = new OnDemandData();
						newRequest.dataType = type;
						newRequest.ID = id;
						newRequest.incomplete = false;

						requested.insertHead(newRequest);
						closeRequest(newRequest);
						waiting = true;
						if (filesLoaded < totalFiles) filesLoaded++;
						statusString = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
						completedCount++;
						if (completedCount == 10) return;
					}
				}
			}
			anInt1332--;
		}
	}

	public boolean method569(int i) {
		return anIntArray1348[i] == 1;
	}

	// Fields remain the same
	private int totalFiles;
	private final NodeList requested;
	private int anInt1332;
	public String statusString;
	private int writeLoopCycle;
	private long openSocketTime;
	private int[] mapIndices3;
	private final byte[] ioBuffer;
	public int onDemandCycle;
	private final byte[][] fileStatus;
	private Client clientInstance;
	private final NodeList aClass19_1344;
	private int completedSize;
	private int expectedSize;
	private int[] anIntArray1348;
	public int anInt1349;
	private int[] mapIndices2;
	private int filesLoaded;
	private boolean running;
	private OutputStream outputStream;
	private int[] mapIndices4;
	private boolean waiting;
	private final NodeList aClass19_1358;
	private int[] anIntArray1360;
	private final NodeSubList nodeSubList;
	private InputStream inputStream;
	private Socket socket;
	private final int[][] versions;
	private int uncompletedCount;
	private int completedCount;
	private final NodeList aClass19_1368;
	private OnDemandData current;
	private final NodeList aClass19_1370;
	private int[] mapIndices1;
	private byte[] modelIndices;
	private int loopCycle;
}