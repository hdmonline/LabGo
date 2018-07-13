package alpha.labgo.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import alpha.labgo.R;
import alpha.labgo.backend.RestUtils;
import alpha.labgo.settings.AccountSettingsActivity;

public class DeleteAccountDialog extends DialogFragment {

    private static final String TAG = "ClearTagsDialog";

    private AccountSettingsActivity mActivity;
    private String mGtid, mKey, mSecret, mPasswordString;

    // AWS S3
    private BasicAWSCredentials mCredentials;
    private AmazonS3Client mS3Client;
    private TransferUtility mTransferUtility;

    // Widgets
    private TextView mActionCancel, mActionConfirm;
    private TextInputEditText mPassword;

    public static DeleteAccountDialog newInstance(String gtid, String key, String secret) {
        DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog();
        Bundle args = new Bundle();
        args.putString("gtid", gtid);
        args.putString("key", key);
        args.putString("secret", secret);
        deleteAccountDialog.setArguments(args);
        return deleteAccountDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGtid = getArguments().getString("gtid");
            mKey = getArguments().getString("key");
            mSecret = getArguments().getString("secret");
        }
        mActivity = (AccountSettingsActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_account, container, false);

        // Widgets
        mActionCancel = view.findViewById(R.id.action_delete_account_cancel);
        mActionConfirm = view.findViewById(R.id.action_delete_account_confirm);
        mPassword = view.findViewById(R.id.text_delete_account_password);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        /*
         * This includes 4 steps:
         * 1. delete account on Firebase;
         * 2. delete information on Firestore by using REST API;
         * 3. delete S3 image on S3 bucket;
         * 4. delete the row in student_id_image_id table by using REST API.
         */
        mActionConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: delete account");
                mPasswordString = mPassword.getText().toString();
                mActivity.onPreDeleteAccount();

                // Before deleting the account, the user need to re-authenticate first.
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, mPasswordString);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    deleteAccount(user);
                                    Log.d(TAG, "User re-authenticated.");
                                } else {
                                    mActivity.onFailDeleteAccount("The password is incorrect!");
                                    Log.e(TAG, "onComplete: Fail to re-authenticate on Firebase.");
                                }
                            }
                        });



                getDialog().dismiss();
            }
        });

        return view;
    }

    private void deleteAccount(FirebaseUser user) {

        /// Step 1: Delete Firebase account
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mActivity.onPostDeleteAccount(0);
                            Log.d(TAG, "User account deleted.");
                        } else {
                            mActivity.onFailDeleteAccount("Failed to delete account on Firebase.");
                            Log.e(TAG, "onComplete: Fail to delete account on Firebase.");
                        }
                    }
                });

        // Step 2: Delete Firestore documents
        new RestUtils.DeleteAccount(mActivity).execute(mGtid);

        // TODO: Delete S3 image and the row in student_id_image_id table.
        // Step 3: Delete image from S3
//                String fileName = mGtid + ".jpg";
//                mCredentials = new BasicAWSCredentials(mKey, mSecret);
//                mS3Client = new AmazonS3Client(mCredentials);
//
//                mTransferUtility =
//                        TransferUtility.builder()
//                                .context(mActivity)
//                                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                                .s3Client(mS3Client)
//                                .build();
//
//                TransferObserver uploadObserver =
//                        mTransferUtility.(fileName, new File(mPath));
    }

}
