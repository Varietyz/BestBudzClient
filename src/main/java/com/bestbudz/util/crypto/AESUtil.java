package com.bestbudz.util.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public final class AESUtil {
	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int IV_SIZE = 12; // 96 bits
	private static final int TAG_SIZE = 128; // 128 bits

	public static byte[] encrypt(byte[] key, byte[] data) throws Exception {
		byte[] iv = new byte[IV_SIZE];
		new SecureRandom().nextBytes(iv);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_SIZE, iv));
		byte[] encrypted = cipher.doFinal(data);
		byte[] result = new byte[IV_SIZE + encrypted.length];
		System.arraycopy(iv, 0, result, 0, IV_SIZE);
		System.arraycopy(encrypted, 0, result, IV_SIZE, encrypted.length);
		return result;
	}

	public static byte[] decrypt(byte[] key, byte[] data) throws Exception {
		byte[] iv = new byte[IV_SIZE];
		System.arraycopy(data, 0, iv, 0, IV_SIZE);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_SIZE, iv));
		return cipher.doFinal(data, IV_SIZE, data.length - IV_SIZE);
	}
}
