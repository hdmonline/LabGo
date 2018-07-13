package alpha.labgo.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import alpha.labgo.BaseActivity;
import alpha.labgo.R;

public class AccountSettingsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AccountSettingsActivity";

    // Widgets
    private Toolbar mToolbar;
    private TextView mDeleteAccount, mProfile, mChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Widgets
        mToolbar = findViewById(R.id.toolbar_account_settings);
        mDeleteAccount = findViewById(R.id.text_delete_account);
        mProfile = findViewById(R.id.text_edit_profile);
        mChangePassword = findViewById(R.id.text_change_password);

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
        }
    }
}