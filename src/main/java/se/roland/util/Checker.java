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

    
    public static boolean checkdigit(char input){
        var result = false;
        switch (input){
            case  '0' : result = true; break;
            case  '1' : result = true; break;
            case  '2' : result = true; break;
            case  '3' : result = true; break;
            case  '4' : result = true; break;
            case  '5' : result = true; break;
            case  '6' : result = true; break;
            case  '7' : result = true; break;
            case  '8' : result = true; break;
            case  '9' : result = true; break;
            default : result   = false;  break;
        };
        return result;
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
