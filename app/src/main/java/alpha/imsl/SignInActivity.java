package alpha.imsl;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GTID_REQUEST = 1;

    private ImageButton mCameraButton;
    private Button mSignUpButton;
    private Button mSignInButton;
    private TextInputEditText mGtidField;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mGtidField = findViewById(R.id.field_sign_in_gtid);
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
            onAuthSuccess(mAuth.getCurrentUser());
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
                    mGtidField.setText(qrCode.displayValue);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onAuthSuccess(FirebaseUser user) {

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

        }
    }
}
