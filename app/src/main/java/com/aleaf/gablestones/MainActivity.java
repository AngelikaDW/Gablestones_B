package com.aleaf.gablestones;


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
            }
            else{
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
            case R.id.action_settings:
                Intent infoIntent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(infoIntent);
                return true;
            case R.id.info_tour:
                Intent tourIntent = new Intent(MainActivity.this, Introslider.class);
                startActivity(tourIntent);
                return true;
//            case R.id.confetti:
//                Intent confettiIntent = new Intent(MainActivity.this, ConfettiActivity.class);
//                startActivity(confettiIntent);
//                return true;
            default:
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }
}