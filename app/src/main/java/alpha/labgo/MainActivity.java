package alpha.labgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import alpha.labgo.fragment.DashboardFragment;
import alpha.labgo.fragment.InventoryFragment;
import alpha.labgo.fragment.NotificationFragment;
import alpha.labgo.fragment.QrCodeScanFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    // views
    private NavigationView mNavigationView;
    private View mHeaderView;
    private TextView mUserName;
    private TextView mUserEmail;

    // firebase
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    // fragment
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Views
        mNavigationView = findViewById(R.id.nav_view);
        mHeaderView = mNavigationView.getHeaderView(0);
        mUserName = mHeaderView.findViewById(R.id.field_drawer_user_name);
        mUserEmail = mHeaderView.findViewById(R.id.field_drawer_user_email);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        initDrawer();

        // check if the user is still valid
        FirebaseUser currUser = mAuth.getCurrentUser();
        checkUser(currUser);

        // Display user name in drawer header
        displayUserName(currUser);

        // Create the adapter that will return a fragment for each section
        createFragmentAdapter();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    /**
     * display user name in drawer header
     * @param currUser
     */
    private void displayUserName(FirebaseUser currUser) {
        String uid = currUser.getUid();
        mFirestore.collection("users").document(uid).get()
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkUser(FirebaseUser currUser) {
        if (currUser != null) {
            // check if the user is still valid.
            currUser.reload().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signOut();
                }
            });
        }
    }

    // Create the adapter that will return a fragment for each section
    private void createFragmentAdapter() {
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new QrCodeScanFragment(),
                    new DashboardFragment(),
                    new InventoryFragment(),
                    new NotificationFragment()
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.segment_qr_code),
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
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_qr_code:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_inventory:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };


}
