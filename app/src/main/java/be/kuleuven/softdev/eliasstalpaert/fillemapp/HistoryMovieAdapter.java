package be.kuleuven.softdev.eliasstalpaert.fillemapp;

/*
* 2 things needed for RecyclerView
* - RecyclerView.Adapter
* - RecyclerView.ViewHolder
* */

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HistoryMovieAdapter extends RecyclerView.Adapter<HistoryMovieAdapter.HistoryMovieViewHolder> implements ItemTouchHelperAdapter {

    public static final String EXTRA_NOGENERATE = "be.kuleuven.softdev.eliasstalpaert.fillemapp.NOGENERATE";

    private Context hCtx; //needed for layoutinflater
    private List<HistoryMovie> movieList;
    private HistoryMovie movieBind;
    private Boolean history_enable;

    public HistoryMovieAdapter(Context hCtx, List<HistoryMovie> movieList, Boolean history_enable) {
        this.hCtx = hCtx;
        this.history_enable = history_enable;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public HistoryMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(hCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new HistoryMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryMovieViewHolder holder, int position) {
        movieBind = movieList.get(position);

        holder.title.setText(movieBind.getMovieTitle());
        holder.genre.setText(movieBind.getMovieGenre());
        holder.year.setText(movieBind.getMovieReleaseYear());
        holder.jsonString = movieBind.getJsonString();

        Picasso.get()
                .load(movieBind.getPosterUrl())
                .fit()
                .centerInside()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.poster);


    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(movieList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(movieList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) throws NullPointerException{
        if(history_enable){
            MainActivity.history.remove(movieList.get(position).getMovieImdbId());
            movieList.remove(position);
        }
        else{
            try {
                String id = MainActivity.watchList.get(position).getMovieImdbId();
                MainActivity.watchList.remove(position);
                Iterator<String> it = MainActivity.watchString.iterator();
                boolean found = false;
                while (it.hasNext() && !found) {
                    String next = it.next();
                    if (next.equals(id)) {
                        it.remove();
                        found = true;
                    }
                }

                MainActivity.saveWatchList(hCtx);
            }
            catch (NullPointerException e){
                notifyDataSetChanged();
            }
        }
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    class HistoryMovieViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{

        TextView title, genre, year;
        ImageView poster;
        String jsonString;

        ConstraintLayout cLayout;

        public HistoryMovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.findViews();
        }

        private void findViews(){
            title = itemView.findViewById(R.id.historyTitle);
            genre = itemView.findViewById(R.id.historyGenre);
            year = itemView.findViewById(R.id.historyYear);
            poster = itemView.findViewById(R.id.historyPoster);
            cLayout = itemView.findViewById(R.id.constraintLayoutHistory);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(hCtx, DisplayMovieActivity.class);

            intent.putExtra(MovieGenerator.EXTRA_JSONSTRING, this.jsonString);
            intent.putExtra(HistoryMovieAdapter.EXTRA_NOGENERATE, true);

            hCtx.startActivity(intent);
        }
    }
}
