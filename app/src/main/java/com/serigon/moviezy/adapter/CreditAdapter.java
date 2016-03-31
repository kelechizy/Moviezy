package com.serigon.moviezy.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serigon.movietrend.R;
import com.serigon.moviezy.activity.PersonActivityFragment;
import com.serigon.moviezy.utility.CursorRecyclerViewAdapter;

/**
 * Created by Kelechi on 3/9/2016.
 */

public class CreditAdapter extends CursorRecyclerViewAdapter<CreditAdapter.ViewHolder> {
    private final Context mContext;

    public CreditAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.credit_list, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.movieTextView.setText(cursor.getString(PersonActivityFragment.COL_CREDIT_TITLE));
        viewHolder.characterTextView.setText("as " + cursor.getString(PersonActivityFragment.COL_CREDIT_CHARACTER));

        String posterImage = cursor.getString(PersonActivityFragment.COL_CREDIT_POSTER_PATH);

        Glide.with(mContext)
                .load(posterImage)
                .crossFade()
                .into(viewHolder.posterImageView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView characterTextView;
        public TextView movieTextView;
        public ImageView posterImageView;

        public ViewHolder(View view) {
            super(view);
            posterImageView = (ImageView) view.findViewById(R.id.credit_poster);
            movieTextView = (TextView) view.findViewById(R.id.movieTextView);
            characterTextView = (TextView) view.findViewById(R.id.characterTextView);
        }
    }
}