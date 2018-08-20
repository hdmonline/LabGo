package alpha.labgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import alpha.labgo.fragments.DashboardFragment;
import alpha.labgo.fragments.InventoryFragment;
import alpha.labgo.fragments.NotificationFragment;
import alpha.labgo.settings.SettingsActivity;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    // Views
    private NavigationView mNavigationView;
    private View mHeaderView;
    private TextView mUserName;
    private TextView mUserEmail;
    private BottomNavigationView mBottomNavigationView;
    private MenuItem mPrevMenuItem;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private MenuItem mRefresh;
    private MenuItem mSearch;
    private MenuItem mQrCode;
    private SearchView mSearchView;

    // Firebase
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    // ViewPager and PagerAdapter
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    // Fragments
    private DashboardFragment mDashboardFragment;
    private InventoryFragment mInventoryFragment;
    private NotificationFragment mNotificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Views
        mNavigationView = findViewById(R.id.nav_view);
        mHeaderView = mNavigationView.getHeaderView(0);
        mUserName = mHeaderView.findViewById(R.id.field_drawer_user_name);
        mUserEmail = mHeaderView.findViewById(R.id.field_drawer_user_email);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mViewPager = findViewById(R.id.container);
        mDrawer = findViewById(R.id.layout_drawer);

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
        displayUserName(sGtid);

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mRefresh = menu.findItem(R.id.action_refresh);
        mQrCode = menu.findItem(R.id.action_qr_code);
        mSearch = menu.findItem(R.id.action_search_item);

        // Make the SearchView fill the width of the toolbar
        mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        // Set listeners
        search(mSearchView);
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                int currentPage = mViewPager.getCurrentItem();
                switch (currentPage) {
                    case 0:
                        mDashboardFragment.filterData(newText);
                        break;
                    case 1:
                        mInventoryFragment.filterData(newText);
                        break;
                }
                return false;
            }
        });
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
            Intent intent = new Intent(MainActivity.this, QrCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("gtid", sGtid);
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

        // settings
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
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
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }


        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Display user name in drawer header
     *
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
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Create the adapter that will return a fragment for each section
     */
    private void createFragmentAdapter() {
        mDashboardFragment = DashboardFragment.newInstance(sGtid);
        mInventoryFragment = InventoryFragment.newInstance(sGtid);
        mNotificationFragment = new NotificationFragment();
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    mDashboardFragment,
                    mInventoryFragment,
                    mNotificationFragment
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.segment_dashboard),
                    getString(R.string.segment_inventory),
                    getString(R.string.segment_notification)
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
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
                switch (position) {
                    case 0:
                        mRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        mQrCode.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        mSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        break;
                    case 1:
                        mRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        mQrCode.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        mSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        break;
                    case 2:
                        mRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                        mQrCode.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        mSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        break;
                }
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
                case R.id.navigation_dashboard:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_inventory:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };
}
