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

public class AddItemClearDialog extends DialogFragment {

    private static final String TAG = "AddItemClearDialog";

    // Widgets
    private TextView mActionCancel, mActionClear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_inventory_warning, container, false);

        mActionCancel = view.findViewById(R.id.action_add_inventory_warning_cancel);
        mActionClear = view.findViewById(R.id.action_add_inventory_warning_clear);

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
