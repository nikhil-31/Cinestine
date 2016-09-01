package in.nikhil.cinestine.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.nikhil.cinestine.Data.Favourite;
import in.nikhil.cinestine.R;
import io.realm.RealmResults;

/**
 * Created by nikhil on 01-09-2016.
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavViewHolder> {

    private Context context;

    private LayoutInflater inflater;
    private RealmResults<Favourite> mItems;

    public FavouriteAdapter(Context context,RealmResults<Favourite> results) {
        mItems = results;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void  update(RealmResults<Favourite> results){
        mItems = results;
        notifyDataSetChanged();
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.custom_grid_popular, parent, false);
        FavViewHolder holder = new FavViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FavViewHolder holder, int position) {
        Favourite fav = mItems.get(position);
        holder.text.setText(fav.getmOriginalTitle());
        Picasso.with(context)
                .load(fav.getmPosterPath())
                .into(holder.image);


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class FavViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView text;
        public FavViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.popular_image);
            text = (TextView) itemView.findViewById(R.id.popular_text);


        }
    }
}
