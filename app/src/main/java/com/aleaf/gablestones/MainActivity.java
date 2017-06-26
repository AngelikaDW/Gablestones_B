package com.aleaf.gablestones;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.TabLayout;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v4.view.ViewPager;
    import android.os.Bundle;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private ViewPager viewPager;

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

        // FAB
        //fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        //fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        //showRightFab(mViewPager.getCurrentItem());

        //add a listener to the ViewPager to call the showRightFab(...) function each time the
        // selected tab changes.

//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                showRightFab(position);
//            }
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//            @Override
//            public void onPageScrollStateChanged(int state) {}
//        });


//        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                animateFab(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        };

//        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                animateFab(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        };

//        fab1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mViewPager.getContext(), "Testing FAB Intro", Toast.LENGTH_SHORT).show();
//            }
//        });
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

//    // Function to show the right FAB for the given tab. Default case, just call hide method on tabs.
//    public void showRightFab(int tab) {
//        switch (tab) {
//            case 0:
//                fab2.hide(new FloatingActionButton.OnVisibilityChangedListener() {
//                    @Override
//                    public void onHidden(FloatingActionButton fab) {
//                        fab1.show();
//                    }
//                });
//                break;
//            case 1:
//                fab1.hide(new FloatingActionButton.OnVisibilityChangedListener(){
//                    @Override
//                    public void onHidden(FloatingActionButton fab) {
//                        fab2.show();
//                    }
//                });
//                break;
//            default:
//                fab1.hide();
//                fab2.hide();
//                break;
//        }
//    }
//    private void animateFab(int position) {
//        switch (position) {
//            case 0:
//                fab1.show();
//                fab2.hide();
//                break;
//            case 1:
//                fab2.show();
//                fab1.hide();
//                break;
//
//            default:
//                fab1.show();
//                fab2.hide();
//                break;
//        }
//    }


}