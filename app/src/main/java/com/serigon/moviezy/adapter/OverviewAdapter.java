package com.serigon.moviezy.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.serigon.movietrend.R;
import com.serigon.moviezy.activity.DetailActivityFragment;

import java.util.ArrayList;

/**
 * Created by Kelechi on 3/12/2016.
 */
public class OverviewAdapter extends CursorAdapter {
    Context context;
    ArrayList<String> itemList = new ArrayList<String>();
    private static LayoutInflater inflater = null;

    public OverviewAdapter(Context Context) {
        super(Context, null, false);
        context = Context;
        inflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.overview_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.overviewTextView = (TextView) convertView.findViewById(R.id.overviewTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        viewHolder.overviewTextView.setText(cursor.getString(DetailActivityFragment.COL_MOVIE_OVERVIEW));

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.overview_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.overviewTextView.setText(cursor.getString(DetailActivityFragment.COL_MOVIE_OVERVIEW));
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
        TextView overviewTextView;

        public ViewHolder(View view) {
            overviewTextView = (TextView) view.findViewById(R.id.overviewTextView);
        }

        public ViewHolder() {}
    }

}
