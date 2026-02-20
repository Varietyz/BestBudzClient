package com.bestbudz.dock.ui.panel.bubblebudz.system.effects;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EffectLoader
{
	private static final String EFFECTS_PACKAGE = "com.bestbudz.dock.ui.panel.bubblebudz.system.effects.types";
	private static final String EFFECTS_PATH = "com/bestbudz/dock/ui/panel/client/bubblebudz/system/effects/types";

	public static void loadAllEffects(EffectManager effectManager) {
		System.out.println("Starting effect loading...");
		try {
			Set<String> effectDirectories = findEffectDirectories();
			System.out.println("Found effect directories: " + effectDirectories);

			for (String effectDir : effectDirectories) {
				System.out.println("Loading effect from directory: " + effectDir);
				loadEffectFromDirectory(effectManager, effectDir);
			}

			System.out.println("Loaded " + effectDirectories.size() + " effect types: " + effectDirectories);
		} catch (Exception e) {
			System.err.println("Failed to load effects: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static Set<String> findEffectDirectories() throws Exception {
		Set<String> directories = new HashSet<>();

		ClassLoader classLoader = EffectLoader.class.getClassLoader();
		URL effectsUrl = classLoader.getResource(EFFECTS_PATH);

		System.out.println("Looking for effects at: " + EFFECTS_PATH);
		System.out.println("Effects URL: " + effectsUrl);

		if (effectsUrl == null) {
			System.err.println("Effects package not found: " + EFFECTS_PATH);
			return directories;
		}

		String protocol = effectsUrl.getProtocol();
		System.out.println("URL protocol: " + protocol);

		if ("file".equals(protocol)) {

			findDirectoriesInFileSystem(new File(effectsUrl.toURI()), directories);
		} else if ("jar".equals(protocol)) {

			findDirectoriesInJar(effectsUrl, directories);
		}

		return directories;
	}

	private static void findDirectoriesInFileSystem(File effectsDir, Set<String> directories) {
		System.out.println("Scanning filesystem directory: " + effectsDir.getAbsolutePath());

		if (!effectsDir.exists() || !effectsDir.isDirectory()) {
			System.err.println("Effects directory does not exist or is not a directory: " + effectsDir);
			return;
		}

		File[] subdirs = effectsDir.listFiles(File::isDirectory);
		System.out.println("Found subdirectories: " + (subdirs != null ? subdirs.length : 0));

		if (subdirs != null) {
			for (File subdir : subdirs) {
				String dirName = subdir.getName().toLowerCase();
				System.out.println("Checking subdirectory: " + dirName + " (" + subdir.getAbsolutePath() + ")");

				if (hasFactoryClass(subdir, dirName)) {
					directories.add(dirName);
					System.out.println("Added effect directory: " + dirName);
				} else {
					System.out.println("No factory class found for: " + dirName);
				}
			}
		}
	}

	private static void findDirectoriesInJar(URL jarUrl, Set<String> directories) throws Exception {
		String jarPath = jarUrl.getPath();
		String jarFile = jarPath.substring(5, jarPath.indexOf("!"));

		try (JarFile jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			Set<String> foundDirs = new HashSet<>();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();

				if (entryName.startsWith(EFFECTS_PATH + "/") && !entry.isDirectory()) {
					String relativePath = entryName.substring(EFFECTS_PATH.length() + 1);
					int slashIndex = relativePath.indexOf('/');

					if (slashIndex > 0) {
						String dirName = relativePath.substring(0, slashIndex).toLowerCase();
						foundDirs.add(dirName);
					}
				}
			}

			for (String dirName : foundDirs) {
				if (hasFactoryClassInJar(jar, dirName)) {
					directories.add(dirName);
				}
			}
		}
	}

	private static boolean hasFactoryClass(File directory, String effectName) {
		String expectedFactoryName = capitalizeFirst(effectName) + "Factory.java";
		File factoryFile = new File(directory, expectedFactoryName);
		System.out.println("Looking for factory file: " + factoryFile.getAbsolutePath() + " (exists: " + factoryFile.exists() + ")");

		String expectedFactoryClass = capitalizeFirst(effectName) + "Factory.class";
		File factoryClassFile = new File(directory, expectedFactoryClass);
		System.out.println("Looking for factory class: " + factoryClassFile.getAbsolutePath() + " (exists: " + factoryClassFile.exists() + ")");

		return factoryFile.exists() || factoryClassFile.exists();
	}

	private static boolean hasFactoryClassInJar(JarFile jar, String effectName) {
		String expectedFactoryPath = EFFECTS_PATH + "/" + effectName + "/" + capitalizeFirst(effectName) + "Factory.class";
		return jar.getEntry(expectedFactoryPath) != null;
	}

	private static void loadEffectFromDirectory(EffectManager effectManager, String effectName) {
		try {
			String factoryClassName = EFFECTS_PACKAGE + "." + effectName + "." + capitalizeFirst(effectName) + "Factory";

			Class<?> factoryClass = Class.forName(factoryClassName);

			if (EffectFactory.class.isAssignableFrom(factoryClass)) {
				EffectFactory factory = (EffectFactory) factoryClass.getDeclaredConstructor().newInstance();
				effectManager.registerEffectType(effectName, factory);
				System.out.println("Registered effect: " + effectName + " using " + factoryClassName);
			} else {
				System.err.println("Class " + factoryClassName + " does not implement EffectFactory");
			}
		} catch (Exception e) {
			System.err.println("Failed to load effect '" + effectName + "': " + e.getMessage());
		}
	}

	private static String capitalizeFirst(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}
}
