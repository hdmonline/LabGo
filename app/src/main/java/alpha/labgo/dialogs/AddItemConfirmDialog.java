package alpha.labgo.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import alpha.labgo.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddItemConfirmDialog extends DialogFragment {

    private static final String TAG = "AddItemConfirmDialog";

    public interface OnAddInventoryListener {
        void updateUi();
    }
    public OnAddInventoryListener mOnAddInventoryListener;

    // Widgets
    private TextView mName, mDescription;
    private TextView mActionOk, mActionCancel;
    private String mNameString, mImageUrl, mDescriptionString;
    private CircleImageView mImage;

    public static AddItemConfirmDialog newInstance(String name, String image, String description) {
        AddItemConfirmDialog dashboardFragment = new AddItemConfirmDialog();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("image", image);
        args.putString("description", description);
        dashboardFragment.setArguments(args);
        return dashboardFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_inventory, container, false);

        mNameString = getArguments().getString("name");
        mImageUrl = getArguments().getString("image");
        mDescriptionString = getArguments().getString("description");

        mActionOk = view.findViewById(R.id.action_add_inventory_ok);
        mActionCancel = view.findViewById(R.id.action_add_inventory_cancel);
        mName = view.findViewById(R.id.text_add_inventory_name);
        mDescription = view.findViewById(R.id.text_add_inventory_description);
        mImage = view.findViewById(R.id.image_add_inventory_image);

        Glide.with(this)
                .asBitmap()
                .load(mImageUrl)
                .into(mImage);
        mName.setText(mNameString);
        mDescription.setText(mDescriptionString);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add inventory");
                // TODO: send http request to add item, dismiss dialog,
                mOnAddInventoryListener.updateUi();
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnAddInventoryListener = (OnAddInventoryListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
