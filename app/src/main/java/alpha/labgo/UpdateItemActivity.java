package alpha.labgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class UpdateItemActivity extends BaseActivity {

    private static final String TAG = "UpdateItemActivity";

    private static final int ADD_ITEM = 12;
    private static final int EDIT_ITEM = 13;

    // Widgets
    private FloatingActionButton mFabSearch, mFabCamera;
    private Toolbar mToolbar;

    // Tool info
    private String mToolName;
    private String mToolImageUrl;
    private String mToolDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);

        // Widgets
        mToolbar = findViewById(R.id.toolbar);
        mFabSearch = findViewById(R.id.fab_add_item_image_search);
        mFabCamera = findViewById(R.id.fab_add_item_image_camera);

        // Gets item information if it has
        Intent intent = getIntent();
        getToolInfo(intent);

        setSupportActionBar(mToolbar);
        mFabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Search Image", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mFabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Take Image", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getToolInfo(Intent intent) {
        if (intent.getIntExtra("addOrEdit", -1) == EDIT_ITEM) {
            mToolName = intent.getStringExtra("toolName");
            mToolDescription = intent.getStringExtra("toolDescription");
            mToolImageUrl = intent.getStringExtra("toolImage");
        }
    }
}
