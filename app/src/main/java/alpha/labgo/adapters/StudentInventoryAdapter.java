package alpha.labgo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

import alpha.labgo.R;
import alpha.labgo.models.BorrowedItem;
import alpha.labgo.models.StudentInventory;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class StudentInventoryAdapter
        extends ExpandableRecyclerViewAdapter<StudentInventoryAdapter.StudentViewHolder,
        StudentInventoryAdapter.StudentBorrowedItemViewHolder>
        implements Filterable {

    private static final String TAG = "StudentInventoryAdapter";

    private Context mContext;
    private ArrayList<StudentInventory> mStudentInventories = new ArrayList<>();
    private ArrayList<StudentInventory> mFilteredStudentInventories = new ArrayList<>();

    /**
     * Default constructor.
     *
     * @param groups The group list
     */
    public StudentInventoryAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    public StudentInventoryAdapter(Context context) {
        super(new ArrayList<StudentInventory>());
        mContext = context;
    }

    @SuppressWarnings("unchecked")
    public StudentInventoryAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        try {
            mStudentInventories = (ArrayList<StudentInventory>) groups;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        mContext = context;
    }

    public class StudentViewHolder extends GroupViewHolder {

        private static final String TAG = "StudentViewHolder";

        private TextView mName, mGtid;
        private ImageView mArrow;
        private CircleImageView mProfileImage;

        public StudentViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_student_name);
            mGtid = itemView.findViewById(R.id.text_student_gtid);
            mProfileImage = itemView.findViewById(R.id.image_item);
            mArrow = itemView.findViewById(R.id.image_expand_arrow);
        }

        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            mArrow.setAnimation(rotate);
        }

        private void animateCollapse() {
            RotateAnimation rotate =
                    new RotateAnimation(0, 90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            mArrow.setAnimation(rotate);
        }

        public void setStudentView(ExpandableGroup studentInventory) {
            if (studentInventory instanceof StudentInventory){
                mName.setText(studentInventory.getTitle());
                mGtid.setText(((StudentInventory) studentInventory).getGtid());
            } else {
                Log.e(TAG, "setStudentView: the input type needs to be StudentInventory");
            }
        }
    }

    public class StudentBorrowedItemViewHolder extends ChildViewHolder {

        private CircleImageView mToolImage;
        private TextView mToolName;
        private TextView mCheckOutTime;
        private TextView mDescription;
        private RelativeLayout mParentLayout;

        public StudentBorrowedItemViewHolder(View itemView) {
            super(itemView);
            mToolImage = itemView.findViewById(R.id.image_item);
            mToolName = itemView.findViewById(R.id.text_item_name);
            mCheckOutTime = itemView.findViewById(R.id.text_checkout_time);
            mDescription = itemView.findViewById(R.id.text_item_description);
            mParentLayout = itemView.findViewById(R.id.layout_borrowed_parent);
        }

        public void setBorrowedItemView(BorrowedItem borrowedItem) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(borrowedItem.getItemImage())
                    .into(mToolImage);
            mToolName.setText(borrowedItem.getItemName());
            mDescription.setText(borrowedItem.getItemDescription());
            mCheckOutTime.setText(borrowedItem.getCheckOutTime());
        }
    }

    public void setList(ArrayList<StudentInventory> studentInventories) {
        mStudentInventories = studentInventories;
        mFilteredStudentInventories = studentInventories;
        notifyDataSetChanged();
    }

    @Override
    public StudentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_student_inventory, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(StudentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setStudentView(group);
    }

    @Override
    public StudentInventoryAdapter.StudentBorrowedItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_borrowed_item, parent, false);
        return new StudentBorrowedItemViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(StudentBorrowedItemViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final BorrowedItem borrowedItem = ((StudentInventory) group).getItems().get(childIndex);
        holder.setBorrowedItemView(borrowedItem);
    }

    @Override
    public int getItemCount() {
        return mFilteredStudentInventories.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String constrainString = constraint.toString().toLowerCase();

                if (constrainString.isEmpty()) {
                    mFilteredStudentInventories = mStudentInventories;
                } else {
                    ArrayList<StudentInventory> filteredList = new ArrayList<>();

                    for (StudentInventory item : mStudentInventories) {
                        if (item.getName().toLowerCase().contains(constrainString) ||
                                item.getGtid().toLowerCase().contains(constrainString)) {
                            filteredList.add(item);
                        }
                    }
                    mFilteredStudentInventories = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredStudentInventories;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                mFilteredStudentInventories = (ArrayList<StudentInventory>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
