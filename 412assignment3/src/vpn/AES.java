package vpn;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	static String IV = "AAAAAAAAAAAAAAAA";
	static String plaintext = "test text 123\0\0\0"; /* Note null padding */
	static String encryptionKey = "0123456789abcdef";

	// Encrypts plaintext into cihertext
	public static byte[] encrypt(String in_plain, String in_key)
			throws Exception {
		String plainText = pad(in_plain);
		String encryptionKey = pad(in_key);
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"),
				"AES");
		cipher.init(Cipher.ENCRYPT_MODE, key,
				new IvParameterSpec(IV.getBytes("UTF-8")));
		return cipher.doFinal(plainText.getBytes("UTF-8"));
	}

	// Decrypts ciphertext into plaintext
	public static String decrypt(byte[] cipherText, String in_key)
			throws Exception {
		String encryptionKey = pad(in_key);
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"),
				"AES");
		cipher.init(Cipher.DECRYPT_MODE, key,
				new IvParameterSpec(IV.getBytes("UTF-8")));
		return new String(cipher.doFinal(cipherText), "UTF-8");
	}
	
	// Pads a string so that it contains a multiple of 16 characters
	public static String pad(String s){
		String result = s;
		while(result.length() % 16 != 0)
		{
			result = result + '\0';
		}
		return result;
	}
}