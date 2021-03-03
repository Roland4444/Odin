package se.roland;

import junit.framework.TestCase;
import se.roland.example.JHOF;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.Assert.assertNotEquals;

public class JHOFTest extends TestCase {

    public void testCallmarina() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertNotNull(JHOF.callmarina());
        System.out.println(JHOF.callmarina());
    }

    public void testResult() {
        ArrayList arr = new ArrayList();
        arr.add("s");
        arr.add("S");
        arr.add("s");
        assertNotEquals(null, new JHOF().generate(arr));
        assertEquals(3, new JHOF().generate(arr).size());

    }


}