package com.bestbudz.network;

import com.bestbudz.engine.core.ClientEngine;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Socket implements Runnable {

  private final InputStream inputStream;
  private final OutputStream outputStream;
  private final java.net.Socket socket;
  public final ClientEngine engine;
  private boolean closed;
  private byte[] buffer;

  public Socket(ClientEngine Engine, java.net.Socket socket1) throws IOException {
    closed = false;
    isWriter = false;
    hasIOError = false;
    engine = Engine;
    socket = socket1;
    socket.setSoTimeout(30000);
    socket.setTcpNoDelay(true);
    inputStream = socket.getInputStream();
    outputStream = socket.getOutputStream();
  }

  public void close() {
    closed = true;
    try {
      if (inputStream != null) inputStream.close();
      if (outputStream != null) outputStream.close();
      if (socket != null) socket.close();
    } catch (IOException _ex) {
      System.out.println("Error closing stream");
    }
    isWriter = false;
    synchronized (this) {
      notify();
    }
    buffer = null;
  }

  public int read() throws IOException {
    if (closed) return 0;
    else return inputStream.read();
  }

  public int available() throws IOException {
    if (closed) return 0;
    else return inputStream.available();
  }

  public void flushInputStream(byte[] abyte0, int j) throws IOException {
    int i = 0;
    if (closed) return;
    int k;
    for (; j > 0; j -= k) {
      k = inputStream.read(abyte0, i, j);
      if (k <= 0) throw new IOException("EOF");
      i += k;
    }
  }

  /** Write a length-prefixed protobuf GamePacket to the server. */
  public void writeProto(GamePacket packet) throws IOException {
    byte[] data = packet.toByteArray();
    byte[] header = {
      (byte)(data.length >> 24), (byte)(data.length >> 16),
      (byte)(data.length >> 8),  (byte)data.length
    };
    queueBytes(4, header);
    queueBytes(data.length, data);
  }

  /** Read a length-prefixed protobuf GamePacket from the server. */
  public GamePacket readProto() throws IOException {
    byte[] header = new byte[4];
    flushInputStream(header, 4);
    int len = ((header[0] & 0xFF) << 24) | ((header[1] & 0xFF) << 16) |
              ((header[2] & 0xFF) << 8)  |  (header[3] & 0xFF);
    byte[] data = new byte[len];
    flushInputStream(data, len);
    return GamePacket.parseFrom(data);
  }

  /** Check if at least 4 bytes (length prefix) are available for a protobuf read. */
  public boolean hasProtoAvailable() throws IOException {
    return available() >= 4;
  }

  public void queueBytes(int i, byte[] abyte0) throws IOException {
    if (closed) return;
    if (hasIOError) {
      hasIOError = false;
      throw new IOException("Error in writer thread");
    }
    if (buffer == null) buffer = new byte[5000];
    synchronized (this) {
      for (int l = 0; l < i; l++) {
        buffer[buffIndex] = abyte0[l];
        buffIndex = (buffIndex + 1) % 5000;
        if (buffIndex == (writeIndex + 4900) % 5000) throw new IOException("buffer overflow");
      }

      if (!isWriter) {
        isWriter = true;
        engine.startRunnable(this, 3);
      }
      notify();
    }
  }

  public void run() {
    while (isWriter) {
      int i;
      int j;
      synchronized (this) {
        if (buffIndex == writeIndex)
          try {
            wait();
          } catch (InterruptedException _ex)
		  {
			  throw new RuntimeException(_ex);
		  }
        if (!isWriter) return;
        j = writeIndex;
        if (buffIndex >= writeIndex) i = buffIndex - writeIndex;
        else i = 5000 - writeIndex;
      }
      if (i > 0) {
        try {
          outputStream.write(buffer, j, i);
        } catch (IOException _ex) {
          hasIOError = true;
        }
        writeIndex = (writeIndex + i) % 5000;
        try {
          if (buffIndex == writeIndex) outputStream.flush();
        } catch (IOException _ex) {
          hasIOError = true;
        }
      }
    }
  }
  private int writeIndex;
  private int buffIndex;
  private boolean isWriter;
  private boolean hasIOError;
}
