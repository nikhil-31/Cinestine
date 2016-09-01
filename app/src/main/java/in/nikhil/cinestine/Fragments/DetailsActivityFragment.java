package in.nikhil.cinestine.Fragments;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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

    Movie movie;
    Toolbar toolbar;

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;


    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private ArrayList<Trailer> trailersArrayList = new ArrayList<Trailer>();
    private ArrayList<Review> reviewArrayList = new ArrayList<Review>();

    Realm mRealm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        movie = getActivity().getIntent().getParcelableExtra("Movie");

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        toolbar.inflateMenu(R.menu.menu_details);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(movie.getOriginalTitle());
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transperent));


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Movie Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                addMovie();
            }
        });
        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_like));

        trailerAdapter = new TrailerAdapter(getActivity());

        RecyclerView recyclertrailer = (RecyclerView) v.findViewById(R.id.recycler_trailer);
        LinearLayoutManager layoutManagertrailer = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclertrailer.setLayoutManager(layoutManagertrailer);
        recyclertrailer.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter(getActivity());

        RecyclerView recyclerReview = (RecyclerView) v.findViewById(R.id.recycler_review);
        LinearLayoutManager layoutManagerreview = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerReview.setLayoutManager(layoutManagerreview);
        recyclerReview.setAdapter(reviewAdapter);


        ImageView poster = (ImageView) v.findViewById(R.id.poster_details);
        TextView releaseDate = (TextView) v.findViewById(R.id.Release_write);
        TextView rating = (TextView) v.findViewById(R.id.Rating_write);
        TextView title = (TextView) v.findViewById(R.id.Title_write);
        TextView overview = (TextView) v.findViewById(R.id.overview_new);
        ImageView backdrop = (ImageView) v.findViewById(R.id.backdrop1);


        Picasso.with(getActivity())
                .load(movie.getPosterPath())
                .into(poster);

        Picasso.with(getActivity())
                .load(movie.getBackdrop())
                .into(backdrop);

        releaseDate.setText(movie.getReleaseDate());

        rating.setText(movie.getVoteAverage().toString()+"/10");

        title.setText(movie.getOriginalTitle());

        overview.setText(movie.getOverview());

        volleySingleton = volleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sendTrailerJsonRequest();
        sendReviewJsonRequest();

        return v;
    }

    private void addMovie() {
        Realm realm = Realm.getDefaultInstance();
        Favourite favourite = new Favourite(movie.getOriginalTitle(),
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

    }


    private void sendTrailerJsonRequest() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                TmdbUrls.MOVIE_BASE_URL + movie.getmId() + TmdbUrls.TRAILERS + TmdbUrls.API_KEY,
                null
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
//                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
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
        StringBuilder builder = new StringBuilder();
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

            builder.append("Name " + jsonObject.optString(NAME) + "\n");
            data.add(current);
        }


        return data;

    }

    private void sendReviewJsonRequest() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                TmdbUrls.MOVIE_BASE_URL + movie.getmId() + TmdbUrls.REVIEWS + TmdbUrls.API_KEY,
                null
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
                Toast.makeText(getActivity(), "Error getting reviews", Toast.LENGTH_LONG).show();
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
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < results.length(); i++) {

            Review current = new Review();

            JSONObject jsonObject = results.getJSONObject(i);

            current.setId(jsonObject.optString(ID));
            current.setAuthor(jsonObject.optString(AUTHOR));
            current.setContent(jsonObject.optString(CONTENT));


            builder.append("AUTHOR " + jsonObject.optString(AUTHOR) + "CONTENT " + jsonObject.optString(CONTENT) + "\n");
            data.add(current);
        }
//        Toast.makeText(getApplicationContext(), builder, Toast.LENGTH_LONG).show();

        return data;

    }


}
