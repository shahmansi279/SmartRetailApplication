package com.project;

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
public class CustomListAdapter extends ArrayAdapter<StoreInfo> implements
		Filterable {

	Context context;
	int layoutResourceId;
	ArrayList<StoreInfo> stores = new ArrayList<StoreInfo>();
	ArrayList<StoreInfo> filteredstores = new ArrayList<StoreInfo>();
	public LayoutInflater mInflater;
	private ItemFilter mFilter = new ItemFilter();
	int col;
	int textCol;

	// ColorDrawable col = new ColorDrawable(0xFFFF4444);
	public CustomListAdapter(Context context, int layoutResourceId,
			ArrayList<StoreInfo> stores) {

		super(context, layoutResourceId, stores);
		Log.v("Adap", "In Adap");
		this.context = context;
		this.stores = stores;
		this.filteredstores = stores;
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

			final ArrayList<StoreInfo> list = stores;

			int count = stores.size();
			final ArrayList<StoreInfo> nlist = new ArrayList<StoreInfo>(count);

			String filterableString;

			for (int i = 0; i < count; i++) {
				filterableString = list.get(i).getStoreName();
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
			filteredstores = (ArrayList<StoreInfo>) results.values;
			notifyDataSetChanged();
		}

	}

	@Override
	public int getCount() {

		return this.filteredstores.size();
	}

	public StoreInfo getItem(int position) {
		return (filteredstores.get(position));
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
			holder.storeAddr = (TextView) row.findViewById(R.id.storeAddr);
			holder.txtTitle = (TextView) row.findViewById(R.id.storeText);

			row.setTag(holder);
		} else {

			holder = (PlaceHolder) row.getTag();
		}

		if (selectedPos == position) {
			holder.txtTitle.setTextColor(textCol);
			holder.storeAddr.setTextColor(textCol);
			row.setBackgroundColor(col);

		} else {
			holder.txtTitle.setTextColor(Color.BLACK);
			holder.storeAddr.setTextColor(Color.BLACK);
			row.setBackgroundColor(Color.WHITE);
		}
		StoreInfo storeItem = (StoreInfo) filteredstores.get(position);
		System.out.println("position" + position);

		holder.txtTitle.setText(storeItem.getStoreName());
		holder.storeAddr.setText(storeItem.getStoreAddr());

		String url = storeItem.getStoreUrl();

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
		TextView storeAddr;
	}


}