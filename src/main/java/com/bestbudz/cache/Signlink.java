package com.bestbudz.cache;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.core.LoadingErrorScreen.addConsoleMessage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.lang.ref.WeakReference;
import java.util.Map;

public final class Signlink implements Runnable {

	public static final int clientversion = 317;
	public static int uid;
	public static int storeid = 32;
	public static boolean sunjava;
	public static String dns = null;
	public static String midi = null;
	public static int midivol;
	public static int midifade;
	public static int wavevol;
	public static boolean reporterror = true;
	public static String errorname = "";
	private static boolean active;
	private static int threadliveid;
	private static InetAddress socketip;
	private static int socketreq;
	private static Socket socket = null;
	private static int threadreqpri = 1;
	private static Runnable threadreq = null;
	private static String dnsreq = null;
	private static String urlreq = null;
	private static DataInputStream urlstream = null;
	private static int savelen;
	private static String savereq = null;
	private static byte[] savebuf = null;
	private static boolean midiplay;
	private static int midipos;
	private static boolean waveplay;
	private static int wavepos;

	private static final Map<String, WeakReference<byte[]>> fileCache = new ConcurrentHashMap<>();
	private static final Map<String, Long> fileCacheAccessTimes = new ConcurrentHashMap<>();
	private static final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
	private static final int MAX_FILE_CACHE_SIZE = 50;
	private static final int MAX_CACHED_FILE_SIZE = 512 * 1024;
	private static final long CACHE_CLEANUP_INTERVAL = 60000;

	private static long totalFilesProcessed = 0;
	private static long totalBytesProcessed = 0;
	private static long cacheHits = 0;
	private static long cacheMisses = 0;
	private static long lastCacheCleanup = 0;
	private static String cachedCacheDir = null;

	private Signlink() {
	}

