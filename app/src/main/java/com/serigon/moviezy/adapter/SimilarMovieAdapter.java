package com.serigon.moviezy.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.serigon.movietrend.R;
import com.serigon.moviezy.activity.DetailActivityFragment;
import com.serigon.moviezy.utility.CursorRecyclerViewAdapter;

/**
 * Created by Kelechi on 3/10/2016.
 */
public class SimilarMovieAdapter extends CursorRecyclerViewAdapter<SimilarMovieAdapter.ViewHolder> {
    private final Context mContext;

    public SimilarMovieAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(SimilarMovieAdapter.ViewHolder viewHolder, Cursor cursor) {
        String posterImage = cursor.getString(DetailActivityFragment.COL_SIMILAR_POSTER_PATH);

        Glide.with(mContext)
                .load(posterImage)
                .crossFade()
                .into(viewHolder.posterView);
    }

    @Override
    public SimilarMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_similar, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterView;

        public ViewHolder(View view) {
            super(view);
            posterView = (ImageView) view.findViewById(R.id.similar_movie_image);
        }

    }

}
