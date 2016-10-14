package com.example.endoscope;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.endoscope.adapter.ImgsAdapter;
import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;

//添加诊断信息功能
public class AddDiagnosisActivity extends Activity implements OnClickListener,
		topbarClickListener {

	private MyDatabaseHelper dbHelper;

	private Topbar mTopbar;

	private EditText Pno;
	private EditText Psex;
	private EditText Page;
	private EditText symptomDes;
	private EditText diagnosisRes;
	private Button saveBT;
	private ImageButton bt_addImage;
	private GridView pict;

	private String patientNo;
	private String pname;
	private String date;
	private String symptom;
	private String did;
	private String result;
	private String image;
	private Bitmap bmp;
	
	ImgsAdapter imgsAdapter;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<String> listfile = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_adddiagnosis);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		Pno = (EditText) findViewById(R.id.PnoET);
		Psex = (EditText) findViewById(R.id.PsexET);
		Page = (EditText) findViewById(R.id.PageET);
		symptomDes = (EditText) findViewById(R.id.symptom_desET);
		diagnosisRes = (EditText) findViewById(R.id.diagnosis_resET);
		saveBT = (Button) findViewById(R.id.addDiagnosisBT);
		bt_addImage = (ImageButton) findViewById(R.id.bt_add_image);
		pict = (GridView) findViewById(R.id.show_image);

		//读取上个页面传递过来的信息
		Intent diagnosis_it = getIntent();
		patientNo = diagnosis_it.getStringExtra("Pno");
		did = diagnosis_it.getStringExtra("Did");	
		dbHelper = new MyDatabaseHelper(AddDiagnosisActivity.this,
				"PatientManagement.db", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (patientNo != null) {
			
			Cursor pd_cursor = db.query("PD", null, "Pno = ?",
					new String[] { patientNo }, null, null, null);
			pd_cursor.moveToFirst();
			Pno.setText(pd_cursor.getString(pd_cursor.getColumnIndex("No")));
			pd_cursor.close();
		} else {
			Bundle bundle = getIntent().getExtras();
			patientNo = bundle.getString("PatientNO");
			did = bundle.getString("Did");
			Cursor pd_cursor = db.query("PD", null, "Pno = ?",
					new String[] { patientNo }, null, null, null);
			pd_cursor.moveToFirst();
			Pno.setText(pd_cursor.getString(pd_cursor.getColumnIndex("No")));
			pd_cursor.close();
			if (bundle != null) {
				if (bundle.getStringArrayList("files") != null) {
					listfile = bundle.getStringArrayList("files");
					imgsAdapter = new ImgsAdapter(this, listfile, null);
					pict.setAdapter(imgsAdapter);
					symptom = bundle.getString("Symptom");
					symptomDes.setText(symptom);
					Log.e("11",symptom);
					result = bundle.getString("Result");
					Log.e("22",result);
					diagnosisRes.setText(result);
					Log.e("hhh", patientNo);
				}else
				{
					symptom = bundle.getString("Symptom");
					symptomDes.setText(symptom);
					Log.e("11",symptom);
					result = bundle.getString("Result");
					Log.e("22",result);
					diagnosisRes.setText(result);
				}
			}
		}


		//根据PatientNo获取病人信息
		Cursor patient_diagnosis_cursor = db.query("Patient", null, "Pno = ?",
				new String[] { patientNo }, null, null, null);
		patient_diagnosis_cursor.moveToFirst();
		mTopbar.setToptitle(patient_diagnosis_cursor
				.getString(patient_diagnosis_cursor.getColumnIndex("Pname")));
		pname = patient_diagnosis_cursor.getString(patient_diagnosis_cursor
				.getColumnIndex("Pname"));
		Psex.setText(patient_diagnosis_cursor
				.getString(patient_diagnosis_cursor.getColumnIndex("Psex")));
		Page.setText(patient_diagnosis_cursor
				.getString(patient_diagnosis_cursor.getColumnIndex("Page")));
		patient_diagnosis_cursor.close();

		//设置左边按钮显示的图片
		mTopbar.setLeftBT(R.drawable.back);

		//显示左边按钮，隐藏右边按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(false);

		mTopbar.setOnTopbarClickListener(this);
		saveBT.setOnClickListener(this);
		bt_addImage.setOnClickListener(this);
	}

	
	//以缩略图形式显示图片
	public String translateList(ArrayList<String> list) {
		String img = "";
		String[] imgArray = (String[]) list.toArray(new String[0]);
		for (int i = 0; i < imgArray.length; i++) {
			img = img + imgArray[i] + ";";
		}
		return img;

	}

	
	//点击各按钮时，触发的事件
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		
		//添加图片按钮
		case R.id.bt_add_image: 
			symptom = symptomDes.getText().toString();
			result = diagnosisRes.getText().toString();
			Bundle bundle = new Bundle();
			bundle.putString("Did", did);
			bundle.putString("pname", pname);
			bundle.putString("paNO", patientNo);
			bundle.putString("symptom", symptom);
			bundle.putString("result", result);
			Intent it = new Intent(AddDiagnosisActivity.this,
					ImgsActivity.class);
			it.putExtras(bundle);
			startActivity(it);
			finish();
			break;

			
		//添加诊断信息按钮
		case R.id.addDiagnosisBT:
			date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.getDefault()).format(new Date(System
					.currentTimeMillis()));
			symptom = symptomDes.getText().toString();
			result = diagnosisRes.getText().toString();
			image = translateList(listfile);

			if (!symptom.equals("")) {
				if (!result.equals("")) {
					dbHelper = new MyDatabaseHelper(AddDiagnosisActivity.this,
							"PatientManagement.db", null, 1);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("Pno", patientNo);
					values.put("Ddate", date);
					values.put("Dsymptom", symptom);
					values.put("Dresult", result);
					values.put("image", image);
					db.insert("Diagnosis", null, values);//将信息放入数据库
					values.clear();

					Intent diagnosis_it = new Intent(AddDiagnosisActivity.this,
							PatientActivity.class);
					diagnosis_it.putExtra("Pno", patientNo);
					diagnosis_it.putExtra("Did", did);
					startActivity(diagnosis_it);
					finish();
				} else {
					Toast.makeText(this, getString(R.string.inputDiagnosisResult),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, getString(R.string.inputSymptomDescription), Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
	}

	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}

		return mDateFormat.format(new Date(time));
	}
	
	
	//点击Topbar中左边按钮触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(AddDiagnosisActivity.this,
				PatientActivity.class);
		intent.putExtra("Pno", patientNo);
		intent.putExtra("Did", did);
		startActivity(intent);
		finish();
	}

	@Override
	public void rightClick() {
		// TODO Auto-generated method stub

	}

	
	//按返回键时触发的事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(AddDiagnosisActivity.this,
					PatientActivity.class);
			intent.putExtra("Pno", patientNo);
			intent.putExtra("Did", did);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
