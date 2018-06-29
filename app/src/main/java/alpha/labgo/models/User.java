package alpha.labgo.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String gtid;
    public String name;
    public String email;
    public boolean isTa;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String gtid, String name, String email, boolean isTa) {
        this.name = name;
        this.email = email;
        this.gtid = gtid;
        this.isTa = isTa;
    }
}
