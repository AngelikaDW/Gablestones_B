package com.aleaf.gablestones;

    import android.content.Intent;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.TabLayout;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentTransaction;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v4.view.ViewPager;
    import android.os.Bundle;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;

    import java.util.Locale;


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

        //Attempt to go back to Mission Fragment from ClueDetail Activity TODO: get it working!
     /*   FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .commit();*/
/*
        FragmentManager fm = getSupportFragmentManager();
        mMissionFragment = (MissionFragment) fm.findFragmentByTag(TAG_MISSION_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mMissionFragment == null) {
            mMissionFragment = new MissionFragment();
            fm.beginTransaction().add(mMissionFragment, TAG_MISSION_FRAGMENT).commit();
        }*/

        //Get the system language of user's device
        String language = Locale.getDefault().getDisplayLanguage();
        Log.i("Device language", language);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new IntroFragment(), "Intro");
        adapter.addFragment(new MissionFragment(), "Mission");
        adapter.addFragment(new MapFragment(), "Map");
        viewPager.setAdapter(adapter);
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