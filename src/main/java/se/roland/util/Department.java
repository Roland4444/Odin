package se.roland.util;

import java.util.HashMap;
import java.util.Map;

public class Department {
    public Map<Integer, Integer> DepsMap = new HashMap();
    public Department(){
        DepsMap.put(6, 1);
        DepsMap.put(16, 1);
        DepsMap.put(10, 2);
        DepsMap.put(9, 25);
    };

}
