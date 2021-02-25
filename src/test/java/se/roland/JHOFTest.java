package se.roland;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;

public class JHOFTest extends TestCase {

    public void testCallmarina() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertNotNull(JHOF.callmarina());
        System.out.println(JHOF.callmarina());
    }
}