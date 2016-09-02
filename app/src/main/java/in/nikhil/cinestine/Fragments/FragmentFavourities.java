package in.nikhil.cinestine.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.nikhil.cinestine.Activities.DetailsActivity;
import in.nikhil.cinestine.Adapters.FavouriteAdapter;
import in.nikhil.cinestine.Data.Favourite;
import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.R;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FragmentFavourities extends Fragment implements FavouriteAdapter.ClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;

    Realm mRealm;
    RealmResults<Favourite> results;
    private FavouriteAdapter mAdapter;

    public FragmentFavourities() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentFavourities newInstance(String param1, String param2) {
        FragmentFavourities fragment = new FragmentFavourities();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favourities, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_favourite);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRealm = Realm.getDefaultInstance();
        results = mRealm.where(Favourite.class).findAllAsync();
        mAdapter = new FavouriteAdapter(getActivity(), results);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            Log.v("Listener", " Change in data ");
            mAdapter.update(results);

        }
    };


    @Override
    public void onStart() {
        super.onStart();
        results.addChangeListener(realmChangeListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        results.removeChangeListener(realmChangeListener);
    }

    @Override
    public void itemClicked(View view, int position) {
        Movie movie = new Movie();
        Favourite fav = results.get(position);

        movie.setOriginalTitle(fav.getmOriginalTitle());
        movie.setPosterPath(fav.getmPosterPath());
        movie.setOverview(fav.getmOverview());
        movie.setVoteAverage(fav.getmVoteAverage());
        movie.setReleaseDate(fav.getmReleaseDate());
        movie.setBackdrop(fav.getmBackdrop());
        movie.setmId(fav.getmId());
        movie.setmPopularity(fav.getmPopularity());
        movie.setmVoteCount(fav.getmVoteCount());
        movie.setmOriginalLanguage(fav.getmOriginalLanguage());
        movie.setmTitle(fav.getmTitle());
        movie.setmAdult(fav.getmAdult());

        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("Movie", movie);
        startActivity(intent);

    }
}
