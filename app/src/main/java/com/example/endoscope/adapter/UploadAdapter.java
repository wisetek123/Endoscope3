package com.example.endoscope.adapter;

import java.util.List;

import android.content.Context;

import com.example.endoscope.R;
import com.example.endoscope.bean.BrowseBean;
import com.example.endoscope.bean.UploadBean;
import com.example.endoscope.utils.CommonAdapter;
import com.example.endoscope.utils.ViewHolder;


//上传适配器
public class UploadAdapter extends CommonAdapter<UploadBean> {

	public UploadAdapter(Context context, List<UploadBean> datas) {
		super(context, datas, R.layout.upload_item);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void convert(ViewHolder holder, UploadBean bean) {
		holder.setText(R.id.uploadfile, bean.getuploadfile());
		holder.setProgressBar(R.id.uploadbar, bean.getnum());
	}
}
