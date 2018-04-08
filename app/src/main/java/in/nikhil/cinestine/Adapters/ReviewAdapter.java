package in.nikhil.cinestine.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.nikhil.cinestine.Model.Review;
import in.nikhil.cinestine.Model.Trailer;
import in.nikhil.cinestine.R;

/**
 * Created by nikhil on 28-08-2016.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

  private ArrayList<Review> ReviewList = new ArrayList<Review>();
  private LayoutInflater inflater;
  private Context context;

  public ReviewAdapter(Context context) {
    inflater = LayoutInflater.from(context);
    this.context = context;
  }

  public void setReviewsList(ArrayList<Review> reviewList) {
    ReviewList = reviewList;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = inflater.inflate(R.layout.recycler_review_single_row, parent, false);
    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    final Review current = ReviewList.get(position);
    String author = context.getString(R.string.author) + current.getAuthor();
    holder.author.setText(author);
    holder.content.setText(current.getContent());
  }

  @Override
  public int getItemCount() {
    return ReviewList.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder {

    TextView author;
    TextView content;

    MyViewHolder(View itemView) {
      super(itemView);
      author = itemView.findViewById(R.id.review_single_row_author);
      content = itemView.findViewById(R.id.review_single_row_content);
    }
  }
}
