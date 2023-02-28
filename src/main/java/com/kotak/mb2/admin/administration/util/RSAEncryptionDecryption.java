package com.kotak.mb2.admin.administration.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.kotak.mb2.admin.administration.constants.AppConstants;
import org.apache.commons.codec.binary.Base64;


/**
 * @author sambath
 *
 * public & private key generated using com.msf.kmb.API.RSAkeyGenerator.java
 */
public class RSAEncryptionDecryption {
   // private static Logger log = Logger.getLogger(RSAEncryptionDecryption.class);


    public static String getPublicKey() {
        return AppConstants.PUBLIC_KEY;
    }
    public static String getPrivateKey() {
        return AppConstants.PRIVATE_KEY;
    }

    public static String getPublicKeySimBinding() {
        return AppConstants.PUBLIC_KEY_SIMBINDING;
    }

    public static String getPrivateKeySimBinding() {
        return AppConstants.PRIVATE_KEY_SIMBINDING;
    }

    static Base64 base64 = new Base64();
    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64.decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            //log.error(" Error ---- " + e);
        } catch (InvalidKeySpecException e) {
           // log.error(" Error ---- " + e);
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(base64.decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
           // log.error(" Error ---- " + e);
        }
        try {
            if(keyFactory != null) {
                privateKey = keyFactory.generatePrivate(keySpec);
            }
        } catch (InvalidKeySpecException e) {
           // log.error("Invalid key exception",e);
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static  String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(base64.decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

//    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, JSONException {
//        try {
//
//        	JSONObject js=new JSONObject();
//        	js.put("CRN", "5898333");
//        	js.put("MPIN", "101010");
////        	String js="30001287"+"-"+"c31f19d414a9fbfe566fff1371e4caf7";
////           String encryptedString = Base64.encodeBase64String(encrypt(js, publicKey));
//        	String encryptedString = Base64.encodeBase64String(encrypt(js.toString(), RSAEncryptionDecryption.getPublicKey()));            System.out.println(encryptedString);
////            String decryptedString = RSAEncryptionDecryption.decrypt(encryptedString, privateKey);
//            String decryptedString = RSAEncryptionDecryption.decrypt(encryptedString, RSAEncryptionDecryption.getPrivateKey());
//            System.out.println(decryptedString);
//        } catch (NoSuchAlgorithmException e) {
//            System.err.println(e.getMessage());
//        }
//
//    }
}