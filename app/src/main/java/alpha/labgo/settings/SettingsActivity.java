package alpha.labgo.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import alpha.labgo.BaseActivity;
import alpha.labgo.R;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SettingsActivity";

    // Widgets
    private Toolbar mToolbar;
    private TextView mAccountSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Widgets
        mToolbar = findViewById(R.id.toolbar_settings);
        mAccountSettings = findViewById(R.id.text_account_settings);

        // Display toolbar and add back button
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAccountSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mAccountSettings) {
            Intent toAccountSettings = new Intent(this, AccountSettingsActivity.class);
            startActivity(toAccountSettings);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}
