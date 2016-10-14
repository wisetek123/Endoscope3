package com.example.endoscope.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.endoscope.R;
import com.example.endoscope.R.layout;
import com.example.endoscope.bean.BrowseListBean;
import com.example.endoscope.utils.CommonAdapter;
import com.example.endoscope.utils.ViewHolder;

//图片适配器
public class BrowseListAdapter extends CommonAdapter<BrowseListBean> {

	private Boolean isMulSelect = false;
	Context context;

	private List<String> selectid = new ArrayList<String>();

	public BrowseListAdapter(Context context, List<BrowseListBean> datas) {
		super(context, datas, R.layout.photowall_item);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public void setMulSelect(Boolean isMulSelect) {
		this.isMulSelect = isMulSelect;
	}

	public List<String> getlist() {
		return selectid;
	}

	public void clearlist() {
		selectid.clear();
	}

	@Override
	public void convert(ViewHolder holder, BrowseListBean bean) {
		String FileName = bean.getName();
		String FilePath = bean.getFilePath();
		if (FileName.indexOf(".mp4") != -1) {
			holder.setText(R.id.photo_name, FileName);
			FilePath = FilePath.replace(".mp4", ".JPEG");
			Bitmap pict = BitmapFactory.decodeFile(FilePath);
			if (pict != null) {
				pict = ThumbnailUtils.extractThumbnail(pict, 100, 100);
				holder.setImageBitmap(R.id.thumbnail, pict);
			}

		}
		if (FilePath.indexOf("/Image/") != -1
				&&FilePath.indexOf(".JPEG") != -1) {
			holder.setText(R.id.photo_name, FileName);
			Bitmap pict = BitmapFactory.decodeFile(FilePath);
			pict = ThumbnailUtils.extractThumbnail(pict, 100, 100);
			holder.setImageBitmap(R.id.thumbnail, pict);
		}

		if (isMulSelect) {
			final View view = holder.getConvertView();
			final CheckBox ceb = (CheckBox) view.findViewById(R.id.checkBoX);
			final TextView txt = (TextView) view.findViewById(R.id.photo_name);
			holder.setCheckBoxVisible(R.id.checkBoX);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (isMulSelect) {
						if (ceb.isChecked()) {
							ceb.setChecked(false);
							selectid.remove(txt.getText().toString());
						} else {
							ceb.setChecked(true);
							selectid.add(txt.getText().toString());
						}
					}
				}
			});

		}
		// view.setOnLongClickListener(new Onlongclick());

	}
}

// class Onlongclick implements OnLongClickListener {
//
// public boolean onLongClick(View v) {
// // TODO Auto-generated method stub
//
// isMulSelect = true;
// layout.setVisibility(View.VISIBLE);
// return true;
// }
// }

