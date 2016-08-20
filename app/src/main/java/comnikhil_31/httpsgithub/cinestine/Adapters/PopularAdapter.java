package comnikhil_31.httpsgithub.cinestine.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import comnikhil_31.httpsgithub.cinestine.Model.Movie;
import comnikhil_31.httpsgithub.cinestine.R;

/**
 * Created by nikhil on 20-08-2016.
*/
public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {

    private ArrayList<Movie> mMovie = new ArrayList<Movie>();
    private Context context;
    private ClickListener clickListener;
    private LayoutInflater inflater;

    public PopularAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setMoviesList(ArrayList<Movie> listmovies) {
        this.mMovie = listmovies;
        notifyItemRangeChanged(0, listmovies.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.custom_grid_popular, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Movie currentMovie = mMovie.get(position);
        holder.text.setText(currentMovie.getOriginalTitle());
        Picasso.with(context)
                .load(currentMovie.getPosterPath())
                .into(holder.image);


    }

    @Override
    public int getItemCount() {
        return mMovie.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView text;

        public MyViewHolder(View itemview) {
            super(itemview);

            image = (ImageView) itemview.findViewById(R.id.popular_image);
            text = (TextView) itemview.findViewById(R.id.popular_text);
            itemview.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


            if (clickListener !=null){
                clickListener.itemClicked(v,getAdapterPosition());
            }
        }
    }
    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        void itemClicked(View view,int position);
    }
}

