package com.aleaf.gablestones;


    import android.content.Intent;
    import android.support.design.widget.TabLayout;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentTransaction;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v4.view.ViewPager;
    import android.os.Bundle;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    private static final String TAG_MISSION_FRAGMENT = "mission_fragment";

    private MissionFragment mMissionFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*Intent sent by ClueDetailActivity will open the MapFragment*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("Fragment#")) {
                int Fragment = bundle.getInt("Fragment#");
                Log.i("MainActivity LOG", "Fragment: "+String.valueOf(Fragment));
                setupViewPagerReload(mViewPager);
            }
            else {
                Log.i("ClueDetail LOG", "Fragment# is Null");
            }
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new MissionFragment(), getString(R.string.fragment_mission));
        adapter.addFragment(new MapFragment(), getString(R.string.fragment_map));
        adapter.addFragment(new IntroFragment(), getString(R.string.fragment_about));
        viewPager.setAdapter(adapter);
    }
    /*Upon click on Image in the ClueDetailActivity, the MapFragment will be loaded*/
    private void setupViewPagerReload(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new MissionFragment(), getString(R.string.fragment_mission));
        adapter.addFragment(new MapFragment(), getString(R.string.fragment_map));
        adapter.addFragment(new IntroFragment(), getString(R.string.fragment_about));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


}