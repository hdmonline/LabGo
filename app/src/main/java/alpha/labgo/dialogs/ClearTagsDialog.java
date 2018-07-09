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
import alpha.labgo.database.RestUtils;

public class ClearTagsDialog extends DialogFragment {

    private static final String TAG = "ClearTagsDialog";

    // Widgets
    private TextView mActionCancel, mActionClear, mTvTitle;
    private String mTitle;

    public static ClearTagsDialog newInstance(String title) {
        ClearTagsDialog clearTagsDialog = new ClearTagsDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        clearTagsDialog.setArguments(args);
        return clearTagsDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_clear_tags, container, false);

        mTitle = getArguments().getString("title");

        mActionCancel = view.findViewById(R.id.action_clear_tags_cancel);
        mActionClear = view.findViewById(R.id.action_clear_tags_clear);
        mTvTitle = view.findViewById(R.id.text_clear_tags_title);

        mTvTitle.setText(mTitle);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        mActionClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clear tags");
                new RestUtils.DeleteIncomingTags(getActivity()).execute();
                getDialog().dismiss();
            }
        });

        return view;
    }
}
