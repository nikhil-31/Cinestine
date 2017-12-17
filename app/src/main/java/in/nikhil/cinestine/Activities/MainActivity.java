package in.nikhil.cinestine.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import in.nikhil.cinestine.Adapters.FavouriteAdapter;
import in.nikhil.cinestine.Adapters.PopularAdapter;
import in.nikhil.cinestine.Data.Favourite;
import in.nikhil.cinestine.Fragments.DetailsActivityFragment;
import in.nikhil.cinestine.Fragments.FragmentFavourities;
import in.nikhil.cinestine.Fragments.FragmentPopular;
import in.nikhil.cinestine.Fragments.FragmentTopRated;
import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.R;


public class MainActivity extends AppCompatActivity implements PopularAdapter.OnAdapterItemSelectedListener,
    FavouriteAdapter.OnAdapterItemSelectedListener {

  private static final int MOVIE_POPULAR = 0;
  private static final int MOVIE_TOPRATED = 1;
  private static final int MOVIE_FAVOURITE = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    ViewPager viewPager = findViewById(R.id.pager);
    TabLayout tabs = findViewById(R.id.tabs);
    viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

    tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    tabs.setupWithViewPager(viewPager);
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
      // so tell the fragment to update its data
      detailsActivityFragment.updateContent(id);
    }
  }

  @Override
  public void onItemSelected(Favourite id) {
    DetailsActivityFragment detailsActivityFragment = (DetailsActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    Movie movie = new Movie();

    movie.setOriginalTitle(id.getmOriginalTitle());
    movie.setPosterPath(id.getmPosterPath());
    movie.setOverview(id.getmOverview());
    movie.setVoteAverage(id.getmVoteAverage());
    movie.setReleaseDate(id.getmReleaseDate());
    movie.setBackdrop(id.getmBackdrop());
    movie.setmId(id.getmId());
    movie.setmPopularity(id.getmPopularity());
    movie.setmVoteCount(id.getmVoteCount());
    movie.setmOriginalLanguage(id.getmOriginalLanguage());
    movie.setmTitle(id.getmTitle());
    movie.setmAdult(id.getmAdult());

    if (detailsActivityFragment == null) {
      Intent movieDetailIntent = new Intent(MainActivity.this, DetailsActivity.class);
      movieDetailIntent.putExtra("Movie", movie);
      startActivity(movieDetailIntent);
    } else {
      // DisplayFragment (Fragment B) is in the layout (tablet layout), so tell the fragment to update
      detailsActivityFragment.updateContent(movie);
    }
  }

  //Using Fragment state pager adapter because it will save the state and not discard it
  //If we were using the FragmentPagerAdapter the onsave instance will have never been called in the
  //Fragments
  private class MyPagerAdapter extends FragmentStatePagerAdapter {
    String[] tabs = getResources().getStringArray(R.array.tabs);

    MyPagerAdapter(FragmentManager fm) {
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
