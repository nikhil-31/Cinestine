package in.nikhil.cinestine.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.nikhil.cinestine.Adapters.ReviewAdapter;
import in.nikhil.cinestine.Adapters.TrailerAdapter;
import in.nikhil.cinestine.Data.Favourite;
import in.nikhil.cinestine.Extras.TmdbUrls;
import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.Model.Review;
import in.nikhil.cinestine.Model.Trailer;
import in.nikhil.cinestine.Network.VolleySingleton;
import in.nikhil.cinestine.R;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

  public DetailsActivityFragment() {
  }

  private Movie movie;

  private RequestQueue requestQueue;

  private TrailerAdapter trailerAdapter;
  private ReviewAdapter reviewAdapter;

  private ArrayList<Trailer> trailersArrayList = new ArrayList<Trailer>();
  private ArrayList<Review> reviewArrayList = new ArrayList<Review>();

  private Realm mRealm;
  private String trailerURL;
  private String reviewURL;

  private CollapsingToolbarLayout collapsingToolbar;
  private ImageView poster;
  private TextView releaseDate;
  private TextView rating;
  private TextView title;
  private TextView overview;
  private ImageView backdrop;
  private FloatingActionButton fab;

  private Activity mActivity;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = getActivity();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_details, container, false);

    movie = mActivity.getIntent().getParcelableExtra("Movie");

    Toolbar toolbar = v.findViewById(R.id.toolbar);

    toolbar.setNavigationIcon(ContextCompat.getDrawable(mActivity, R.mipmap.ic_back));
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mActivity.finish();

      }
    });
    toolbar.inflateMenu(R.menu.menu_details);

    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {

        if (item.getItemId() == R.id.action_share) {
          String data = trailersArrayList.get(0).getTrailer();
          startActivity(Intent.createChooser(shareIntent(TmdbUrls.YOUTUBE_URL + data),
              "Share Via"));
          return true;
        }
        return true;
      }
    });

    collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
    collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transperent));

    fab = v.findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Movie Saved", Snackbar.LENGTH_LONG).show();
        addMovie();
      }
    });

    trailerAdapter = new TrailerAdapter(mActivity);

    RecyclerView recyclerTrailer = v.findViewById(R.id.recycler_trailer);
    LinearLayoutManager layoutManagerTrailer = new LinearLayoutManager(mActivity
        , LinearLayoutManager.HORIZONTAL, false);
    recyclerTrailer.setLayoutManager(layoutManagerTrailer);
    recyclerTrailer.setAdapter(trailerAdapter);

    reviewAdapter = new ReviewAdapter(mActivity);

    RecyclerView recyclerReview = v.findViewById(R.id.recycler_review);
    LinearLayoutManager layoutManagerReview = new LinearLayoutManager(mActivity
        , LinearLayoutManager.VERTICAL, false);
    recyclerReview.setLayoutManager(layoutManagerReview);
    recyclerReview.setAdapter(reviewAdapter);
    recyclerReview.setNestedScrollingEnabled(false);

    poster = v.findViewById(R.id.poster_details);
    releaseDate = v.findViewById(R.id.Release_write);
    rating = v.findViewById(R.id.Rating_write);
    title = v.findViewById(R.id.Title_write);
    overview = v.findViewById(R.id.overview_new);
    backdrop = v.findViewById(R.id.backdrop1);

    VolleySingleton volleySingleton = VolleySingleton.getInstance();
    requestQueue = volleySingleton.getRequestQueue();
    setData(movie);

