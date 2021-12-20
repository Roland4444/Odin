package se.roland.abstractions;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

public class timeBasedUUID {

    public static String generate() {
        NoArgGenerator timeBasedGenerator = Generators.timeBasedGenerator();
        return timeBasedGenerator.generate().toString();
    }
    public static int generateInt(){
        return (int) Math.round(Math.random()*10000);
    }

}