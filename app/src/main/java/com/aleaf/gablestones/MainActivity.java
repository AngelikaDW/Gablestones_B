package com.aleaf.gablestones;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity

        {

        private static final String TAG = "MainActivity";

        private SectionsPageAdapter mSectionsPageAdapter;

        private ViewPager mViewPager;

        private MissionFragment mMissionFragment;

        public int mRunNbr;
        public int mFragmentId;
        public int mMatch;
        public int mTourOpen;


        Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Default open Quest/List Fragment, only when intent asks to open MapFragment (1)
        mFragmentId =0;
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*Intent sent by ClueDetailActivity gets information about runNbr of Gablestone and
        Fragment_ID*/
        /*Intent sent by SelectTourActivity send information with tour to display
        mTourOpen 1 or 2*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("Run#")) {
                mRunNbr = bundle.getInt("Run#");

                if (bundle.containsKey("Fragment_ID")) {
                    mFragmentId = bundle.getInt("Fragment_ID");
                }
                if (bundle.containsKey("LocMatch")) {
                    mMatch = bundle.getInt("LocMatch");
                }
            } else {
                if (bundle.containsKey("Tour")) {
                    mTourOpen = bundle.getInt("Tour");
                }
            }
        }
        //If ClueDetailActivity sends mapIntent to open Map, get Fragment_ID from intent
        // and open Map Fragment. If no intent, open default Fragment 0 (listview)
        mViewPager.setCurrentItem(mFragmentId);


    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new MissionFragment(), getString(R.string.fragment_mission));
        adapter.addFragment(new MapFragment(), getString(R.string.fragment_map));
        adapter.addFragment(new IntroFragment(), getString(R.string.fragment_about));
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

        switch (item.getItemId()) {
            case R.id.madeByColofon:
                Intent infoIntent = new Intent(MainActivity.this, ColofonActivity.class);
                startActivity(infoIntent);
                return true;
            case R.id.intro_tour:
                Intent tourIntent = new Intent(MainActivity.this, Introslider.class);
                startActivity(tourIntent);
                return true;
//            case R.id.confetti:
//                Intent confettiIntent = new Intent(MainActivity.this, ConfettiActivity.class);
//                startActivity(confettiIntent);
//                return true;
            case R.id.select_tour:
                Intent selectTourIntent = new Intent(MainActivity.this, SelectTourActivity.class);
                startActivity(selectTourIntent);
                return true;
            case R.id.open_map:
                Intent openMapIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(openMapIntent);
                return true;
            case R.id.open_clueDetail:
                Intent clueDetailIntent = new Intent(MainActivity.this, ClueDetailActivity.class);
                startActivity(clueDetailIntent);
                return true;
            case R.id.open_info:
                Intent infoTourIntent = new Intent(MainActivity.this, ClueDetailActivity.class);
                startActivity(infoTourIntent);
                return true;

            default:
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.madeByColofon) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }
}