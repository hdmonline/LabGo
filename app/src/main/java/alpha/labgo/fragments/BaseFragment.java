package alpha.labgo.fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    private static ProgressDialog mProgressDialog;

    // TODO: Add ProgressBar + Notification functions.

    public void showProgressDialog(String message) {

        // when signed out, the mProgressDialog is still stored in the BaseActivity
        // but the actual activity is gone. check mProgressDialog for different scenarios.
        if (mProgressDialog == null || mProgressDialog.getOwnerActivity() == null
                || mProgressDialog.getOwnerActivity().isDestroyed() || mProgressDialog.getOwnerActivity().isFinishing()) {
            mProgressDialog = new ProgressDialog(getActivity());
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
}
