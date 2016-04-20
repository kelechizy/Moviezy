package com.serigon.moviezy.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serigon.movietrend.R;
import com.serigon.moviezy.activity.DetailActivityFragment;
import com.serigon.moviezy.utility.CursorRecyclerViewAdapter;

/**
 * Created by Kelechi on 3/9/2016.
 */
public class GenreAdapter extends CursorRecyclerViewAdapter<GenreAdapter.ViewHolder> {

    public GenreAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_genre, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String genre = cursor.getString(DetailActivityFragment.COL_GENRE_NAME);

        viewHolder.mTextView.setText(genre);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public LinearLayout mLayout;

        public ViewHolder(View view) {
            super(view);
            mLayout = (LinearLayout) view.findViewById(R.id.genreLayout);
            mTextView = (TextView) view.findViewById(R.id.genreTextView);
        }
    }
}