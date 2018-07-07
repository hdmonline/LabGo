package alpha.labgo.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    private String gtid;
    private String name;
    private String email;
    private boolean isTa;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String gtid, String name, String email, boolean isTa) {
        this.name = name;
        this.email = email;
        this.gtid = gtid;
        this.isTa = isTa;
    }

    public String getGtid() {
        return gtid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean getIsTa() {
        return isTa;
    }

    public void setGtid(String gtid) {
        this.gtid = gtid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTa(boolean ta) {
        isTa = ta;
    }
}
