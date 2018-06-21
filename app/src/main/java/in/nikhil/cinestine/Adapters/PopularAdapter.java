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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.nikhil.cinestine.Model.Movie;
import in.nikhil.cinestine.R;

/**
 * Created by nikhil on 20-08-2016.
 */
public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {

  private ArrayList<Movie> mMovie = new ArrayList<Movie>();

  private LayoutInflater inflater;
  private Activity mActivity;

  private OnAdapterItemSelectedListener mAdapterCallback;

  public PopularAdapter(Activity activity) {
    mAdapterCallback = (OnAdapterItemSelectedListener) activity;
    inflater = LayoutInflater.from(activity);
    mActivity = activity;
  }

  public void setMoviesList(ArrayList<Movie> listmovies) {
    mMovie = listmovies;
    notifyItemRangeChanged(0, listmovies.size());
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.custom_grid_popular, parent, false);
    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    Movie currentMovie = mMovie.get(position);
    holder.text.setText(currentMovie.getOriginalTitle());
    Picasso.with(mActivity).load(currentMovie.getPosterPath()).into(holder.image);
  }

  @Override
  public int getItemCount() {
    return mMovie.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView text;

    MyViewHolder(View itemview) {
      super(itemview);
      image = itemview.findViewById(R.id.popular_image);
      text = itemview.findViewById(R.id.popular_text);
      itemview.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mAdapterCallback != null) {
            mAdapterCallback.onItemSelected(mMovie.get(getAdapterPosition()));
          }
        }
      });
    }
  }

  public interface OnAdapterItemSelectedListener {
    void onItemSelected(Movie id);
  }
}

