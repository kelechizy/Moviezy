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
import com.serigon.moviezy.activity.CastActivityFragment;
import com.serigon.moviezy.utility.CursorRecyclerViewAdapter;

/**
 * Created by Kelechi on 3/11/2016.
 */
public class CastAdapter extends CursorRecyclerViewAdapter<CastAdapter.ViewHolder> {
    private final Context mContext;

    public CastAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(CastAdapter.ViewHolder viewHolder, Cursor cursor) {
        viewHolder.nameTextView.setText(cursor.getString(CastActivityFragment.COL_CAST_NAME));
        viewHolder.characterTextView.setText(cursor.getString(CastActivityFragment.COL_CAST_CHARACTER));

        String profileImage = cursor.getString(CastActivityFragment.COL_CAST_PROFILE_PATH);

        Glide.with(mContext)
                .load(profileImage)
                .crossFade()
                .into(viewHolder.profileView);
    }

    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cast_mini_list, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileView;
        TextView characterTextView;
        TextView nameTextView;

        public ViewHolder(View view) {
            super(view);
            profileView = (ImageView) view.findViewById(R.id.profileImageView);
            characterTextView = (TextView) view.findViewById(R.id.characterTextView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        }
    }
}