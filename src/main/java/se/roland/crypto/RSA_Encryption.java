package se.roland.crypto;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSA_Encryption
{
    public  static String plainText = "Plain text which need to be encrypted by Java RSA Encryption in ECB Mode";
    public static final String saveToFile = "key.bin";
    public static void main(String[] args) throws Exception
    {
        // Get an instance of the RSA key generator
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        // Generate the KeyPair
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        KeySave KS = new KeySave(keyPair);
        if (!new File(saveToFile).exists())
            Saver.Saver.write(Saver.Saver.savedToBLOB(KS), saveToFile);
        // Get the public and private key
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        if (new File(saveToFile).exists()){
            KS = (KeySave) Saver.Saver.restored(Saver.Saver.readBytes(saveToFile));
            publicKey = KS.keyPair.getPublic();
            privateKey = KS.keyPair.getPrivate();
            System.out.println("RESTORED!!");
        }
        System.out.println("PUBLCI KEY::"+ publicKey.toString());

        System.out.println("Original Text  : "+plainText);

        // Encryption
        byte[] cipherTextArray = encrypt(plainText, publicKey);
        String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
        System.out.println("Encrypted Text : "+encryptedText);

        // Decryption
        String decryptedText = decrypt(cipherTextArray, privateKey);
        System.out.println("DeCrypted Text : "+decryptedText);
    }

    public static byte[] encrypt (String plainText,PublicKey publicKey ) throws Exception
    {
        //Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING Padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plainText.getBytes()) ;

        return cipherText;
    }

    public static String decrypt (byte[] cipherTextArray, PrivateKey privateKey) throws Exception
    {
        //Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING Padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        //Perform Decryption
        byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);

        return new String(decryptedTextArray);
    }
}