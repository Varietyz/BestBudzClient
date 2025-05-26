package com.bestbudz.util;

import com.bestbudz.cache.Signlink;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtility {

  public static byte[] readFile(String name) {
    File f = new File(name);
    System.out.println(">>> user.home    = " + System.getProperty("user.home"));
    System.out.println(">>> user.dir     = " + System.getProperty("user.dir"));
    System.out.println(">>> findCacheDir = " + Signlink.findCacheDir());
    System.out.println(">>> fullPath     = " + f.getAbsolutePath() + "    | exists? " + f.exists());
    try {
      RandomAccessFile raf = new RandomAccessFile(name, "r");
      ByteBuffer buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
      try {
        if (buf.hasArray()) {
          return buf.array();
        } else {
          byte[] array = new byte[buf.remaining()];
          buf.get(array);
          return array;
        }
      } finally {
        raf.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
