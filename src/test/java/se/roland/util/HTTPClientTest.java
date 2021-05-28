package se.roland.util;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.stream.Collectors;

public class HTTPClientTest extends TestCase {

    public void testSendPOST() {
        var map = new HashMap<>();
        map.put("a::", "12");
        map.put("b::", "14");
        map.put("c f::", "14");
        var map2 = map.entrySet().stream()
                .filter(a-> a.getKey().toString().length()>0)
                .collect(Collectors.toMap(
                        e -> e.getKey().toString().replace("::","").replace(" ", "_"),
                        e -> e.getValue()
                ));
        map2.entrySet().stream().forEach(System.out::println);
        assertEquals(map2.get("a"), "12");
        assertEquals(map2.get("c_f"), "14");
    }

    public void testAcess(){


    }
}