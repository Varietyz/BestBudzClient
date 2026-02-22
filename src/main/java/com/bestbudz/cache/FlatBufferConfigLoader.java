package com.bestbudz.cache;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class FlatBufferConfigLoader {

	public static ByteBuffer load(String filename) {
		byte[] data = JsonCacheLoader.loadFileBytes(filename);
		if (data == null) return null;
		ByteBuffer buf = ByteBuffer.wrap(data);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		return buf;
	}
}
