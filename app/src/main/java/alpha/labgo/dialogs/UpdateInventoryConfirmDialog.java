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
import alpha.labgo.database.RestUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateInventoryConfirmDialog extends DialogFragment {

    private static final String TAG = "UpdateInventoryConfirm";

    private static final int ADD_INVENTORY = 10;
    private static final int DELETE_INVENTORY = 11;

    public interface OnAddInventoryListener {
        void updateUi();
    }

    public OnAddInventoryListener mOnAddInventoryListener;

    // Widgets
    private TextView mName, mDescription;
    private TextView mActionOk, mActionCancel, mTitle;
    private String mTag, mNameString, mImageUrl, mDescriptionString;
    private CircleImageView mImage;

    // Add or delete
    private int mAddOrDelete;

    public static UpdateInventoryConfirmDialog newInstance(String tag, String name, String image, String description, int addOrDelete) {
        UpdateInventoryConfirmDialog updateInventoryConfirmDialog = new UpdateInventoryConfirmDialog();
        Bundle args = new Bundle();
        args.putString("tag", tag);
        args.putString("name", name);
        args.putString("image", image);
        args.putString("description", description);
        args.putInt("addOrDelete", addOrDelete);
        updateInventoryConfirmDialog.setArguments(args);
        return updateInventoryConfirmDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_inventory, container, false);

        mTag = getArguments().getString("tag");
        mNameString = getArguments().getString("name");
        mImageUrl = getArguments().getString("image");
        mDescriptionString = getArguments().getString("description");
        mAddOrDelete = getArguments().getInt("addOrDelete");

        mActionOk = view.findViewById(R.id.action_update_inventory_ok);
        mActionCancel = view.findViewById(R.id.action_update_inventory_cancel);
        mName = view.findViewById(R.id.text_update_inventory_name);
        mDescription = view.findViewById(R.id.text_update_inventory_description);
        mImage = view.findViewById(R.id.image_update_inventory_image);
        mTitle = view.findViewById(R.id.text_update_inventory_title);

        if (mAddOrDelete == ADD_INVENTORY) {
            mTitle.setText(R.string.add_inventory_title);
        } else if (mAddOrDelete == DELETE_INVENTORY) {
            mTitle.setText(R.string.delete_inventory_title);
        }

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
                if (mAddOrDelete == ADD_INVENTORY) {
                    Log.d(TAG, "onClick: add inventory");
                    // Send HTTP request to gateway
                    String[] paramStrings = {mNameString};
                    new RestUtils.AddInventoryItem(getActivity()).execute(paramStrings);
                } else if (mAddOrDelete == DELETE_INVENTORY) {
                    Log.d(TAG, "onClick: delete inventory");
                    // Send HTTP request to gateway
                    String[] paramStrings = {mTag};
                    new RestUtils.DeleteInventoryItem(getActivity()).execute(paramStrings);
                }
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
