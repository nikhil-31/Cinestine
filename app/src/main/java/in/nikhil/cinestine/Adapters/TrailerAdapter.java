package in.nikhil.cinestine.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.nikhil.cinestine.Extras.TmdbUrls;
import in.nikhil.cinestine.Model.Trailer;
import in.nikhil.cinestine.R;

/**
 * Created by nikhil on 27-08-2016.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

  private ArrayList<Trailer> trailers = new ArrayList<Trailer>();
  private Context context;
  private LayoutInflater inflater;

  public TrailerAdapter(Context context) {
    this.context = context;
    inflater = LayoutInflater.from(context);
  }

  public void setTrailerList(ArrayList<Trailer> trailerList) {
    this.trailers = trailerList;
    notifyDataSetChanged();
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.recycler_trailer_single_row, parent, false);
    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    final Trailer current = trailers.get(position);

    Picasso.with(context).load(current.getKey()).into(holder.imageView);
    holder.textView.setText(current.getName());
    holder.imageView.setOnClickListener(new ImageView.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(current.getTrailer()));
        context.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return trailers.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView textView;

    public MyViewHolder(View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.trailer_single_row_image);
      textView = itemView.findViewById(R.id.trailer_single_row_text);
    }
  }
}
