package alpha.labgo.models;

import java.util.HashMap;
import java.util.Map;

public class UserByUid {

    public Map<String, Object> user = new HashMap<>();

    public UserByUid(String gtid, String name, String email, boolean identity) {
        user.put("name", name);
        user.put("email", email);
        user.put("gtid", gtid);
        user.put("identity", identity);
    }
}
