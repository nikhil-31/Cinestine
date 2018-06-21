package in.nikhil.cinestine.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import in.nikhil.cinestine.Data.Favourite;
import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.R;
import io.realm.RealmResults;

/**
 * Created by nikhil on 01-09-2016.
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavViewHolder> {

  private LayoutInflater inflater;
  private RealmResults<Favourite> mItems;
  private Activity mAct;
  private OnAdapterItemSelectedListener mAdapterCallback;

  public FavouriteAdapter(Activity activity, RealmResults<Favourite> results) {
    mItems = results;
    mAdapterCallback = (OnAdapterItemSelectedListener) activity;
    mAct = activity;
    inflater = LayoutInflater.from(activity);
  }

  public void update(RealmResults<Favourite> results) {
    mItems = results;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.custom_grid_popular, parent, false);
    return new FavViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
    final Favourite fav = mItems.get(position);

    Picasso.with(mAct).load(fav.getmPosterPath()).into(holder.image);

    holder.text.setText(fav.getmOriginalTitle());
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mAdapterCallback != null) {
          mAdapterCallback.onItemSelected(fav);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  class FavViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView text;

    FavViewHolder(View itemView) {
      super(itemView);
      image = itemView.findViewById(R.id.popular_image);
      text = itemView.findViewById(R.id.popular_text);
    }
  }

  public interface OnAdapterItemSelectedListener {
    void onItemSelected(Favourite id);
  }
}
