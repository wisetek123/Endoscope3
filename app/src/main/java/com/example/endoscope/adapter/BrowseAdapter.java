package com.example.endoscope.adapter;

import java.util.List;

import android.content.Context;

import com.example.endoscope.R;
import com.example.endoscope.bean.BrowseBean;
import com.example.endoscope.utils.CommonAdapter;
import com.example.endoscope.utils.ViewHolder;

//目录适配器
public class BrowseAdapter extends CommonAdapter<BrowseBean> {

	public BrowseAdapter(Context context, List<BrowseBean> datas) {
		super(context, datas, R.layout.browse_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void convert(ViewHolder holder, BrowseBean bean) {
		holder.setText(R.id.type, bean.getType()).setText(R.id.num,
				bean.getNum());
	}
}
