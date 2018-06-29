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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private static final int GTID_REQUEST = 1;

    // views
    private RelativeLayout mSignInLayout;
    private ImageView mAppIcon;
    private ImageButton mCameraButton;
    private Button mSignUpButton;
    private Button mSignInButton;
    private TextInputEditText mGtidField;
    private TextInputEditText mPasswordField;
    private ProgressBar mLoadingIndicator;

    //private DatabaseReference mDatabase;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrUser;

    private String mGtid;
    private boolean mIsTa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mSignInLayout = findViewById(R.id.layout_sign_in);
        mAppIcon = findViewById(R.id.image_app_icon);
        mGtidField = findViewById(R.id.field_sign_in_gtid);
        mPasswordField = findViewById(R.id.field_sign_in_password);
        mCameraButton = findViewById(R.id.button_camera);
        mSignUpButton = findViewById(R.id.button_sign_up);
        mSignInButton = findViewById(R.id.button_sign_in);
        mLoadingIndicator = findViewById(R.id.pb_sign_in_loading_indicator);

        // Click listeners
        mCameraButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);

        // check if the user is still valid
        mCurrUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mCurrUser != null) {
            checkUser(mCurrUser);
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
                    //Barcode qrCode =  data.getParcelableExtra("gtid");
                    mGtid =  data.getStringExtra("gtid");
                    //mGtidField.setText(qrCode.displayValue);
                    mGtidField.setText(mGtid);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onAuthSuccess(boolean isTa) {
        // Go to DashboardActivity
        if (isTa) {
            Intent intent = new Intent(SignInActivity.this, MainTaActivity.class);
            intent.putExtra("gtid", mGtid);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.putExtra("gtid", mGtid);
            startActivity(intent);
        }
        finish();
    }

    private boolean validateForm() {
        boolean result = true;

        // check email
        if (TextUtils.isEmpty(mGtidField.getText().toString())) {
            mGtidField.setError("Required");
            result = false;
        } else {
            mGtidField.setError(null);
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

    private void signInWithGtid() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        final String gtidInput = mGtidField.getText().toString();
        final String password = mPasswordField.getText().toString();


        mFirestore.collection("gtid").document(gtidInput).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String email = documentSnapshot.getString("email");
                            mIsTa = documentSnapshot.getBoolean("ta");
                            mGtid = gtidInput;
                            signInWithEmail(email, password);
                        } else {
                            hideProgressDialog();
                            Toast.makeText(SignInActivity.this, "The user does not exist",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(mIsTa);
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                            Exception exception = task.getException();
                            Log.d(TAG, exception.toString());
                        }
                    }
                });
    }

    private void checkUser(FirebaseUser currUser) {

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mSignInLayout.setVisibility(View.INVISIBLE);

        if (currUser != null) {
            // check if the user is still valid.
            currUser.reload().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mSignInLayout.setVisibility(View.VISIBLE);
                    mAuth.signOut();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String uid = mCurrUser.getUid();
                    mFirestore.collection("users").document(uid).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                                    mGtid = documentSnapshot.getString("gtid");
                                    mIsTa = documentSnapshot.getBoolean("ta");
                                    onAuthSuccess(mIsTa);
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // carry "caller" to new activity so that the new activity can track back to prev one.
        if (id == R.id.button_camera) {
            Intent toBuzzCard = new Intent(SignInActivity.this, BuzzCardTextActivity.class);
            toBuzzCard.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            toBuzzCard.putExtra("caller", getResources().getInteger(R.integer.FROM_CAMERA_BUTTON));
            startActivityForResult(toBuzzCard, GTID_REQUEST);
        } else if (id == R.id.button_sign_up) {
            Intent toBuzzCard = new Intent(SignInActivity.this, BuzzCardTextActivity.class);
            toBuzzCard.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            toBuzzCard.putExtra("caller", getResources().getInteger(R.integer.FROM_SIGN_UP_BUTTON));
            startActivity(toBuzzCard);
        } else if (id == R.id.button_sign_in) {
            signInWithGtid();
        }
    }
}