//        isMovieInDatabase();
    return v;
  }

  public void updateContent(Movie movie) {
    this.movie = movie;
    setData(movie);
  }

  public void setData(Movie movie) {
    try {
      collapsingToolbar.setTitle(movie.getOriginalTitle());

      if (isMovieInDatabase()) {
        fab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.mipmap.ic_like));
      } else {
        fab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.mipmap.ic_like_outline));
      }

      Picasso.with(mActivity)
          .load(movie.getPosterPath())
          .into(poster);

      Picasso.with(mActivity)
          .load(movie.getBackdrop())
          .into(backdrop);

      releaseDate.setText(movie.getReleaseDate());

      String rate = movie.getVoteAverage().toString() + "/10";
      rating.setText(rate);

      title.setText(movie.getOriginalTitle());

      overview.setText(movie.getOverview());
      trailerURL = TmdbUrls.MOVIE_BASE_URL + movie.getmId() + TmdbUrls.TRAILERS + TmdbUrls.API_KEY;
      reviewURL = TmdbUrls.MOVIE_BASE_URL + movie.getmId() + TmdbUrls.REVIEWS + TmdbUrls.API_KEY;

      sendTrailerJsonRequest(trailerURL);
      sendReviewJsonRequest(reviewURL);

    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  private boolean isMovieInDatabase() {
    mRealm = Realm.getDefaultInstance();
    if (movie != null) {
      RealmResults<Favourite> saved = mRealm.where(Favourite.class).contains("mId"
          , movie.getmId()).findAll();
      return saved.size() != 0;
    }
    return false;
  }

  private void addMovie() {
    Realm realm = Realm.getDefaultInstance();
    Favourite favourite = new Favourite(
        movie.getOriginalTitle(),
        movie.getPosterPath(),
        movie.getOverview(),
        movie.getVoteAverage(),
        movie.getReleaseDate(),
        movie.getBackdrop(),
        movie.getmId(),
        movie.getmPopularity(),
        movie.getmVoteCount(),
        movie.getmOriginalLanguage(),
        movie.getmTitle(),
        movie.getmAdult());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(favourite);
    realm.commitTransaction();
    realm.close();
    fab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.mipmap.ic_like));
  }

  private void sendTrailerJsonRequest(String trailerURL) {

    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET
        , trailerURL
        , null
        , new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          trailersArrayList = parseTrailerJSONResponse(response);
          trailerAdapter.setTrailerList(trailersArrayList);

        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(mActivity, "Error, Unable to fetch trailers", Toast.LENGTH_LONG).show();
      }
    });
    requestQueue.add(request);
  }

  public ArrayList<Trailer> parseTrailerJSONResponse(JSONObject response) throws JSONException {

    final String RESULTS = "results";
    final String ID = "id";
    final String KEY = "key";
    final String NAME = "name";
    final String SITE = "site";
    final String SIZE = "size";
    final String TYPE = "type";
    final String LANGUAGE = "iso_639_1";
    final String COUNTRY = "iso_3166_1";

    ArrayList<Trailer> data = new ArrayList<Trailer>();

    if (response == null || response.length() == 0) {
      return data;
    }

    JSONArray results = response.getJSONArray(RESULTS);
    for (int i = 0; i < results.length(); i++) {

      Trailer current = new Trailer();

      JSONObject jsonObject = results.getJSONObject(i);

      current.setId(jsonObject.optString(ID));
      current.setKey(jsonObject.optString(KEY));
      current.setName(jsonObject.optString(NAME));
      current.setSite(jsonObject.optString(SITE));
      current.setSize(jsonObject.getString(SIZE));
      current.setType(jsonObject.optString(TYPE));
      current.setLanguage(jsonObject.getString(LANGUAGE));
      current.setCountry(jsonObject.getString(COUNTRY));

      data.add(current);
    }
    return data;
  }

  private void sendReviewJsonRequest(String reviewURL) {

    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET
        , reviewURL
        , null
        , new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          reviewArrayList = parseReviewJSONResponse(response);
          reviewAdapter.setReviewsList(reviewArrayList);

        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(mActivity, "Error getting reviews", Toast.LENGTH_LONG).show();
      }
    });
    requestQueue.add(request);

  }

  public ArrayList<Review> parseReviewJSONResponse(JSONObject response) throws JSONException {

    final String RESULTS = "results";
    final String ID = "id";
    final String AUTHOR = "author";
    final String CONTENT = "content";

    ArrayList<Review> data = new ArrayList<Review>();

    if (response == null || response.length() == 0) {
      return data;
    }

    JSONArray results = response.getJSONArray(RESULTS);

    for (int i = 0; i < results.length(); i++) {

      Review current = new Review();

      JSONObject jsonObject = results.getJSONObject(i);

      current.setId(jsonObject.optString(ID));
      current.setAuthor(jsonObject.optString(AUTHOR));
      current.setContent(jsonObject.optString(CONTENT));

      data.add(current);
    }
    return data;
  }

  public Intent shareIntent(String data) {
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sent from Cinestine");
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
    return sharingIntent;
  }
}
