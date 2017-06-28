package com.minxing.mxcircledemotest;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AESUtil {
	public static final String TAG = "AESUtils";
	private static final String IV = "a0fe7c7c98e09e8c";

	public static String encrypt(String key, String clearText) {
		byte[] result = null;
		try {
			result = encrypt(toByte(MD5Util.getMD5String(MD5Util.getMD5String(key))), clearText.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toHex(result);
	}

	public static String getRawKey() {
		try {
			String seed = System.currentTimeMillis() + "";
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(toByte(seed));
			kgen.init(128, sr); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			return Base64.encodeToString(raw, Base64.NO_WRAP);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(IV.getBytes()));
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] toByte(String hexString) {
//		System.out.println(">>>>>>>>>>>>>>>>>>>" + hexString);
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
//			System.out.println(">>>>>>>>>>>>>>>>>>>" + result[i]);
		}
		return result;
	}

	private static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private static void appendHex(StringBuffer sb, byte b) {
		final String HEX = "0123456789abcdef";
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}
	
	
}
