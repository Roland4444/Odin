package se.roland.example;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

public class ExampleReflections {
    private static final JHOF J = new JHOF();
    public static void main(String[] args) {
        ArrayList arr = new ArrayList();
        arr.add("s");
        arr.add("S");
        arr.add("s");
        arr.add("S");
        arr.add("S");
        arr.add("S");
        arr.add("S");
        J.generate(arr);
        Reflections reflections = new Reflections("se.roland");
        Set<Class<?>> SuperMS = reflections
                .getTypesAnnotatedWith(SuperM.class);
        var counter = 0;
        for (Class<?> clazz : SuperMS)
            System.out.println(clazz);
        System.out.println("counter::" + counter);
    }
}