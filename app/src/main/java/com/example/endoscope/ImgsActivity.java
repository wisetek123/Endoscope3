package com.example.endoscope;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.example.endoscope.AddDiagnosisActivity;
import com.example.endoscope.R;
import com.example.endoscope.bean.ConstantBean;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;
import com.example.logic.ImgsAdapter.OnItemClickClass;
import com.example.logic.*;


//在诊断信息中添加图片功能
public class ImgsActivity extends Activity implements  topbarClickListener {
	Topbar mTopbar;

	Bundle bund;
	FileTraversal fileTraversal;
	GridView imgGridView;
	ImgsAdapter imgsAdapter;
	LinearLayout select_layout;
	Util util;
	RelativeLayout relativeLayout2;
	HashMap<Integer, ImageView> hashImage;
	Button choise_button;
	String PatientNO, patientName,result,symptom,Did;
	List<String> locallist = new ArrayList<String>();
	ArrayList<String> filelist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photogrally);

		imgGridView = (GridView) findViewById(R.id.gridView1);
		select_layout = (LinearLayout) findViewById(R.id.selected_image_layout);
		relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
		choise_button = (Button) findViewById(R.id.button3);
		mTopbar = (Topbar)findViewById(R.id.id_topbar);

		
		//获取上个页面传过来的信息
		bund = getIntent().getExtras();
		Did=bund.getString("Did");
		PatientNO = bund.getString("paNO");
		patientName = bund.getString("pname");
		result = bund.getString("result");
		symptom = bund.getString("symptom");
		String folderPath = ConstantBean.Path + "/" + patientName + "_"
				+ PatientNO + "/" + "Image";
		util = new Util(this);
		locallist = util.listAlldir(folderPath);

		if (PatientNO != null)
			Log.e("PatientNO", PatientNO);
		if(!locallist.isEmpty()){
		imgsAdapter = new ImgsAdapter(this, locallist, onItemClickClass);
		imgGridView.setAdapter(imgsAdapter);
		}
		hashImage = new HashMap<Integer, ImageView>();
		filelist = new ArrayList<String>();
		// imgGridView.setOnItemClickListener(this);
		util = new Util(this);
		
		//设置Topbar中的文字信息
		mTopbar.setToptitle(getString(R.string.addPhoto));
		
		mTopbar.setOnTopbarClickListener(this);
		
		//设置Topbar中按钮的图片
		mTopbar.setLeftBT(R.drawable.back);
		mTopbar.setRightBT(R.drawable.ok);
		
		//显示Topbar中的按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(true);
	}

	class BottomImgIcon implements OnItemClickListener {

		int index;

		public BottomImgIcon(int index) {
			this.index = index;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

		}
	}

	@SuppressLint("NewApi")
	public ImageView iconImage(String filepath, int index, CheckBox checkBox)
			throws FileNotFoundException {
		LinearLayout.LayoutParams params = new LayoutParams(
				relativeLayout2.getMeasuredHeight() - 10,
				relativeLayout2.getMeasuredHeight() - 10);
		ImageView imageView = new ImageView(this);
		imageView.setLayoutParams(params);
		imageView.setBackgroundResource(R.drawable.imgbg);
		float alpha = 100;
		imageView.setAlpha(alpha);
		util.imgExcute(imageView, imgCallBack, filepath);
		imageView.setOnClickListener(new ImgOnclick(filepath, checkBox));
		return imageView;
	}

	ImgCallBack imgCallBack = new ImgCallBack() {
		@Override
		public void resultImgCall(ImageView imageView, Bitmap bitmap) {
			imageView.setImageBitmap(bitmap);
		}
	};

	//点击图片时触发的事件
	class ImgOnclick implements OnClickListener {
		String filepath;
		CheckBox checkBox;

		public ImgOnclick(String filepath, CheckBox checkBox) {
			this.filepath = filepath;
			this.checkBox = checkBox;
		}

		@Override
		public void onClick(View arg0) {
			checkBox.setChecked(false);
			select_layout.removeView(arg0);
			choise_button
					.setText("(" + select_layout.getChildCount() + ")");
			filelist.remove(filepath);
		}
	}

	
	//点击下方图片栏中图片时触发的事件
	ImgsAdapter.OnItemClickClass onItemClickClass = new OnItemClickClass() {
		@Override
		public void OnItemClick(View v, int Position, CheckBox checkBox) {
			String filapath = locallist.get(Position);
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				select_layout.removeView(hashImage.get(Position));
				filelist.remove(filapath);
				choise_button.setText("(" + select_layout.getChildCount()
						+ ")");
			} else {
				try {
					checkBox.setChecked(true);
					Log.i("img", "img choise position->" + Position);
					ImageView imageView = iconImage(filapath, Position,
							checkBox);
					if (imageView != null) {
						hashImage.put(Position, imageView);
						filelist.add(filapath);
						select_layout.addView(imageView);
						choise_button.setText("("
								+ select_layout.getChildCount() + ")");
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	};

	//点击左边按钮触发的事件
	public void leftClick() {
		Intent intent = new Intent(this, AddDiagnosisActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("files", null);
		bundle.putString("PatientNO", PatientNO);
		bundle.putString("Symptom", symptom);
		bundle.putString("Did",Did);
		bundle.putString("Result",result);
		
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	//点击右边按钮触发的事件
	public void rightClick() {
		if (PatientNO != null) {
			String spStr[] = PatientNO.split(";");
			if (spStr.length > 3) {
				//弹出提示框
				Dialog dialog = new AlertDialog.Builder(ImgsActivity.this)
						.setTitle(getString(R.string.prompt))
						.setMessage(getString(R.string.upToSelect))
						.setPositiveButton(getString(R.string.yes),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setNegativeButton(getString(R.string.no),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();
				dialog.show();
			} else {
				Intent intent = new Intent(this, AddDiagnosisActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("files", filelist);
				bundle.putString("PatientNO", PatientNO);
				bundle.putString("Symptom",symptom);
				bundle.putString("Result", result);
				bundle.putString("Did",Did);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		}

	}
	//按返回键时触发的事件
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, AddDiagnosisActivity.class);
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("files", null);
			bundle.putString("PatientNO", PatientNO);
			bundle.putString("Symptom", symptom);
			bundle.putString("Did",Did);
			bundle.putString("Result",result);
			
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}




}
