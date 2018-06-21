package uk.gov.dvla.osg.rpd.config.gui;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A utility class that encrypts or decrypts a file.
 */
public class Cryptifier {

	static final Logger LOGGER = LogManager.getLogger();
	
    private static final String ALGORITHM = "AES";
    // uses a single key for encryption and decryption
    private static final String KEY = "FUD2MYY6OQNSDHFE";

    /**
     * @param inputBytes message to encrypt
     * @return encrypted message
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */
    public static byte[] encrypt(byte[] inputBytes) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return doCrypto(inputBytes, Cipher.ENCRYPT_MODE);
    }

    /**
     * @param inputBytes message to decrypt
     * @return decrypted message as plain text
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */
    public static byte[] decrypt(byte[] inputBytes) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return doCrypto(inputBytes, Cipher.DECRYPT_MODE);
    }

    /**
     * @param inputBytes message to transform
     * @param encryptMode type of transformation needed
     * @return transformed message
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidAlgorithmParameterException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
    private static byte[] doCrypto(byte[] inputBytes, int encryptMode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(encryptMode, secretKey, ivParams);
        
        return cipher.doFinal(inputBytes);
    }
}