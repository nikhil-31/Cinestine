package in.nikhil.cinestine.Fragments;

import android.content.Intent;
import android.os.Bundle;
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

import in.nikhil.cinestine.Activities.MovieDetailsActivity;
import in.nikhil.cinestine.Adapters.PopularAdapter;
import in.nikhil.cinestine.Extras.TmdbUrls;
import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.Network.VolleySingleton;
import in.nikhil.cinestine.R;


public class FragmentPopular extends Fragment implements PopularAdapter.ClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String STATE_MOVIE = "state_movies";


    private String mParam1;
    private String mParam2;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

    private RecyclerView listMovieHits;
    private PopularAdapter adapter;
    private ArrayList<Movie> ListMovies = new ArrayList<Movie>();

    public FragmentPopular() {
        // Required empty public constructor
    }

    public static FragmentPopular newInstance(String param1, String param2) {
        FragmentPopular fragment = new FragmentPopular();
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

        volleySingleton = volleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        listMovieHits = (RecyclerView) view.findViewById(R.id.recycler_popular);
        listMovieHits.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new PopularAdapter(getActivity());
        adapter.setClickListener(this);
        listMovieHits.setAdapter(adapter);
        if (savedInstanceState != null) {
            ListMovies = savedInstanceState.getParcelableArrayList(STATE_MOVIE);
            adapter.setMoviesList(ListMovies);
        } else {
            sendJsonRequest();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIE, ListMovies);

    }

    private void sendJsonRequest() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                TmdbUrls.MOVIE_BASE_URL + TmdbUrls.SORT_POPULAR + TmdbUrls.API_KEY,
                null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ListMovies = parseJSONResponse(response);
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

        ArrayList<Movie> data = new ArrayList<Movie>();

        if (response == null || response.length() == 0) {
            return data;
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

            data.add(current);
        }
        return data;

    }


    @Override
    public void itemClicked(View view, int position) {
        Movie mvs = ListMovies.get(position);
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("Movie", mvs);
        startActivity(intent);

    }
}
