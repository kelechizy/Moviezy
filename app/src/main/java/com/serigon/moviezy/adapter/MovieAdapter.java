package com.serigon.moviezy.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.serigon.movietrend.R;
import com.serigon.moviezy.activity.DetailActivity;
import com.serigon.moviezy.activity.MovieFragment;
import com.serigon.moviezy.data.MovieContract;
import com.serigon.moviezy.utility.RecyclerItemClickListener;

/**
 * Created by Kelechi on 10/9/2015.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements View.OnClickListener {
    final private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private static LayoutInflater inflater;
    private RecyclerItemClickListener.OnItemClickListener onItemClickListener;

    public MovieAdapter(Context context,Cursor cursor) {
        mContext = context;
        mDataValid = cursor != null;
        this.inflater = LayoutInflater.from(context);
    }


    public void setOnItemClickListener(final RecyclerItemClickListener.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = this.inflater.inflate(R.layout.movie_list, parent, false);

        ViewHolder vh = new ViewHolder(itemView, new IMyViewHolderClicks(){
            @Override
            public void onPotato(View caller) {
//                Intent intent = new Intent(mContext, DetailActivity.class)
//                        .putExtra("MOVIE_ID", mCursor.getLong(MovieFragment.COL_MOVIE_ID))
//                        .putExtra("MOVIE_TITLE", mCursor.getString(MovieFragment.COL_MOVIE_TITLE));

//                Log.e("HEERE",""+mCursor.getLong(MovieFragment.COL_MOVIE_ID));
//
//                    ((MovieFragment.Callback) mContext).onItemSelected(MovieContract.MovieEntry.buildMovieUri(
//                            mCursor.getInt(MovieFragment.COL_MOVIE_ID)));

                Uri contentUri = MovieContract.MovieEntry.buildMovieUri(mCursor.getLong(MovieFragment.COL_MOVIE_ID));

                Intent intent = new Intent(mContext, DetailActivity.class)
                        .setData(contentUri);
                mContext.startActivity(intent);


            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Cursor cursor = this.getItem(position);

        String posterImage = cursor.getString(MovieFragment.COL_MOVIE_POSTER);

        Glide.with(mContext)
                .load(posterImage)
                .placeholder(R.drawable.movieholder_dark)
                .crossFade()
                .into(viewHolder.posterView);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public Cursor getItem(final int position)
    {
        if (mCursor != null && !mCursor.isClosed())
        {
            mCursor.moveToPosition(position);
        }

        return mCursor;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public void onClick(View view) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView posterView;
        public IMyViewHolderClicks mListener;

        public ViewHolder(View view, IMyViewHolderClicks listener) {
            super(view);
            posterView = (ImageView) view.findViewById(R.id.row_movie_image);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int movieColumnIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            //mClickHandler.onClick(mCursor.getLong(movieColumnIndex), this);
            Log.e("HEERE","HERRRR");
            mListener.onPotato(view);
        }

    }

    public static interface IMyViewHolderClicks {
        public void onPotato(View caller);
    }
}
