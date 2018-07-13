package alpha.labgo.settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
            // TODO: goto account settings

        }
    }
}
