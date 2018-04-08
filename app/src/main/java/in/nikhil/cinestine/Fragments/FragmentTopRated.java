package in.nikhil.cinestine.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.nikhil.cinestine.Adapters.PopularAdapter;
import in.nikhil.cinestine.Extras.EndlessRecyclerViewScrollListener;
import in.nikhil.cinestine.Extras.TmdbUrls;
import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.Network.VolleySingleton;
import in.nikhil.cinestine.R;


public class FragmentTopRated extends Fragment {

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private String STATE_MOVIE = "state_movies";

  private String mParam1;
  private String mParam2;

  private ImageLoader imageLoader;
  private RequestQueue requestQueue;
  private RecyclerView listMovieHits;
  private PopularAdapter adapter;
  private ArrayList<Movie> ListMovies = new ArrayList<Movie>();

  public FragmentTopRated() {
    // Required empty public constructor
  }

  public static FragmentTopRated newInstance(String param1, String param2) {
    FragmentTopRated fragment = new FragmentTopRated();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
    VolleySingleton volleySingleton = VolleySingleton.getInstance();
    requestQueue = volleySingleton.getRequestQueue();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_top_rated, container, false);
    listMovieHits = (RecyclerView) view.findViewById(R.id.recycler_top_rated);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
    listMovieHits.setLayoutManager(gridLayoutManager);

    listMovieHits.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        // Triggered only when new data needs to be appended to the list
        // Add whatever code is needed to append new items to the bottom of the list
        sendJsonRequest(page);
//                Snackbar.make(listMovieHits, "Loading page " + page, Snackbar.LENGTH_LONG).show();
      }
    });

    adapter = new PopularAdapter(getActivity(), getActivity());

    listMovieHits.setAdapter(adapter);
    if (savedInstanceState != null) {
      ListMovies = savedInstanceState.getParcelableArrayList(STATE_MOVIE);
      adapter.setMoviesList(ListMovies);
    } else {
      sendJsonRequest(1);
    }
    return view;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(STATE_MOVIE, ListMovies);
  }

  private void sendJsonRequest(int page) {


    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
        TmdbUrls.MOVIE_BASE_URL + TmdbUrls.SORT_TOP_RATED + TmdbUrls.API_KEY + TmdbUrls.PAGE + page,
        null
        , new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          ListMovies.addAll(parseJSONResponse(response));
          adapter.setMoviesList(ListMovies);

        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
      }
    });
    requestQueue.add(request);

  }

  public ArrayList<Movie> parseJSONResponse(JSONObject response) throws JSONException {

    final String RESULTS = "results";
    final String POSTER_PATH = "poster_path";
    final String OVERVIEW = "overview";
    final String ORIGINAL_TITLE = "original_title";
    final String RELEASE_DATE = "release_date";
    final String BACKDROP_PATH = "backdrop_path";
    final String VOTE_AVERAGE = "vote_average";
    final String ID = "id";
    final String POPULARITY = "popularity";
    final String VOTE_COUNT = "vote_count";
    final String ORIGINAL_LANGUAGE = "original_language";
    final String TITLE = "title";
    final String ADULT = "adult";

    ArrayList<Movie> movieArrayList = new ArrayList<Movie>();

    if (response == null || response.length() == 0) {
      return movieArrayList;
    }

    JSONArray results = response.getJSONArray(RESULTS);

    for (int i = 0; i < results.length(); i++) {

      Movie current = new Movie();

      JSONObject jsonObject = results.getJSONObject(i);

      current.setPosterPath(jsonObject.getString(POSTER_PATH));
      current.setOverview(jsonObject.optString(OVERVIEW));
      current.setOriginalTitle(jsonObject.optString(ORIGINAL_TITLE));
      current.setReleaseDate(jsonObject.optString(RELEASE_DATE));
      current.setVoteAverage(Float.parseFloat(jsonObject.optString(VOTE_AVERAGE)));
      current.setBackdrop(jsonObject.optString(BACKDROP_PATH));
      current.setmId(jsonObject.getString(ID));
      current.setmPopularity(jsonObject.getString(POPULARITY));
      current.setmVoteCount(jsonObject.getString(VOTE_COUNT));
      current.setmOriginalLanguage(jsonObject.getString(ORIGINAL_LANGUAGE));
      current.setmTitle(jsonObject.getString(TITLE));
      current.setmAdult(jsonObject.getString(ADULT));

      movieArrayList.add(current);
    }
    return movieArrayList;
  }


}
