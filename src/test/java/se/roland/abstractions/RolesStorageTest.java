package se.roland.abstractions;

import Message.abstractions.BinaryMessage;
import abstractions.DSLRole;
import abstractions.Role;
import com.avs.ParseDSL;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RolesStorageTest extends TestCase {
    String temprs = "temp.rs.bin";
    ParseDSL parser = new ParseDSL();
    public void testLoadDSLObject() {
        var readRole = new Role("read","", parser);
        var writeRole =new Role("write","", parser);
        var createRole = new Role("create","", parser);
        var Roles = new ArrayList<Role>(Arrays.asList(readRole, writeRole, createRole));
        var ObjectRules   = new DSLRole("requests", Roles);
        RolesStorage rs = new RolesStorage();
        var set = new HashSet<DSLRole>();
        set.add(ObjectRules);
        rs.storage.put("olga", set);
        assertEquals("'requests' => ::read{}, ::write{}, ::create{}.", rs.loadDSLObject("requests", "olga"));
    }

    public void teststorage(){
        RolesStorage rs = new RolesStorage();
        rs.storage=new HashMap<>();
        rs.storage.put("13",null);
        var readRole = new Role("read","", parser);
        var writeRole =new Role("write","", parser);
        var createRole = new Role("create","", parser);
        var Roles = new ArrayList<Role>(Arrays.asList(readRole, writeRole, createRole));
        var ObjectRules   = new DSLRole("requests", Roles);
        assertNotEquals(null, ObjectRules);
        var set = new HashSet<DSLRole>();
        set.add(ObjectRules);
        rs.storage.put("13",set);
            rs.name = "test";
        byte[] saved = BinaryMessage.savedToBLOB(rs);
        assertEquals(null, rs.storage.get("12"));
    }

    private void assertNotEquals(Object o, DSLRole objectRules) {
    }

    public void testSave() throws IOException {
        if (new File(temprs).exists())
            new File(temprs).delete();
        RolesStorage rs = new RolesStorage();
        rs.storage=new HashMap<>();
        var readRole = new Role("read","", parser);
        var writeRole =new Role("write","", parser);
        var createRole = new Role("create","", parser);
        var Roles = new ArrayList<Role>(Arrays.asList(readRole, writeRole, createRole));
        var ObjectRules   = new DSLRole("requests", Roles);
        rs.save("olga", ObjectRules);
        System.out.println("Length:="+BinaryMessage.savedToBLOB(rs).length);
        BinaryMessage.write(BinaryMessage.savedToBLOB(rs), temprs );
        RolesStorage restored = (RolesStorage) BinaryMessage.restored(BinaryMessage.readBytes(temprs));
        assertEquals(rs.storage.get("olga").toString(), restored.storage.get("olga").toString());
    }
}