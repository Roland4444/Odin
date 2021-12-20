package se.roland.crypto;



import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Gost3411Hash {
    public Gost3411Hash(){

    }

    public static byte[] hash_byte(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance( "sha-256" );
        md.update( data.getBytes( StandardCharsets.UTF_8 ) );
        byte[] digest = md.digest();
        return digest;
    }

    public static byte[] getBytesFromBase64(String input){
        return Base64.getDecoder().decode(input);
    }

    public static void main(String[] args){

    }



}


