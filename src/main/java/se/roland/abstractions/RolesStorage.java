package se.roland.abstractions;
import Message.abstractions.BinaryMessage;
import abstractions.DSLRole;
import java.util.*;
public class RolesStorage implements BinaryMessage{
    public Map<String, Set<DSLRole>> storage=new HashMap<>();
    public String name;
    public String loadDSLObject(String Object, String User){
        var set=  storage.get(User);
        for (DSLRole i : set)
            if (i.getObjectName().equals(Object))
                    return i.toString();
        return null;
    }

    public int save(String User, DSLRole role){
        var set=  storage.get(User);
        if (set == null) {
            set = new HashSet<>();
            set.add(role);
            storage.put(User, set);
        }
        for (DSLRole i : set)
            if (i.getObjectName().equals(role.getObjectName()))
                return -1;
        set.add(role);
        return 0;
    }
}
