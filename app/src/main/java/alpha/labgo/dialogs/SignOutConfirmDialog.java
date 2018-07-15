package alpha.labgo.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import alpha.labgo.R;
import alpha.labgo.settings.AccountSettingsActivity;

public class SignOutConfirmDialog extends DialogFragment {

    private static final String TAG = "SignOutConfirmDialog";

    private AccountSettingsActivity mActivity;

    // Widgets
    private TextView mActionCancel, mActionConfirm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            mActivity = (AccountSettingsActivity) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sign_out_confirm, container, false);

        // Widgets
        mActionCancel = view.findViewById(R.id.action_delete_account_cancel);
        mActionConfirm = view.findViewById(R.id.action_delete_account_confirm);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        mActionConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: sign out");
                mActivity.signOut();
                getDialog().dismiss();
            }
        });

        return view;
    }
}
