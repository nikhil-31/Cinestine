package in.nikhil.cinestine.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import in.nikhil.cinestine.Adapters.PopularAdapter;
import in.nikhil.cinestine.Fragments.DetailsActivityFragment;
import in.nikhil.cinestine.Fragments.FragmentFavourities;
import in.nikhil.cinestine.Fragments.FragmentPopular;
import in.nikhil.cinestine.Fragments.FragmentTopRated;
import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.R;
import in.nikhil.cinestine.tabs.SlidingTabLayout;

public class MainActivity extends AppCompatActivity implements PopularAdapter.OnAdapterItemSelectedListener {


    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;
    private static final int MOVIE_POPULAR = 0;
    private static final int MOVIE_TOPRATED = 1;
    private static final int MOVIE_FAVOURITE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));


        mTabs.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);
        mTabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mViewPager);

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

    @Override
    public void onItemSelected(Movie id) {
        DetailsActivityFragment detailsActivityFragment = (DetailsActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (detailsActivityFragment == null) {
            // DisplayFragment (Fragment B) is not in the layout (handset layout),
            // so start DisplayActivity (Activity B)
            // and pass it the info about the selected item
            Intent mMovieDetailIntent = new Intent(MainActivity.this, DetailsActivity.class);
            mMovieDetailIntent.putExtra("Movie", id);
            startActivity(mMovieDetailIntent);
        } else {
            // DisplayFragment (Fragment B) is in the layout (tablet layout),
            // so tell the fragment to update
            detailsActivityFragment.updateContent(id);
        }

    }

    //Using Fragment state pager adapter because it will save the state and not discard it
    //If we were using the FragmentPagerAdapter the onsave instance will have never been called in the
    //Fragments
    public class MyPagerAdapter extends FragmentStatePagerAdapter {


        String[] tabs = getResources().getStringArray(R.array.tabs);

        public MyPagerAdapter(FragmentManager fm) {

            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case MOVIE_POPULAR:
                    fragment = FragmentPopular.newInstance("", "");
                    break;
                case MOVIE_TOPRATED:
                    fragment = FragmentTopRated.newInstance("", "");
                    break;
                case MOVIE_FAVOURITE:
                    fragment = FragmentFavourities.newInstance("", "");
                    break;

            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return getResources().getStringArray(R.array.tabs)[position];

        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
