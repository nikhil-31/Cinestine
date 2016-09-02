package in.nikhil.cinestine.Adapters;

import android.content.Context;
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
    private Context context;

    private LayoutInflater inflater;

    public ReviewAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    public void setReviewsList(ArrayList<Review> reviewList){
        ReviewList = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.recycler_review_single_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Review current = ReviewList.get(position);
        holder.author.setText("Author: "+current.getAuthor());
        holder.content.setText(current.getContent());
    }

    @Override
    public int getItemCount() {
        return ReviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView content;

        public MyViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.review_single_row_author);
            content = (TextView) itemView.findViewById(R.id.review_single_row_content);
        }
    }
}
