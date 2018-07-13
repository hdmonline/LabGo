package alpha.labgo.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import alpha.labgo.BaseActivity;
import alpha.labgo.R;
import alpha.labgo.SignInActivity;
import alpha.labgo.dialogs.DeleteAccountDialog;

public class AccountSettingsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AccountSettingsActivity";

    // Widgets
    private Toolbar mToolbar;
    private TextView mDeleteAccount, mProfile, mChangePassword;
    private ProgressBar mProgressBar;

    // This variable stores the status of 4 steps (now is 2)
    private boolean[] mDeleteAccountSuccess = new boolean[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Views
        mToolbar = findViewById(R.id.toolbar_account_settings);
        mDeleteAccount = findViewById(R.id.text_delete_account);
        mProfile = findViewById(R.id.text_edit_profile);
        mChangePassword = findViewById(R.id.text_change_password);
        mProgressBar = findViewById(R.id.pb_account_settings);

        // OnClickListeners
        mDeleteAccount.setOnClickListener(this);
        mProfile.setOnClickListener(this);
        mChangePassword.setOnClickListener(this);

        // Display toolbar and add back button
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v == mDeleteAccount) {
            // TODO: show dialog for confirmation
            DeleteAccountDialog dialog = new DeleteAccountDialog().newInstance(sGtid, S3_KEY, S3_SECRET);
            dialog.show(getFragmentManager(), "DeleteAccountDialog");
        }
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
}
