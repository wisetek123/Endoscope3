package com.example.endoscope.adapter;

import java.io.File;
import java.util.List;

import com.example.endoscope.R;
import com.example.logic.CacheView;
import com.example.logic.Img;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


//查看图片时的适配器
public class PageAdapter extends BaseAdapter {
	private List<Img> list;
	LayoutInflater inflater;

	public PageAdapter(Context context, List<Img> list) {
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CacheView cacheView;
		
		Bitmap bm;
		if (convertView == null) {
			convertView=inflater.inflate(R.layout.item_page,null);
			cacheView=new CacheView();
			cacheView.tv_des=(TextView) convertView.findViewById(R.id.tv_des);
			cacheView.imgv_img=(ImageView)convertView.findViewById(R.id.imageView1);
			convertView.setTag(cacheView);
			
		}
		else
		{
			cacheView=(CacheView)convertView.getTag();
		}
		bm = BitmapFactory.decodeFile(list.get(position).getImgurl());
		cacheView.tv_des.setText(list.get(position).getDes());
		if(bm!=null){
		cacheView.imgv_img.setImageBitmap(bm);
		}
		return convertView;
	}
	
	

}
