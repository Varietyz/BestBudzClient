package com.bestbudz.cache;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class IdentityResolver {

  private static final Gson GSON = new Gson();

  private static List<File> listAvailableStores() {
    String cacheDirectory = Signlink.findCacheDir();
    String operativeSystem = System.getProperty("os.name");
    List<File> result = new ArrayList<>();
    result.add(new File(cacheDirectory, "venran.json"));
    result.add(new File(System.getProperty("user.home"), "venkey.json"));

    if (operativeSystem != null) {
      operativeSystem = operativeSystem.toLowerCase();

      if (operativeSystem.contains("win")) {
        result.add(new File(System.getenv("APPDATA") + "/BestBudzCache/", "venran.json"));
      }
    }

    for (File file : result) {
      File root = file.getParentFile();

      if (root.exists() && !root.isDirectory()) {
        root.delete();
      }

      if (!root.exists()) {
        root.mkdirs();
      }

      if (file.exists() && !file.canRead()) {
        file.setReadable(true);
      }

      if (file.exists() && !file.canWrite()) {
        file.setWritable(true);
      }
    }

    return Collections.unmodifiableList(result);
  }

  private static long readKey(File file) throws IOException {
    String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    JsonObject obj = GSON.fromJson(content, JsonObject.class);
    return obj.get("key").getAsLong();
  }

  private static void writeKey(File file, long key) throws IOException {
    Files.write(file.toPath(), ("{\"key\":" + key + "}").getBytes(StandardCharsets.UTF_8));
  }

  public static long resolve() {
    List<File> files = listAvailableStores();
    migrateOldBinaryFiles();

    long key;
    File oldest = null;

	  for (File file : files)
	  {
		  if (file.exists())
		  {
			  if ((oldest == null) || (oldest.lastModified() > file.lastModified()))
			  {
				  oldest = file;
			  }
		  }
	  }

    if (oldest != null) {
      try {
        key = readKey(oldest);
      } catch (Exception ex) {
        key = new SecureRandom().nextLong();
      }
    } else {
      key = new SecureRandom().nextLong();
    }

    for (File file : files) {
      boolean write = true;

      if (file.exists()) {
        try {
          long l = readKey(file);

          if (l == key) {
            write = false;
          }
        } catch (Exception ex) {
          // corrupt file, rewrite
        }
      }

      if (write) {
        try {
          writeKey(file, key);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }

    return key;
  }

  private static void migrateOldBinaryFiles() {
    String[][] oldNewPairs = {
      {Signlink.findCacheDir() + "venran.dat", Signlink.findCacheDir() + "venran.json"},
      {System.getProperty("user.home") + File.separator + "venkey.dat", System.getProperty("user.home") + File.separator + "venkey.json"},
    };
    String os = System.getProperty("os.name");
    if (os != null && os.toLowerCase().contains("win")) {
      oldNewPairs = new String[][] {
        {Signlink.findCacheDir() + "venran.dat", Signlink.findCacheDir() + "venran.json"},
        {System.getProperty("user.home") + File.separator + "venkey.dat", System.getProperty("user.home") + File.separator + "venkey.json"},
        {System.getenv("APPDATA") + "/BestBudzCache/venran.dat", System.getenv("APPDATA") + "/BestBudzCache/venran.json"},
      };
    }

    for (String[] pair : oldNewPairs) {
      File oldFile = new File(pair[0]);
      File newFile = new File(pair[1]);
      if (oldFile.exists() && !newFile.exists() && oldFile.length() == 8) {
        try (DataInputStream in = new DataInputStream(Files.newInputStream(oldFile.toPath()))) {
          long oldKey = in.readLong();
          writeKey(newFile, oldKey);
          oldFile.delete();
          System.out.println("Migrated " + oldFile.getName() + " -> " + newFile.getName());
        } catch (Exception e) {
          System.err.println("Failed to migrate " + oldFile.getName() + ": " + e.getMessage());
        }
      }
    }
  }
}
