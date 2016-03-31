package com.serigon.moviezy.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serigon.movietrend.R;
import com.serigon.moviezy.activity.DetailActivityFragment;

import java.util.ArrayList;

/**
 * Created by Kelechi on 3/11/2016.
 */
public class CastMiniAdapter extends CursorAdapter {

    Context context;
    ArrayList<String> itemList = new ArrayList<String>();
    private static LayoutInflater inflater = null;

    public CastMiniAdapter(Context Context) {
        super(Context, null, false);
        context = Context;
        inflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.cast_mini_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.profileView = (ImageView) convertView.findViewById(R.id.profileImageView);
            viewHolder.characterTextView = (TextView) convertView.findViewById(R.id.characterTextView);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        viewHolder.nameTextView.setText(cursor.getString(DetailActivityFragment.COL_CAST_NAME));
        viewHolder.characterTextView.setText(cursor.getString(DetailActivityFragment.COL_CAST_CHARACTER));

        String url = cursor.getString(DetailActivityFragment.COL_CAST_PROFILE_PATH);

        Glide.with(context)
                .load(url)
                .crossFade()
                .into(viewHolder.profileView);

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_mini_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.nameTextView.setText(cursor.getString(DetailActivityFragment.COL_CAST_NAME));
        viewHolder.characterTextView.setText(cursor.getString(DetailActivityFragment.COL_CAST_CHARACTER));
        String profileImage = cursor.getString(DetailActivityFragment.COL_CAST_PROFILE_PATH);

        Glide.with(mContext)
                .load(profileImage)
                .crossFade()
                .into(viewHolder.profileView);

    }


    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void add(String path) {
        itemList.add(path);
    }

    private static class ViewHolder {
        ImageView profileView;
        TextView characterTextView;
        TextView nameTextView;

        public ViewHolder(View view) {
            profileView = (ImageView) view.findViewById(R.id.profileImageView);
            characterTextView = (TextView) view.findViewById(R.id.characterTextView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        }

        public ViewHolder() {}
    }
}
