package com.example.endoscope.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images.Thumbnails;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.endoscope.PatientActivity;
import com.example.endoscope.R;
import com.example.endoscope.bean.DiagnosisBean;
import com.example.endoscope.utils.CommonAdapter;
import com.example.endoscope.utils.ViewHolder;

//诊断信息适配器
public class DiagnosisAdapter extends CommonAdapter<DiagnosisBean> {
	Context context;

	public DiagnosisAdapter(Context context, List<DiagnosisBean> datas) {
		super(context, datas, R.layout.diagnosis_item);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void convert(ViewHolder holder, DiagnosisBean bean) {

		holder.setText(R.id.date, bean.getDate())
				.setText(R.id.symptom, bean.getSymptom())
				.setText(R.id.result, bean.getResult());
		String image = bean.getImage();
		if (image != null) {
			String spStr[] = image.split(";");
			if (spStr.length != 0) {
				Bitmap pict;
				for (int i = 0; i < spStr.length; i++) {
					pict = BitmapFactory.decodeFile(spStr[i]);
					pict = ThumbnailUtils.extractThumbnail(pict, 100, 100);
					if (i == 0)
						holder.setImageBitmap(R.id.pict_one, pict);
					if (i == 1)
						holder.setImageBitmap(R.id.pict_two, pict);
					if (i == 2)
						holder.setImageBitmap(R.id.pict_three, pict);
				}

			}
		}

	}

}
