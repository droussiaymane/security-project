package com.kotak.mb2.admin.administration.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

//import sun.misc.BASE64Encoder;

public class EncryptDecrypt {

	private static final String characterEncoding = "UTF-8";
	private static final String cipherTransformation = "AES/CBC/PKCS5Padding";
	private static final String aesEncryptionAlgorithm = "AES";

	private static final byte[] keyValue1 = new byte[] { 'A', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' };
	public static String secretByteKeys = "6b62616e6b617070";

	public static byte[] decrypt(byte[] cipherText, byte[] key,
								 byte[] initialVector) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpecy = new SecretKeySpec(key,
				aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
		cipherText = cipher.doFinal(cipherText);
		return cipherText;
	}

	public static byte[] encrypt(byte[] plainText, byte[] key,
								 byte[] initialVector) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key,
				aesEncryptionAlgorithm);

		// System.out.println(new String(Base64.encodeBase64(key)));
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);

		return plainText;
	}

	private static byte[] getKeyBytes(String key)
			throws UnsupportedEncodingException {
		byte[] keyBytes = new byte[16];
		byte[] parameterKeyBytes = key.getBytes(characterEncoding);
		System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
				Math.min(parameterKeyBytes.length, keyBytes.length));
		return keyBytes;
	}

	public static String encrypt(String plainText, String key) {
		try {
			byte[] plainTextbytes = plainText.getBytes(characterEncoding);
			byte[] keyBytes = getKeyBytes(key);
			byte[] encodedBytes = Base64.encodeBase64(encrypt(plainTextbytes,
					keyBytes, keyBytes));
			return new String(encodedBytes);
		} catch (Exception e) {
			return "";
		}
	}

	public static String encryptnew(String Data, String pass) throws Exception {
		Key key = new SecretKeySpec(getKeyBytes(pass), aesEncryptionAlgorithm);
		// System.out.println(new String(new BASE64Encoder()
		//		.encode(getKeyBytes(pass))));
		Cipher c = Cipher.getInstance(cipherTransformation);

		IvParameterSpec ivParameterSpec = new IvParameterSpec(getKeyBytes(pass));
		c.init(Cipher.ENCRYPT_MODE, key,ivParameterSpec);
		byte[] encVal = c.doFinal(Data.getBytes());
		// String encryptedValue = new BASE64Encoder().encode(encVal);
		return new String(Base64.encodeBase64(encVal));
	}

	public static String decryptText(String key, String encryptedText) {
		try {

			byte[] cipheredBytes = Base64.decodeBase64(encryptedText
					.getBytes(characterEncoding));
			byte[] keyBytes = getKeyBytes(key);
			return new String(decrypt(cipheredBytes, keyBytes, keyBytes),
					characterEncoding);
		} catch (Exception e) {
			return "";
		}
	}

	public static String encryptNEncodeCBC(String key, String message)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (message == null || key == null) {
			throw new IllegalArgumentException("Text to be encrypted and key should not be null");
		}

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		byte[] messageArr = message.getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(java.util.Base64.getDecoder().decode(key), "AES");
		byte[] ivParams = getIV().getBytes();
		byte[] finalMsg = new byte[messageArr.length + 16];
		// copy iv to final message (0-16 characters)
		System.arraycopy(ivParams, 0, finalMsg, 0, 16);
		// append message to final message (16-size of message)
		System.arraycopy(messageArr, 0, finalMsg, 16, messageArr.length);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ivParams));
		// encrypt final message
		byte[] encryptedBytes = cipher.doFinal(finalMsg);
		// base64 encode
		encryptedBytes = java.util.Base64.getEncoder().encode(encryptedBytes);
		return new String(encryptedBytes);
	}

	private static String getIV() {
		SecureRandom rand = new SecureRandom();
		long randLong = rand.nextLong();
		String iv4 = "" + randLong;
		if (iv4.length() < 16) {
			iv4 = String.format("%1$" + (16 - iv4.length()) + "s", iv4);
		} else if (iv4.length() > 16) {
			iv4 = iv4.substring(0, 16);
		}

		return iv4;
	}

	public static String encryptECB(String data, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] skey = key.getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(skey, "AES");
		// Initialize the cipher for encryption
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec);
		// data to be encrypted
		byte[] text = data.getBytes();
		// Encrypt the data
		byte[] textEncrypted = c.doFinal(text);
		return java.util.Base64.getEncoder().encodeToString(textEncrypted);
	}

}
