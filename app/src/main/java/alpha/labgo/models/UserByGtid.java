package alpha.labgo.models;

import java.util.HashMap;
import java.util.Map;

public class UserByGtid {

    public Map<String, Object> user = new HashMap<>();

    public UserByGtid() {

    }

    public UserByGtid(String uid, String name, String email, boolean identity) {
        user.put("name", name);
        user.put("email", email);
        user.put("uid", uid);
        user.put("identity", identity);
    }
}