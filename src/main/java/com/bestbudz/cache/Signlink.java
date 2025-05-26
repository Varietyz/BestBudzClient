package com.bestbudz.cache;

import com.bestbudz.engine.Client;
import com.bestbudz.config.Configuration;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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

public final class Signlink implements Runnable {

	public static final int clientversion = 317;
	public static final RandomAccessFile[] cache_idx = new RandomAccessFile[6];
	public static int uid;
	public static int storeid = 32;
	public static RandomAccessFile cache_dat = null;
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
	private Signlink() {
	}

	public static void startpriv(InetAddress inetaddress) {
		threadliveid = (int) (Math.random() * 99999999D);
		if (active) {
			try {
				Thread.sleep(500L);
			} catch (Exception _ex)
			{
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
			} catch (Exception _ex)
			{
				throw new RuntimeException(_ex);
			}
		}
	}

	public static String findCacheDir()
	{
		Path cacheDir = Paths.get(System.getProperty("user.home"), ".BestBudzCache");
		String theme = Configuration.cacheTheme ? "runelite" : "bestbudz";

		try
		{
			Files.createDirectories(cacheDir);
			Set<Path> expected = new HashSet<>(syncOnce("/caches/fixed", cacheDir));
			expected.addAll(syncOnce("/caches/" + theme, cacheDir));
			deleteStale(cacheDir, expected);
		}
		catch (IOException | URISyntaxException ex)
		{
			throw new RuntimeException("Failed to synchronise cache", ex);
		}
		return cacheDir.toAbsolutePath() + File.separator;
	}

	private static Set<Path> syncOnce(String resourceRoot, Path targetDir)
		throws IOException, URISyntaxException
	{

		Set<Path> embedded = new HashSet<>();
		String root = resourceRoot.startsWith("/") ? resourceRoot.substring(1)
			: resourceRoot;
		URL url = Client.class.getResource('/' + root + '/');
		if (url != null && "file".equals(url.getProtocol()))
		{
			Path rootPath = Paths.get(url.toURI());
			try (Stream<Path> s = Files.walk(rootPath))
			{
				s.filter(Files::isRegularFile).forEach(src -> {
					Path rel = rootPath.relativize(src);
					embedded.add(rel);
					copyIfMissing(src, targetDir.resolve(rel));
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

		try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8")))
		{
			Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements())
			{
				JarEntry je = e.nextElement();
				if (je.isDirectory()) continue;

				String name = je.getName();
				if (!name.startsWith(root + '/')) continue;

				Path rel = Paths.get(root).relativize(Paths.get(name));
				embedded.add(rel);

				Path dest = targetDir.resolve(rel);
				if (Files.notExists(dest))
				{
					Files.createDirectories(dest.getParent());
					try (InputStream in = jar.getInputStream(je))
					{
						Files.copy(in, dest);
					}
					System.out.println("[CACHE] + " + rel);
				}
			}
		}
		return embedded;
	}

	private static void deleteStale(Path cacheDir, Set<Path> keep) throws IOException
	{
		try (Stream<Path> s = Files.walk(cacheDir))
		{
			s.filter(Files::isRegularFile).forEach(p -> {
				Path rel = cacheDir.relativize(p);
				if (!keep.contains(rel))
				{
					try
					{
						Files.deleteIfExists(p);
					}
					catch (IOException ignored)
					{
					}
				}
			});
		}
	}

	private static void copyIfMissing(Path src, Path dest)
	{
		if (Files.notExists(dest))
		{
			try
			{
				Files.createDirectories(dest.getParent());
				Files.copy(src, dest);
				System.out.println("[COPIED-CACHE] Was missing " + dest.getFileName());
			}
			catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}
	}

	private static int getuid(String s) {
		try {
			File file = new File(s + "uid.dat");
			if (!file.exists() || file.length() < 4L) {
				DataOutputStream dataoutputstream = new DataOutputStream(Files.newOutputStream(Paths.get(s + "uid.dat")));
				dataoutputstream.writeInt((int) (Math.random() * 99999999D));
				dataoutputstream.close();
			}
		} catch (Exception _ex)
		{
			throw new RuntimeException(_ex);
		}
		try {
			DataInputStream datainputstream = new DataInputStream(Files.newInputStream(Paths.get(s + "uid.dat")));
			int i = datainputstream.readInt();
			datainputstream.close();
			return i + 1;
		} catch (Exception _ex) {
			return 0;
		}
	}

	public static synchronized Socket opensocket(int i) throws IOException {
		for (socketreq = i; socketreq != 0;)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex)
			{
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
			} catch (Exception _ex)
			{
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

	public static synchronized boolean wavesave(byte[] abyte0, int i) {
		if (i > 0x1e8480)
			return false;
		if (savereq != null) {
			return false;
		} else {
			wavepos = (wavepos + 1) % 5;
			savelen = i;
			savebuf = abyte0;
			waveplay = true;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
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

	public static synchronized void midisave(byte[] abyte0, int i) {
		if (i > 0x1e8480)
			return;
		if (savereq != null) {
		} else {
			midipos = (midipos + 1) % 5;
			savelen = i;
			savebuf = abyte0;
			midiplay = true;
			savereq = "jingle" + midipos + ".mid";
		}
	}

	public static void reporterror(String s) {
		System.out.println("Error: " + s);
	}

	public void run()
	{

		active = true;
		System.out.println("[CACHE] (signlink) active – opening cache files");
		uid = getuid(findCacheDir());
		try
		{
			cache_dat = new RandomAccessFile(findCacheDir() + "main_file_cache.dat", "rw");
			for (int i = 0; i < cache_idx.length; i++)
			{
				cache_idx[i] = new RandomAccessFile(
					findCacheDir() + "main_file_cache.idx" + i, "rw"
				);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		for (int i = threadliveid; threadliveid == i; )
		{
			if (socketreq != 0)
			{
				try
				{
					socket = new Socket(socketip, socketreq);
				}
				catch (Exception _ex)
				{
					socket = null;
				}
				socketreq = 0;
			}
			else if (threadreq != null)
			{
				Thread thread = new Thread(threadreq);
				thread.setDaemon(true);
				thread.start();
				thread.setPriority(threadreqpri);
				threadreq = null;
			}
			else if (dnsreq != null)
			{
				try
				{
					dns = InetAddress.getByName(dnsreq).getHostName();
				}
				catch (Exception _ex)
				{
					dns = "unknown";
				}
				dnsreq = null;
			}
			else if (savereq != null)
			{
				if (savebuf != null)
					try
					{
						FileOutputStream fileoutputstream = new FileOutputStream(findCacheDir() + savereq);
						fileoutputstream.write(savebuf, 0, savelen);
						fileoutputstream.close();
					}
					catch (Exception _ex)
					{
						throw new RuntimeException(_ex);
					}
				if (waveplay)
				{
					waveplay = false;
				}
				if (midiplay)
				{
					midi = findCacheDir() + savereq;
					midiplay = false;
				}
				savereq = null;
			}
			try
			{
				Thread.sleep(50L);
			}
			catch (Exception _ex)
			{
				throw new RuntimeException(_ex);
			}
		}
	}
}