package alpha.labgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import alpha.labgo.backend.InternetUtils;
import alpha.labgo.backend.RestUtils;

public class UpdateItemActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UpdateItemActivity";

    private static final int ADD_ITEM = 12;
    private static final int EDIT_ITEM = 13;

    /* Use this code for returning instead of use CommonStatusCodes.SUCCESS.
     * Because when returning back by finish(), it sends that code.
     */
    private static final int SUCCESS = 123;

    // Widgets
    private FloatingActionButton mFabSearch, mFabCamera;
    private Toolbar mToolbar;
    private ImageView mImage;
    private TextInputEditText mToolName, mToolDescription, mToolImage;
    private ImageButton mShowImage;
    private Button mSubmit;
    private ProgressBar mLoadingIndicator;

    // Tool info
    private String mToolNameString, mToolImageUrl, mToolDescriptionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);

        // Widgets
        mToolbar = findViewById(R.id.toolbar);
        mImage = findViewById(R.id.toolbar_image);
        mFabSearch = findViewById(R.id.fab_add_item_image_search);
        mFabCamera = findViewById(R.id.fab_add_item_image_camera);
        mToolName = findViewById(R.id.text_tool_name);
        mToolDescription = findViewById(R.id.text_tool_description);
        mToolImage = findViewById(R.id.text_tool_image_url);
        mShowImage = findViewById(R.id.button_show_image);
        mSubmit = findViewById(R.id.button_submit_add_item);
        mLoadingIndicator = findViewById(R.id.pb_add_item_loading);

        // Gets item information if it has
        Intent intent = getIntent();
        getToolInfo(intent);

        setSupportActionBar(mToolbar);
        mFabSearch.setOnClickListener(this);
        mFabCamera.setOnClickListener(this);
        mShowImage.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
    }

    private void getToolInfo(Intent intent) {
        if (intent.getIntExtra("addOrEdit", -1) == EDIT_ITEM) {
            mToolNameString = intent.getStringExtra("toolName");
            mToolDescriptionString = intent.getStringExtra("toolDescription");
            mToolImageUrl = intent.getStringExtra("toolImage");

            mToolName.setText(mToolNameString);
            mToolDescription.setText(mToolDescriptionString);

            loadImage(mToolImageUrl);
        }
    }

    public void showImage(Bitmap bmp) {
        mImage.setImageBitmap(bmp);
    }

    private void loadImage(String url) {


        /* This is for showing image in a rectangular area */
        // String[] paramStrings = {url};
        // new InternetUtils.GetImageByUrl(this).execute(paramStrings);

        /* This shows the image by Glide */
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(mImage);
    }

    private boolean validateInputs() {

        boolean result = true;

        if (TextUtils.isEmpty(mToolName.getText().toString())) {
            mToolName.setError("Required");
            result = false;
        } else {
            mToolName.setError(null);
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v == mShowImage) {
            mToolImageUrl = mToolImage.getText().toString();
            loadImage(mToolImageUrl);
        } else if (v == mFabSearch) {
            Snackbar.make(v, "Search Image", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (v == mFabCamera) {
            Snackbar.make(v, "Take Image", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (v == mSubmit) {
            if (validateInputs()) {
                mToolNameString = mToolName.getText().toString();
                mToolDescriptionString = mToolDescription.getText().toString();
                mToolImageUrl = mToolImage.getText().toString();
                String[] paramStrings = {mToolNameString,mToolImageUrl,mToolDescriptionString};
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new RestUtils.AddItem(this).execute(paramStrings);
            }

        }
    }

    public void onSuccessFinishing() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Intent returnIntent = new Intent();
        setResult(SUCCESS, returnIntent);
        finish();
    }

    public void onFailureFinishing() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Intent returnIntent = new Intent();
        setResult(CommonStatusCodes.ERROR, returnIntent);
        finish();
    }
}
