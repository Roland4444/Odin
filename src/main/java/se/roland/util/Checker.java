package se.roland.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Checker implements Serializable {
    public Map<Character, Boolean> dictionary = new HashMap<>();

    public Checker(){
        initDict();
    }


    public boolean isnumber(String input){
        if (input.length()<1)
            return false;
        for (int i=0; i<input.length();i++){
            if (dictionary.get(input.charAt(i)) == null)
                return false;
        }
        return true;
    }


    public void initDict(){
        this.dictionary.put('.', true);
        this.dictionary.put('0', true);
        this.dictionary.put('1', true);
        this.dictionary.put('2', true);
        this.dictionary.put('3', true);
        this.dictionary.put('4', true);
        this.dictionary.put('5', true);
        this.dictionary.put('6', true);
        this.dictionary.put('7', true);
        this.dictionary.put('8', true);
        this.dictionary.put('9', true);


    }
}
