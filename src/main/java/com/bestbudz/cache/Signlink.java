package com.bestbudz.cache;

import static com.bestbudz.engine.core.LoadingErrorScreen.addConsoleMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Signlink implements Runnable {

	public static final int clientversion = 317;
	public static int uid;
	public static int storeid = 32;
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
	private static int savelen;
	private static String savereq = null;
	private static byte[] savebuf = null;
	private static boolean midiplay;
	private static int midipos;
	private static boolean waveplay;
	private static int wavepos;

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

		Path cacheDir = Paths.get(System.getProperty("user.home"), ".BestBudzCache");
		try {
			Files.createDirectories(cacheDir);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to create user data directory", ex);
		}

		cachedCacheDir = cacheDir.toAbsolutePath() + File.separator;
		return cachedCacheDir;
	}

	public static synchronized boolean wavesave(byte[] abyte0, int i) {
		if (i > 0x1e8480) return false;
		if (savereq != null) return false;

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

		midipos = (midipos + 1) % 5;
		savelen = i;
		savebuf = abyte0;
		midiplay = true;
		savereq = "jingle" + midipos + ".mid";
	}

	public void run() {
		active = true;
		addConsoleMessage("[SIGNLINK] active");
		uid = getuid(findCacheDir());

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

			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}
		}
	}

	private static int getuid(String s) {
		String jsonPath = s + "uid.json";
		File jsonFile = new File(jsonPath);

		if (!jsonFile.exists()) {
			try {
				int newUid = (int) (Math.random() * 99999999D);
				Files.write(Paths.get(jsonPath), ("{\"uid\":" + newUid + "}").getBytes(java.nio.charset.StandardCharsets.UTF_8));
			} catch (Exception _ex) {
				throw new RuntimeException(_ex);
			}
		}

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
}
