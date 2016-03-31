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
import com.serigon.moviezy.activity.DetailActivityFragment;

import java.util.ArrayList;

/**
 * Created by Kelechi on 3/10/2016.
 */
public class SimilarMovieAdapter extends CursorAdapter {
    Context context;
    ArrayList<String> itemList = new ArrayList<String>();
    private static LayoutInflater inflater = null;

    public SimilarMovieAdapter(Context Context) {
        super(Context, null, false);
        context = Context;
        inflater.from(context);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //imageView = viewHolder.posterView;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.similar_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.posterView = (ImageView) convertView.findViewById(R.id.similar_movie_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        ImageView imageView = viewHolder.posterView;
        String url = cursor.getString(DetailActivityFragment.COL_SIMILAR_POSTER_PATH);

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.movieholder_dark)
                .crossFade()
                .into(imageView);

        return convertView;
        //return super.getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.similar_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String posterImage = cursor.getString(DetailActivityFragment.COL_SIMILAR_POSTER_PATH);

        Glide.with(context)
                .load(posterImage)
                .placeholder(R.drawable.movieholder_dark)
                .crossFade()
                .into(viewHolder.posterView);

    }


    private static class ViewHolder {
        ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.similar_movie_image);
        }

        public ViewHolder() {

        }
    }

}
