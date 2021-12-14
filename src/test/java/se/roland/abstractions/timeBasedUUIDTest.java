package se.roland.abstractions;

import junit.framework.TestCase;

public class timeBasedUUIDTest extends TestCase {

    public void testGenerate() {
        System.out.println(timeBasedUUID.generate());
    }
}