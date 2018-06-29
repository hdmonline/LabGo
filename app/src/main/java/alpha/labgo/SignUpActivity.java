package alpha.labgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import alpha.labgo.models.User;
import alpha.labgo.models.UserByGtid;
import alpha.labgo.models.UserByUid;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    private TextInputEditText mGtidField;
    private TextInputEditText mNameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private Button mSubmitButton;

    //private DatabaseReference mDatabase;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    // global variables
    private String mGtid;
    private String mName;
    private String mEmail;
    private String mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // views
        mGtidField = findViewById(R.id.field_scanned_gtid);
        mNameField = findViewById(R.id.field_user_name);
        mEmailField = findViewById(R.id.field_user_email);
        mPasswordField = findViewById(R.id.field_user_password);
        mSubmitButton = findViewById(R.id.button_submit);

        // Barcode gtidData = getIntent().getParcelableExtra("qrCode");
        String gtid = getIntent().getStringExtra("gtid");
        mGtidField.setText(gtid);

        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_submit) {
            signUp();
        }
    }

    private boolean validateForm() {
        boolean result = true;

        // check name
        if (TextUtils.isEmpty(mNameField.getText().toString())) {
            mNameField.setError("Required");
            result = false;
        } else {
            mNameField.setError(null);
        }

        // check email
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        // check password
        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    private void validateGtidCallback(boolean valid) {

        if (valid) {
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUser:onComplete" + task.isSuccessful());
                            hideProgressDialog();

                            if (task.isSuccessful()) {
                                onAuthSuccess(task.getResult().getUser().getUid(), mGtid, mName, mEmail);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign Up Failed",
                                        Toast.LENGTH_SHORT).show();
                                Exception exception = task.getException();
                                Log.d(TAG, exception.toString());
                            }
                        }
                    });
        } else {
            hideProgressDialog();
            Toast.makeText(SignUpActivity.this, "Entered GTID has already been signed up",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        mEmail = mEmailField.getText().toString();
        mName = mNameField.getText().toString();
        mPassword = mPasswordField.getText().toString();

        showProgressDialog();
        mGtid = mGtidField.getText().toString();


        // check if gtid is valid
        mFirestore.collection("gtid").document(mGtid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean validGtid = !task.getResult().exists();
                            validateGtidCallback(validGtid);
                        } else {
                            Log.d(TAG, "failed to retrieve gtid data");
                        }
                    }
                });
    }

    private void onAuthSuccess(String uid, String gtid, String name, String email) {

        // Write new user
        writeNewUser(uid, gtid, name, email);

        // Go to MainActivity
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra("gtid", gtid);
        startActivity(intent);
        finish();
    }

    private void writeNewUser(String uid, String gtid, String name, String email) {
        boolean isTa = false;

        UserByUid userByUid = new UserByUid(gtid, name, email, isTa);
        UserByGtid UserByGtid = new UserByGtid(uid, name, email, isTa);

        // write user information into "users"
        mFirestore.collection("users").document(uid).set(userByUid.user);

        // write user information into "gtid" so that app can retrieve any user by gtid.
        mFirestore.collection("gtid").document(gtid).set(UserByGtid.user);

        /* write to real-time database */
        //User user = new User(gtid, name, email, isTa);
        //mDatabase.child("users").child(uid).setValue(user);
        //mDatabase.child("gtid").child(gtid).child("uid").setValue(uid);
        //mDatabase.child("gtid").child(gtid).child("email").setValue(email);
        //mDatabase.child("gtid").child(gtid).child("name").setValue(name);
        //mDatabase.child("gtid").child(gtid).child("ta").setValue(isTa);
    }
}
