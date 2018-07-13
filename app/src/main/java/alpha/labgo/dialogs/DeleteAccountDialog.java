package alpha.labgo.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import alpha.labgo.R;
import alpha.labgo.backend.RestUtils;

public class DeleteAccountDialog extends DialogFragment {

    private static final String TAG = "ClearTagsDialog";

    // Widgets
    private TextView mActionCancel, mActionConfirm, mTvTitle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_clear_tags, container, false);

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
                Log.d(TAG, "onClick: delete account");
                new RestUtils.DeleteIncomingTags(getActivity()).execute();
                getDialog().dismiss();
            }
        });

        return view;
    }
}
