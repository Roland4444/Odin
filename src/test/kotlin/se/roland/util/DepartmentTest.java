package se.roland.util;

import junit.framework.TestCase;

public class DepartmentTest extends TestCase {
    public void testDepartment(){
        Department dep = new Department();
        assertEquals((Integer)25, dep.DepsMap.get(9));
    };

}