package alpha.labgo.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import alpha.labgo.BaseActivity;
import alpha.labgo.R;
import alpha.labgo.SignInActivity;
import alpha.labgo.dialogs.DeleteAccountDialog;
import alpha.labgo.dialogs.SignOutConfirmDialog;

public class AccountSettingsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AccountSettingsActivity";

    // Widgets
    private Toolbar mToolbar;
    private TextView mChangePassword, mProfile, mDeleteAccount, mSignOut;
    private ProgressBar mProgressBar;

    // This variable stores the status of 4 steps (now is 2)
    private boolean[] mDeleteAccountSuccess = new boolean[2];

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Views
        mToolbar = findViewById(R.id.toolbar_account_settings);
        mDeleteAccount = findViewById(R.id.text_delete_account);
        mProfile = findViewById(R.id.text_edit_profile);
        mChangePassword = findViewById(R.id.text_change_password);
        mSignOut = findViewById(R.id.text_account_sign_out);
        mProgressBar = findViewById(R.id.pb_account_settings);

        // OnClickListeners
        mDeleteAccount.setOnClickListener(this);
        mProfile.setOnClickListener(this);
        mSignOut.setOnClickListener(this);
        mChangePassword.setOnClickListener(this);

        // Display toolbar and add back button
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * This method is called when each step of deleting an account is successfully done.
     *
     * @param step The step number of successful step. (start from 0)
     */
    public void onPostDeleteAccount(int step) {
        mDeleteAccountSuccess[step] = true;
        boolean allSuccess = false;
        for (boolean success : mDeleteAccountSuccess) {
            if (!success) {
                allSuccess = false;
                break;
            }
            allSuccess = true;
        }
        if (allSuccess) {
            mProgressBar.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onFailDeleteAccount(String message) {
        mProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }

    public void onPreDeleteAccount() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        if (view == mProfile) {
            // TODO
        }

        if (view == mChangePassword) {
            // TODO
        }

        if (view == mSignOut) {
            SignOutConfirmDialog dialog = new SignOutConfirmDialog();
            dialog.show(getFragmentManager(), "SignOutConfirmDialog");
        }

        if (view == mDeleteAccount) {
            DeleteAccountDialog dialog = new DeleteAccountDialog().newInstance(sGtid, S3_KEY, S3_SECRET);
            dialog.show(getFragmentManager(), "DeleteAccountDialog");
        }
    }

    public void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}
