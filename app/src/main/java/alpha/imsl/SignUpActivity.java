package alpha.imsl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import alpha.imsl.models.User;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    private TextInputEditText mGtidField;
    private TextInputEditText mNameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private Button mSignUp;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // views
        mGtidField = findViewById(R.id.field_scanned_gtid);
        mNameField = findViewById(R.id.field_user_name);
        mEmailField = findViewById(R.id.field_user_email);
        mPasswordField = findViewById(R.id.field_user_password);
//        mSignUp = findViewById();

        Barcode gtidData = getIntent().getParcelableExtra("qrCode");
        mGtidField.setText(gtidData.displayValue);
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

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        final String gtid = mGtidField.getText().toString();
        final String email = mEmailField.getText().toString();
        final String name = mNameField.getText().toString();
        final String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser().getUid(), gtid, name, email);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })


    }

    private void onAuthSuccess(String uid, String gtid, String name, String email) {

        // Write new user
        writeNewUser(uid, gtid, name, email);

        // Go to MainActivity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    private void writeNewUser(String uid, String gtid, String name, String email) {
        boolean identity = false;
        User user = new User(gtid, name, email, identity);
        mDatabase.child("users").child(uid).setValue(user);
        mDatabase.child("gtid").child(gtid).setValue(uid);
    }
}
