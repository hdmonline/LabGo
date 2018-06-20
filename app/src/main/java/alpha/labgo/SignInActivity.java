package alpha.labgo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DatabaseReference;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private static final int GTID_REQUEST = 1;

    // views
    private ImageButton mCameraButton;
    private Button mSignUpButton;
    private Button mSignInButton;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;

    //private DatabaseReference mDatabase;
    //private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        //mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        // Views
        mEmailField = findViewById(R.id.field_sign_in_email);
        mPasswordField = findViewById(R.id.field_sign_in_password);
        mCameraButton = findViewById(R.id.button_camera);
        mSignUpButton = findViewById(R.id.button_sign_up);
        mSignInButton = findViewById(R.id.button_sign_in);

        // Click listeners
        mCameraButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GTID_REQUEST) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode qrCode =  data.getParcelableExtra("qrCode");
                    mEmailField.setText(qrCode.displayValue);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onAuthSuccess() {
        // Go to DashboardActivity
        startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean result = true;

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

    private void signIn() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess();
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                            Exception exception = task.getException();
                            Log.d(TAG, exception.toString());
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_camera) {
            Intent toBuzzCard = new Intent(SignInActivity.this, BuzzCardBarcodeActivity.class);
            toBuzzCard.putExtra("caller", R.integer.FROM_CAMERA_BUTTON);
            startActivityForResult(toBuzzCard, GTID_REQUEST);
        } else if (id == R.id.button_sign_up) {
            Intent toBuzzCard = new Intent(SignInActivity.this, BuzzCardBarcodeActivity.class);
            toBuzzCard.putExtra("caller", R.integer.FROM_SIGN_UP_BUTTON);
            startActivity(toBuzzCard);
        } else if (id == R.id.button_sign_in) {
            signIn();
        }
    }
}
