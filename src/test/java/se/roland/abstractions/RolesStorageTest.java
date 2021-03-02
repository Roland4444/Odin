package se.roland.abstractions;

import abstractions.DSLRole;
import abstractions.Role;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RolesStorageTest extends TestCase {

    public void testLoadDSLObject() {
        var readRole = new Role("read");
        var writeRole =new Role("write");
        var createRole = new Role("create");
        var Roles = new ArrayList<Role>(Arrays.asList(readRole, writeRole, createRole));
        var ObjectRules   = new DSLRole("requests", Roles);
        RolesStorage rs = new RolesStorage();
        var set = new HashSet<DSLRole>();
        set.add(ObjectRules);
        rs.storage.put("olga", set);
        assertEquals("'requests' => ::read{}, ::write{}, ::create{}.", rs.loadDSLObject("requests", "olga"));


    }
}