package com.serigon.moviezy.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.serigon.movietrend.R;
import com.serigon.moviezy.activity.MovieFragment;

/**
 * Created by Kelechi on 10/9/2015.
 */
public class MovieAdapter extends CursorAdapter {
    Context context;
    private static LayoutInflater inflater = null;

    public MovieAdapter(Context Context) {
        super(Context, null, false);
        context = Context;
        inflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.movie_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.posterView = (ImageView) convertView.findViewById(R.id.row_movie_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        String posterImage = cursor.getString(MovieFragment.COL_MOVIE_POSTER);

        Glide.with(context)
                .load(posterImage)
                .placeholder(R.drawable.movieholder)
                .crossFade()
                .into(viewHolder.posterView);

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String posterImage = cursor.getString(MovieFragment.COL_MOVIE_POSTER);

        Glide.with(context)
                .load(posterImage)
                .placeholder(R.drawable.movieholder_dark)
                .crossFade()
                .into(viewHolder.posterView);
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.row_movie_image);
        }
        public ViewHolder() {
        }

    }

}