	public static void startpriv(InetAddress inetaddress) {
		threadliveid = (int) (Math.random() * 99999999D);
		if (active) {
			try {
				Thread.sleep(500L);
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}
			active = false;
		}
		socketreq = 0;
		threadreq = null;
		dnsreq = null;
		savereq = null;
		urlreq = null;
		socketip = inetaddress;
		Thread thread = new Thread(new Signlink());
		thread.setDaemon(true);
		thread.start();
		while (!active) {
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}
		}
	}

	public static String findCacheDir() {

		if (cachedCacheDir != null) {
			return cachedCacheDir;
		}

		cacheLock.writeLock().lock();
		try {

			if (cachedCacheDir != null) {
				return cachedCacheDir;
			}

			Path cacheDir = Paths.get(System.getProperty("user.home"), ".BestBudzCache");
			String theme = "bestbudz";

			try {
				Files.createDirectories(cacheDir);

				Set<Path> expected = new HashSet<>();
				expected.addAll(syncOnceOptimized("/caches/fixed", cacheDir));
				expected.addAll(syncOnceOptimized("/caches/" + theme, cacheDir));
				deleteStaleOptimized(cacheDir, expected);

			} catch (IOException | URISyntaxException ex) {
				throw new RuntimeException("Failed to synchronise cache", ex);
			}

			cachedCacheDir = cacheDir.toAbsolutePath() + File.separator;
			return cachedCacheDir;

		} finally {
			cacheLock.writeLock().unlock();
		}
	}

	private static Set<Path> syncOnceOptimized(String resourceRoot, Path targetDir)
		throws IOException, URISyntaxException {

		cacheLock.readLock().lock();
		try {
			Set<Path> embedded = new HashSet<>();
			String root = resourceRoot.startsWith("/") ? resourceRoot.substring(1) : resourceRoot;
			URL url = Client.class.getResource('/' + root + '/');

			if (url != null && "file".equals(url.getProtocol())) {
				Path rootPath = Paths.get(url.toURI());
				try (Stream<Path> s = Files.walk(rootPath)) {
					s.filter(Files::isRegularFile).forEach(src -> {
						Path rel = rootPath.relativize(src);
						embedded.add(rel);
						copyIfMissingOptimized(src, targetDir.resolve(rel));
					});
				}
				return embedded;
			}

			String jarPath = Client.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath();

			try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
				Enumeration<JarEntry> e = jar.entries();
				int processedCount = 0;

				while (e.hasMoreElements()) {
					JarEntry je = e.nextElement();
					if (je.isDirectory()) continue;

					String name = je.getName();
					if (!name.startsWith(root + '/')) continue;

					Path rel = Paths.get(root).relativize(Paths.get(name));
					embedded.add(rel);

					Path dest = targetDir.resolve(rel);
					if (Files.notExists(dest)) {
						Files.createDirectories(dest.getParent());

						long fileSize = je.getSize();
						if (fileSize > 0 && fileSize <= MAX_CACHED_FILE_SIZE) {

							byte[] cachedData = getCachedFileData(name);
							if (cachedData != null) {
								Files.write(dest, cachedData);
								cacheHits++;
							} else {

								try (InputStream in = jar.getInputStream(je)) {
									byte[] fileData = readAllBytesCompat(in);
									Files.write(dest, fileData);
									cacheFileData(name, fileData);
									cacheMisses++;
									totalBytesProcessed += fileData.length;
								}
							}
						} else {

							try (InputStream in = jar.getInputStream(je)) {
								Files.copy(in, dest);
								cacheMisses++;
							}
						}

						addConsoleMessage("[CACHE] + " + rel);
					}

					processedCount++;
					totalFilesProcessed++;

					if (processedCount % 100 == 0) {
						performPeriodicCacheCleanup();
					}
				}
			}
			return embedded;

		} finally {
			cacheLock.readLock().unlock();
		}
	}

	private static void copyIfMissingOptimized(Path src, Path dest) {
		if (Files.notExists(dest)) {
			try {
				Files.createDirectories(dest.getParent());

				long fileSize = Files.size(src);
				if (fileSize <= MAX_CACHED_FILE_SIZE) {
					String cacheKey = src.toString();
					byte[] cachedData = getCachedFileData(cacheKey);

					if (cachedData != null) {
						Files.write(dest, cachedData);
						cacheHits++;
					} else {
						byte[] fileData = readAllBytesCompat(Files.newInputStream(src));
						Files.copy(src, dest);
						cacheFileData(cacheKey, fileData);
						cacheMisses++;
						totalBytesProcessed += fileData.length;
					}
				} else {

					Files.copy(src, dest);
					cacheMisses++;
				}

				addConsoleMessage("[COPIED-CACHE] Was missing " + dest.getFileName());
				totalFilesProcessed++;

			} catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}
	}

	private static void deleteStaleOptimized(Path cacheDir, Set<Path> keep) throws IOException {
		try (Stream<Path> s = Files.walk(cacheDir)) {
			s.filter(Files::isRegularFile)
				.filter(p -> !keep.contains(cacheDir.relativize(p)))
				.forEach(p -> {
					try {
						Files.deleteIfExists(p);

						String cacheKey = p.toString();
						fileCache.remove(cacheKey);
						fileCacheAccessTimes.remove(cacheKey);
					} catch (IOException ignored) {
					}
				});
		}
	}

	private static byte[] getCachedFileData(String key) {
		WeakReference<byte[]> ref = fileCache.get(key);
		if (ref != null) {
			byte[] data = ref.get();
			if (data != null) {
				fileCacheAccessTimes.put(key, System.currentTimeMillis());
				return data;
			} else {
				fileCache.remove(key);
				fileCacheAccessTimes.remove(key);
			}
		}
		return null;
	}

	private static void cacheFileData(String key, byte[] data) {
		if (data == null || data.length == 0 || data.length > MAX_CACHED_FILE_SIZE) {
			return;
		}

		if (fileCache.size() >= MAX_FILE_CACHE_SIZE) {
			cleanupOldestFileCache();
		}

		fileCache.put(key, new WeakReference<>(data));
		fileCacheAccessTimes.put(key, System.currentTimeMillis());
	}

	private static void cleanupOldestFileCache() {
		if (fileCacheAccessTimes.isEmpty()) return;

		int removeCount = Math.max(1, fileCacheAccessTimes.size() * 3 / 10);

		fileCacheAccessTimes.entrySet().stream()
			.sorted(Map.Entry.comparingByValue())
			.limit(removeCount)
			.forEach(entry -> {
				String key = entry.getKey();
				fileCache.remove(key);
				fileCacheAccessTimes.remove(key);
			});
	}

	private static void performPeriodicCacheCleanup() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastCacheCleanup > CACHE_CLEANUP_INTERVAL) {
			lastCacheCleanup = currentTime;

			fileCache.entrySet().removeIf(entry -> entry.getValue().get() == null);

			fileCacheAccessTimes.entrySet().removeIf(entry -> !fileCache.containsKey(entry.getKey()));

			long memoryUsage = getCurrentMemoryUsage();
			if (memoryUsage > 300) {
				performEmergencyCacheCleanup();
			}
		}
	}

	private static void performEmergencyCacheCleanup() {
		addConsoleMessage("Signlink: Emergency cache cleanup at " + getCurrentMemoryUsage() + "KB");

		fileCache.clear();
		fileCacheAccessTimes.clear();

		System.gc();

		addConsoleMessage("Signlink: Memory after cleanup: " + getCurrentMemoryUsage() + "KB");
	}

	private static long getCurrentMemoryUsage() {
		Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) / 1024;
	}

	public static synchronized boolean wavesave(byte[] abyte0, int i) {
		if (i > 0x1e8480) return false;
		if (savereq != null) return false;

		if (getCurrentMemoryUsage() > 280) {
			performPeriodicCacheCleanup();
		}

		wavepos = (wavepos + 1) % 5;
		savelen = i;
		savebuf = abyte0;
		waveplay = true;
		savereq = "sound" + wavepos + ".wav";
		return true;
	}

	public static synchronized void midisave(byte[] abyte0, int i) {
		if (i > 0x1e8480) return;
		if (savereq != null) return;

		if (getCurrentMemoryUsage() > 280) {
			performPeriodicCacheCleanup();
		}

		midipos = (midipos + 1) % 5;
		savelen = i;
		savebuf = abyte0;
		midiplay = true;
		savereq = "jingle" + midipos + ".mid";
	}

	public void run() {
		active = true;
		addConsoleMessage("[CACHE] (signlink) active");
		uid = getuid(findCacheDir());

		int cleanupCounter = 0;

		for (int i = threadliveid; threadliveid == i;) {
			if (socketreq != 0) {
				try {
					socket = new Socket(socketip, socketreq);
				} catch (Exception _ex) {
					socket = null;
				}
				socketreq = 0;
			} else if (threadreq != null) {
				Thread thread = new Thread(threadreq);
				thread.setDaemon(true);
				thread.start();
				thread.setPriority(threadreqpri);
				threadreq = null;
			} else if (dnsreq != null) {
				try {
					dns = InetAddress.getByName(dnsreq).getHostName();
				} catch (Exception _ex) {
					dns = "unknown";
				}
				dnsreq = null;
			} else if (savereq != null) {
				if (savebuf != null) {
					try {
						FileOutputStream fileoutputstream = new FileOutputStream(findCacheDir() + savereq);
						fileoutputstream.write(savebuf, 0, savelen);
						fileoutputstream.close();
						totalBytesProcessed += savelen;
					} catch (Exception _ex) {
						throw new RuntimeException(_ex);
					}
				}
				if (waveplay) {
					waveplay = false;
				}
				if (midiplay) {
					midi = findCacheDir() + savereq;
					midiplay = false;
				}
				savereq = null;
			}

			cleanupCounter++;
			if (cleanupCounter % 100 == 0) {
				performPeriodicCacheCleanup();
			}

			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}
		}
	}

	private static int getuid(String s) {
		String jsonPath = s + "uid.json";
		String oldPath = s + "uid.dat";

		// Migrate old binary uid.dat → uid.json
		File oldFile = new File(oldPath);
		File jsonFile = new File(jsonPath);
		if (!jsonFile.exists() && oldFile.exists() && oldFile.length() >= 4L) {
			try {
				DataInputStream din = new DataInputStream(Files.newInputStream(oldFile.toPath()));
				int oldUid = din.readInt();
				din.close();
				Files.write(Paths.get(jsonPath), ("{\"uid\":" + oldUid + "}").getBytes(java.nio.charset.StandardCharsets.UTF_8));
				oldFile.delete();
				addConsoleMessage("[CACHE] Migrated uid.dat -> uid.json");
			} catch (Exception e) {
				// fall through to generate new
			}
		}

		// Generate new uid.json if missing
		if (!jsonFile.exists()) {
			try {
				int newUid = (int) (Math.random() * 99999999D);
				Files.write(Paths.get(jsonPath), ("{\"uid\":" + newUid + "}").getBytes(java.nio.charset.StandardCharsets.UTF_8));
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}
		}

		// Read uid.json
		try {
			String content = new String(Files.readAllBytes(Paths.get(jsonPath)), java.nio.charset.StandardCharsets.UTF_8);
			com.google.gson.JsonObject obj = new com.google.gson.Gson().fromJson(content, com.google.gson.JsonObject.class);
			return obj.get("uid").getAsInt() + 1;
		} catch (Exception _ex) {
			return 0;
		}
	}

	public static synchronized Socket opensocket(int i) throws IOException {
		for (socketreq = i; socketreq != 0;)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}

		if (socket == null)
			throw new IOException("could not open socket");
		else
			return socket;
	}

	public static synchronized DataInputStream openurl(String s) throws IOException {
		for (urlreq = s; urlreq != null;)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}

		if (urlstream == null)
			throw new IOException("could not open: " + s);
		else
			return urlstream;
	}

	public static synchronized void dnslookup(String s) {
		dns = s;
		dnsreq = s;
	}

	public static synchronized void startthread(Runnable runnable, int i) {
		threadreqpri = i;
		threadreq = runnable;
	}

	public static synchronized boolean wavereplay() {
		if (savereq != null) {
			return false;
		} else {
			savebuf = null;
			waveplay = true;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
	}

	public static void reporterror(String s) {
		addConsoleMessage("Error: " + s);
	}

	public static String getCacheStatistics() {
		long hitRate = (cacheHits + cacheMisses) > 0 ?
			(cacheHits * 100) / (cacheHits + cacheMisses) : 0;

		return String.format("Signlink Cache - Files: %d, Bytes: %dKB, Hits: %d, Misses: %d, " +
				"Hit Rate: %d%%, Cached: %d, Memory: %dKB",
			totalFilesProcessed, totalBytesProcessed / 1024,
			cacheHits, cacheMisses, hitRate,
			fileCache.size(), getCurrentMemoryUsage());
	}

	public static void forceCleanup() {
		performEmergencyCacheCleanup();
		addConsoleMessage("Signlink: Forced cleanup completed");
	}

	public static void clearCacheDir() {
		cachedCacheDir = null;
		addConsoleMessage("Signlink: Cache directory cleared, will be recalculated on next access");
	}

	private static byte[] readAllBytesCompat(InputStream inputStream) throws IOException {
		try {

			byte[] buffer = new byte[8192];
			int bytesRead;
			java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			return outputStream.toByteArray();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
}
