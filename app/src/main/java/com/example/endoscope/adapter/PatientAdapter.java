package com.example.endoscope.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.endoscope.R;
import com.example.endoscope.bean.PatientBean;
import com.example.endoscope.utils.CommonAdapter;
import com.example.endoscope.utils.ViewHolder;


//病人适配器
public class PatientAdapter extends CommonAdapter<PatientBean> {
	
	private Boolean isMulSelect=false;
	private List<String> selectid = new ArrayList<String>();

	public PatientAdapter(Context context, List<PatientBean> datas) {
		super(context, datas, R.layout.patient_item);
		// TODO Auto-generated constructor stub
	}

	public void setMulSelect(Boolean isMulSelect) {
		this.isMulSelect = isMulSelect;
	}

	public List<String> getlist() {
		return selectid;
	}
	
	public void clearlist()
	{
		selectid.clear();
	}
	public String convertToUTF(String str)
	{
		try {
			str=new String(str.getBytes("GB2312"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	@Override
	public void convert(ViewHolder holder, PatientBean bean) {
		holder.setText(R.id.no, bean.getNo())
				.setText(R.id.name, bean.getName())
				.setText(R.id.sex, bean.getSex())
				.setText(R.id.age, bean.getAge())
				.setText(R.id.tel, bean.getTel())
				.setText(R.id.adr, bean.getAdr())
				.setText(R.id.IDnum, bean.getIDnum());
		
		if (isMulSelect) {
			final View view = holder.getConvertView();
			final CheckBox ceb = (CheckBox) view.findViewById(R.id.chebox);
			final TextView txt = (TextView) view.findViewById(R.id.no);
			holder.setCheckBoxVisible(R.id.chebox);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stubw
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
	}

}
