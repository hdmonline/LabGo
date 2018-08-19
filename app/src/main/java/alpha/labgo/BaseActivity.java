package alpha.labgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    // AWS S3 credential
    protected static final String S3_KEY = "AKIAIVMUFQ7N7SN6UX2Q";
    protected static final String S3_SECRET = "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";

    private static final int PERMISSION_REQUESTS = 2;
    private static ProgressDialog mProgressDialog;
    protected static String sGtid;
    public static boolean sIsTa;

    /**
     * This method shows progress dialog with desired message.
     *
     * @param message The message needs to be shown on the dialog
     */
    public void showProgressDialog(String message) {

        // when signed out, the mProgressDialog is still stored in the BaseActivity
        // but the actual activity is gone. check mProgressDialog for different scenarios.
        if (mProgressDialog == null || mProgressDialog.getOwnerActivity() == null
                || mProgressDialog.getOwnerActivity().isDestroyed() || mProgressDialog.getOwnerActivity().isFinishing()) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void setProgressDialog(int progress) {
        mProgressDialog.setProgress(progress);
    }

    public void setMaxDialog(int max) {
        mProgressDialog.setMax(max);
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static String getGtid() {
        String uid = getUid();

        FirebaseFirestore.getInstance().collection("users")
                .document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    sGtid = documentSnapshot.get("gtid").toString();
                } else {
                    Log.e(TAG, "can't find gtid for this user");
                }
            }
        });
        return sGtid;
    }

    public String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String [] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    public boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    public void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }
}