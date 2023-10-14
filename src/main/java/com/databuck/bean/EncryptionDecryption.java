/* package com.databuck.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;




@Component
public class EncryptionDecryption 
{
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    
    public static String encrypt(String value) 
    {
        Key key = generateKey();
        Cipher cipher;
		try {
			cipher = Cipher.getInstance(EncryptionDecryption.ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
	        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
	        String encryptedValue64 = new BASE64Encoder().encode(encryptedByteValue);
	        return encryptedValue64;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 return null;
		}
		
		 
               
    }
    
    public static String decrypt(String value) 
    {
        Key key = generateKey();
        Cipher cipher;
		try {
			cipher = Cipher.getInstance(EncryptionDecryption.ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
	        byte [] decryptedValue64 = new BASE64Decoder().decodeBuffer(value);
	        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
	        String decryptedValue = new String(decryptedByteValue,"utf-8");
	        return decryptedValue;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        
                
    }
    
    private static Key generateKey() 
    {
        Key key = new SecretKeySpec(EncryptionDecryption.KEY.getBytes(),EncryptionDecryption.ALGORITHM);
        System.out.println("key="+key);
        return key;
    }
    public static void main(String[] args) throws Exception {
		String encrypt = encrypt("shravan");
		System.out.println("encrypt="+encrypt);
		String decrypt = decrypt(encrypt);
		System.out.println("decrypt="+decrypt);
	}
}*/