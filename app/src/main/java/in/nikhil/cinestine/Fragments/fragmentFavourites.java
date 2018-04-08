package in.nikhil.cinestine.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.nikhil.cinestine.Adapters.FavouriteAdapter;
import in.nikhil.cinestine.Data.Favourite;
import in.nikhil.cinestine.R;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class fragmentFavourites extends Fragment {
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  private String mParam1;
  private String mParam2;

  private RecyclerView mRecyclerView;

  Realm mRealm;
  RealmResults<Favourite> results;
  private FavouriteAdapter mAdapter;

  public fragmentFavourites() {
    // Required empty public constructor
  }

  public static fragmentFavourites newInstance(String param1, String param2) {
    fragmentFavourites fragment = new fragmentFavourites();
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
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_favourities, container, false);

    mRecyclerView = v.findViewById(R.id.recycler_favourite);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
    mRecyclerView.setLayoutManager(gridLayoutManager);

    mRealm = Realm.getDefaultInstance();
    results = mRealm.where(Favourite.class).findAllAsync();

    mAdapter = new FavouriteAdapter(getActivity(), results, getActivity());
    mRecyclerView.setAdapter(mAdapter);

    return v;
  }

  @Override
  public void onResume() {
    super.onResume();
    mRealm = Realm.getDefaultInstance();
    results = mRealm.where(Favourite.class).findAllAsync();
    mAdapter = new FavouriteAdapter(getActivity(), results, getActivity());
    mRecyclerView.setAdapter(mAdapter);

    results.addChangeListener(realmChangeListener);
  }

  private RealmChangeListener realmChangeListener = new RealmChangeListener() {
    @Override
    public void onChange() {
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

}