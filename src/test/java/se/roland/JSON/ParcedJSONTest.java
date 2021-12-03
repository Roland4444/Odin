package se.roland.JSON;

import junit.framework.TestCase;

public class ParcedJSONTest extends TestCase {

    public void testParse() {
        var initial = 6;
        var switcher =7;
        switch (switcher){
            case 7: initial=9; break;
            default: initial=12;break;
        }
        assertEquals(9, initial);

        switch (switcher){
            case 2: initial=9; break;
            default: initial=12;break;
        }
        assertEquals(12, initial);
    }
}