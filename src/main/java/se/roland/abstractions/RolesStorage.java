package se.roland.abstractions;
import Message.abstractions.BinaryMessage;
import abstractions.DSLRole;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public class RolesStorage implements BinaryMessage {
    public Map<String, Set<DSLRole>> storage = new HashMap<>();
    public String loadDSLObject(String Object, String User){
        var set=  storage.get(User);
        for (DSLRole i : set)
            if (i.getObjectName().equals(Object))
                    return i.toString();
        return null;
    }
}
