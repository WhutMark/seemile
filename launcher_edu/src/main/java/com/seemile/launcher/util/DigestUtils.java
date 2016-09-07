package com.seemile.launcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class DigestUtils {
	private static final char HEX_DIGETS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String getMD5(String source) {

		try {
			return getMD5(source.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}

		return "";
	}

	public static String getMD5(byte[] source) {
		String s = null;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2];
			int k = 0;

			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = HEX_DIGETS[(byte0 & 0xf0) >> 4];

				str[k++] = HEX_DIGETS[byte0 & 0xf];
			}

			s = new String(str);

		} catch (Exception e) {
		}

		return s;
	}

	public static String getMD5(File file) {
		String result = "";
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			result = bufferToHex(complete.digest());

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = HEX_DIGETS[(bt & 0xf0) >> 4];
		char c1 = HEX_DIGETS[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

}
