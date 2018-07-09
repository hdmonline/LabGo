package alpha.labgo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import alpha.labgo.adapters.InventoryItemAdapter;
import alpha.labgo.fragments.DashboardTaFragment;
import alpha.labgo.fragments.InventoryTaFragment;
import alpha.labgo.fragments.NotificationTaFragment;

public class MainTaActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        InventoryItemAdapter.ShowItemEditDialog {

    private static final String TAG = "MainTaActivity";

    // Views
    private NavigationView mNavigationView;
    private View mHeaderView;
    private TextView mUserName;
    private TextView mUserEmail;
    private BottomNavigationView mBottomNavigationView;
    private MenuItem mPrevMenuItem;
    private DrawerLayout mDrawer;
    private TextView mIdentity;

    // ViewPager and PagerAdapter
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    // Firebase
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String mGtid;

    // Fragments
    private DashboardTaFragment mDashboardFragment;
    private InventoryTaFragment mInventoryFragment;
    private NotificationTaFragment mNotificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ta);

        mGtid = getIntent().getStringExtra("gtid");

        // Views
        mNavigationView = findViewById(R.id.nav_view_ta);
        mHeaderView = mNavigationView.getHeaderView(0);
        mUserName = mHeaderView.findViewById(R.id.field_drawer_user_name);
        mUserEmail = mHeaderView.findViewById(R.id.field_drawer_user_email);
        mIdentity = mHeaderView.findViewById(R.id.text_identity);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_ta);
        mViewPager = findViewById(R.id.container);
        mDrawer = findViewById(R.id.layout_drawer_ta);

        mIdentity.setText("TA");

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        initDrawer();

        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // switch button to dashboard as first page.
        mBottomNavigationView.getMenu().getItem(0).setChecked(true);

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }

        // Display user name in drawer header
        displayUserName(mGtid);

        // Create the adapter that will return a fragment for each section
        createFragmentAdapter();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // QR code activity handled here
        if (id == R.id.action_qr_code) {
            Intent intent = new Intent(MainTaActivity.this, QrCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("gtid", mGtid);
            startActivity(intent);
            return true;
        }

        // refresh button
        if (id == R.id.action_refresh) {
            int currentPage = mViewPager.getCurrentItem();
            switch (currentPage) {
                case 0:
                    mDashboardFragment.refreshData();
                    break;
                case 1:
                    mInventoryFragment.refreshData();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signout) {
            signOut();
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainTaActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * display user name in drawer header
     * @param gtid  User's GTID
     */
    private void displayUserName(String gtid) {
        mFirestore.collection("gtid").document(gtid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mUserName.setText(documentSnapshot.get("name").toString());
                        mUserEmail.setText(documentSnapshot.get("email").toString());
                    }
                });
    }

    private void initDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_ta);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Create the adapter that will return a fragment for each section
     */
    private void createFragmentAdapter() {
        mDashboardFragment = DashboardTaFragment.newInstance(mGtid);
        mInventoryFragment = InventoryTaFragment.newInstance(mGtid);
        mNotificationFragment = new NotificationTaFragment();
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            private final Fragment[] mFragments = new Fragment[]{
                    mDashboardFragment,
                    mInventoryFragment,
                    mNotificationFragment
            };

            private final String[] mFragmentNames = new String[]{
                    getString(R.string.segment_dashboard),
                    getString(R.string.segment_inventory),
                    getString(R.string.segment_notification)
            };

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPrevMenuItem != null) {
                    mPrevMenuItem.setChecked(false);
                }
                else
                {
                    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                mBottomNavigationView.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = mBottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_ta_dashboard:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_ta_inventory:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_ta_notifications:
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void showItemEditDialog() {
        //TODO: Show the item editing dialog here.
        Toast.makeText(this, "This function will be added later",
                Toast.LENGTH_LONG).show();
    }
}
