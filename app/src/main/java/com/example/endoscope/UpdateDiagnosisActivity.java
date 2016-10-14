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


//更新诊断信息功能
public class UpdateDiagnosisActivity extends Activity implements OnClickListener,
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
	//private String symptom1;
	private String result;
	private String rel;
	private String diagnosis_date;
	private String description;
	private String[] imagePath;
	//private String result1;
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

		//获取上个页面传递的信息
		Intent intent = getIntent();
		patientNo = intent.getStringExtra("Pno");
		did =intent.getStringExtra("Did");
		diagnosis_date=intent.getStringExtra("diagnosis_date");
		description=intent.getStringExtra("description");
		imagePath=intent.getStringArrayExtra("imagePath");
		rel=intent.getStringExtra("result");
		dbHelper = new MyDatabaseHelper(UpdateDiagnosisActivity.this,
				"PatientManagement.db", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (patientNo != null) {
			
			Cursor pd_cursor = db.query("PD", null, "Pno = ?",
					new String[] { patientNo }, null, null, null);
			pd_cursor.moveToFirst();
			Pno.setText(pd_cursor.getString(pd_cursor.getColumnIndex("No")));
			pd_cursor.close();
			symptomDes.setText(description);
			diagnosisRes.setText(rel);
			if(imagePath.length>0)
			{
				for (int i = 0; i < imagePath.length; i++) {
					listfile.add(imagePath[i]);
				}
				imgsAdapter = new ImgsAdapter(this, listfile, null);
				pict.setAdapter(imgsAdapter);
			}
		} else {
			Bundle bundle = getIntent().getExtras();
			patientNo = bundle.getString("PatientNO");
			did = bundle.getString("Did");
			diagnosis_date=bundle.getString("date");
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

		// 设置Topbar左边按钮的图片
		mTopbar.setLeftBT(R.drawable.back);

		// 显示左边按钮，隐藏右边按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(false);

		mTopbar.setOnTopbarClickListener(this);
		saveBT.setOnClickListener(this);
		bt_addImage.setOnClickListener(this);
	}

	public String translateList(ArrayList<String> list) {
		String img = "";
		String[] imgArray = (String[]) list.toArray(new String[0]);
		for (int i = 0; i < imgArray.length; i++) {
			img = img + imgArray[i] + ";";
		}
		return img;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		//点击添加图片触发的事件
		case R.id.bt_add_image:
			symptom = symptomDes.getText().toString();
			result = diagnosisRes.getText().toString();
			Bundle bundle = new Bundle();
			bundle.putString("pname", pname);
			bundle.putString("paNO", patientNo);
			bundle.putString("Did",did);
			bundle.putString("symptom", symptom);
			bundle.putString("result", result);
			bundle.putString("date", diagnosis_date);
			Intent it = new Intent(UpdateDiagnosisActivity.this,
					UpdateImage.class);
			it.putExtras(bundle);
			startActivity(it);
			finish();
			break;

		//点击添加诊断信息触发的事件
		case R.id.addDiagnosisBT:
			date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.getDefault()).format(new Date(System
					.currentTimeMillis()));
			symptom = symptomDes.getText().toString();
			result = diagnosisRes.getText().toString();
			image = translateList(listfile);

			if (!symptom.equals("")) {
				if (!result.equals("")) {
					dbHelper = new MyDatabaseHelper(UpdateDiagnosisActivity.this,
							"PatientManagement.db", null, 1);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("Pno", patientNo);
					values.put("Ddate", date);
					values.put("Dsymptom", symptom);
					values.put("Dresult", result);
					values.put("image", image);
					db.insert("Diagnosis", null, values);
					db.delete("Diagnosis","Ddate = ?", new String[]{diagnosis_date});
					values.clear();

					Intent diagnosis_it = new Intent(UpdateDiagnosisActivity.this,
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

	//点击左边按钮触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(UpdateDiagnosisActivity.this,
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

	//按返回键触发的事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(UpdateDiagnosisActivity.this,
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
