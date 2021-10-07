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
        result = switch (input){
            case  '0' -> true;
            case  '1' -> true;
            case  '2' -> true;
            case  '3' -> true;
            case  '4' -> true;
            case  '5' -> true;
            case  '6' -> true;
            case  '7' -> true;
            case  '8' -> true;
            case  '9' -> true;
            default -> false;
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
