package se.roland.crypto;

import java.io.Serializable;
import java.security.KeyPair;

public class KeySave implements Serializable {
    public KeyPair keyPair;
    public KeySave(KeyPair keyPair){
        this.keyPair = keyPair;
    }
    public KeySave(){

    };

}
