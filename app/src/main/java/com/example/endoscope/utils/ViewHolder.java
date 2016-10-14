package com.example.endoscope.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHolder {

	private SparseArray<View> mViews;

	private int mPosition;
	private View mConvertView;

	public ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		mConvertView.setTag(this);

	}

	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}

	}

	/*
	 * 通过viewId获取控件
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);

		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public View getConvertView() {
		return mConvertView;
	}

	/*
	 * 设置TextView的�??
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}

	public ViewHolder setImageResource(int viewId, int resId) {
		ImageView view = getView(viewId);
		view.setImageResource(resId);
		return this;
	}

	public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}

	public ViewHolder setImageURI(int viewId, int resId) {
		ImageView view = getView(viewId);
		// Imageloader.getInstance().loadImg(view, url);
		return this;
	}

	public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
		ImageView view = getView(viewId);
		view.setImageDrawable(drawable);
		return this;
	}
	
	public ViewHolder setCheckBoxVisible(int viewId)	
	{
		CheckBox checkview=getView(viewId);
		checkview.setVisibility(CheckBox.VISIBLE);
		return this;
		
	}
	
	public ViewHolder setProgressBar(int viewId,int num)
	{
		ProgressBar bar=getView(viewId);
		bar.setProgress(num);
		return this;
		
	}
	

}
