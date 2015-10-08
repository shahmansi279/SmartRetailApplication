package com.project;

/**
 * Created by jessiedeot on 10/7/15.
 */


import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CustomOffersListAdapter extends ArrayAdapter<OfferInfo> implements
        Filterable {

    Context context;
    int layoutResourceId;
    ArrayList<OfferInfo> offers = new ArrayList<OfferInfo>();
    ArrayList<OfferInfo> filteredoffers = new ArrayList<OfferInfo>();
    public LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    int col;
    int textCol;

    // ColorDrawable col = new ColorDrawable(0xFFFF4444);
    public CustomOffersListAdapter(Context context, int layoutResourceId,
                                   ArrayList<OfferInfo> offers) {

        super(context, layoutResourceId, offers);
        Log.v("Adap", "In Adap");
        this.context = context;
        this.offers = offers;
        this.filteredoffers = offers;
        this.layoutResourceId = layoutResourceId;
        this.col = (this.context).getResources().getColor(R.color.btn_bg);
        this.textCol = (this.context).getResources().getColor(R.color.white);
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<OfferInfo> list = offers;

            int count = offers.size();
            final ArrayList<OfferInfo> nlist = new ArrayList<OfferInfo>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getOfferTitle();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filteredoffers = (ArrayList<OfferInfo>) results.values;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {

        return this.filteredoffers.size();
    }

    public OfferInfo getItem(int position) {
        return (filteredoffers.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int selectedPos = 0;// -1 // init value for not-selected

    public void setSelectedPosition(int pos) {
        selectedPos = pos;
        // inform the view of this change
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPos;
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PlaceHolder holder;

        if (row == null) {
            mInflater = ((Activity) this.context).getLayoutInflater();

            System.out.println("Layout" + layoutResourceId);
            row = mInflater.inflate(layoutResourceId, parent, false);

            holder = new PlaceHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.icon);
            holder.OfferDesc = (TextView) row.findViewById(R.id.offerDesc);
            holder.txtTitle = (TextView) row.findViewById(R.id.offerTitle);

            row.setTag(holder);
        } else {

            holder = (PlaceHolder) row.getTag();
        }

        if (selectedPos == position) {
            holder.txtTitle.setTextColor(textCol);
            holder.OfferDesc.setTextColor(textCol);
            row.setBackgroundColor(col);

        } else {
            holder.txtTitle.setTextColor(Color.BLACK);
            holder.OfferDesc.setTextColor(Color.BLACK);
            row.setBackgroundColor(Color.WHITE);
        }
        OfferInfo OfferItem = (OfferInfo) filteredoffers.get(position);
        System.out.println("position" + position);

        holder.txtTitle.setText(OfferItem.getOfferTitle());
        holder.OfferDesc.setText(OfferItem.getOfferDesc());

        String url = OfferItem.getOfferIconUrl();

        if (holder.imgIcon != null) {
            // new ImageDownloaderTask(holder.imgIcon, position, url)
            // .execute();

            Picasso.with(getContext()).load(url).into(holder.imgIcon);

        }

        return row;
    }

    public static class PlaceHolder {
        public int position;
        ImageView imgIcon;
        TextView txtTitle;
        TextView OfferDesc;
    }


}
