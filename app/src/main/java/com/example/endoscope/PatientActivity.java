package com.example.endoscope;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import json.JSONParser;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endoscope.adapter.DiagnosisAdapter;
import com.example.endoscope.bean.ConstantBean;
import com.example.endoscope.bean.DiagnosisBean;
import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;

//病人信息页面
public class PatientActivity extends Activity implements OnClickListener,
		topbarClickListener, OnItemLongClickListener, OnItemClickListener {

	private Topbar mTopbar;

	private EditText Pno;
	private EditText Psex;
	private EditText Page;
	private Button check;
	private Button diagnosis;
	private Button browse;
	private TextView sign;

	private ListView mListView;
	private LinkedList<DiagnosisBean> mListItems;
	// private List<PatientBean> mDatas;
	private DiagnosisAdapter mAdapter;
	private DiagnosisBean bean;

	private MyDatabaseHelper dbHelper;

	private String patientName;
	private String patientNo;
	private String did;
	

	private String pno;
	private String pid;
	private String pname;
	private String psex;
	private String page;
	private String pidnum;
	private String padr;
	private String ddate;
	private String dsymptom;
	private String dresult;
	private String[] imagePath;
	private int judge;
	private String path = ConstantBean.Path;
	private String image;
	private String videoPath;
	private static String url = "http://114.215.148.227/android/json/"
			+ "patient_handle.php";
	JSONParser jsonParser = new JSONParser();
	// private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_patient);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		Pno = (EditText) findViewById(R.id.PnoET);
		Psex = (EditText) findViewById(R.id.PsexET);
		Page = (EditText) findViewById(R.id.PageET);
		check = (Button) findViewById(R.id.check);
		diagnosis = (Button) findViewById(R.id.diagnosis);
		browse = (Button)findViewById(R.id.browse);
		sign = (TextView) findViewById(R.id.diagnosisSign);

		mListView = (ListView) findViewById(R.id.diagnosis_listview);

		//获取上个页面传递过来的信息
		Intent diagnosis_it = getIntent();
		patientNo = diagnosis_it.getStringExtra("Pno");
		did = diagnosis_it.getStringExtra("Did");
		
		
		//读取病人的信息
		dbHelper = new MyDatabaseHelper(PatientActivity.this,
				"PatientManagement.db", null, 1);

		mListItems = new LinkedList<DiagnosisBean>();
		// mDatas = new ArrayList<PatientBean>();

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor patient_cursor = db.query("Patient", null, "Pno = ?",
				new String[] { patientNo }, null, null, null);
		Cursor pd_cursor = db.query("PD", null, "Pno = ?",
				new String[] { patientNo }, null, null, null);
		pd_cursor.moveToFirst();
		Pno.setText(pd_cursor.getString(pd_cursor.getColumnIndex("No")));
		pd_cursor.close();
		patient_cursor.moveToFirst();
		pno = patientNo;
		pid = patient_cursor.getString(patient_cursor.getColumnIndex("Pid"));

		pname = patient_cursor
				.getString(patient_cursor.getColumnIndex("Pname"));
		patientName = patient_cursor.getString(patient_cursor
				.getColumnIndex("Pname"));
		mTopbar.setToptitle(patientName);
		
		psex = patient_cursor.getString(patient_cursor.getColumnIndex("Psex"));

		Psex.setText(patient_cursor.getString(patient_cursor
				.getColumnIndex("Psex")));
		page = patient_cursor.getString(patient_cursor.getColumnIndex("Page"));

		Page.setText(patient_cursor.getString(patient_cursor
				.getColumnIndex("Page")));

		pidnum = patient_cursor.getString(patient_cursor
				.getColumnIndex("PIDnum"));
		padr = patient_cursor.getString(patient_cursor.getColumnIndex("Padr"));
		judge = patient_cursor.getInt(patient_cursor.getColumnIndex("Judge"));
		patient_cursor.close();

		Cursor diagnosis_cursor = db.query("Diagnosis", null, "Pno = ?",
				new String[] { patientNo }, null, null, null);
		if (diagnosis_cursor.moveToFirst()) {
			sign.setVisibility(View.GONE);
			do {

				String date = diagnosis_cursor.getString(diagnosis_cursor
						.getColumnIndex("Ddate"));
				ddate = diagnosis_cursor.getString(diagnosis_cursor
						.getColumnIndex("Ddate"));
				String symptom = diagnosis_cursor.getString(diagnosis_cursor
						.getColumnIndex("Dsymptom"));
				dsymptom = diagnosis_cursor.getString(diagnosis_cursor
						.getColumnIndex("Dsymptom"));
				String result = diagnosis_cursor.getString(diagnosis_cursor
						.getColumnIndex("Dresult"));
				dresult = diagnosis_cursor.getString(diagnosis_cursor
						.getColumnIndex("Dresult"));
				String image = diagnosis_cursor.getString(diagnosis_cursor
						.getColumnIndex("image"));
				if (image != null) {
					bean = new DiagnosisBean(date, symptom, result, image);
					imagePath = image.split(";");
				} else {
					bean = new DiagnosisBean(date, symptom, result);
				}
				// mDatas.add(bean);
				mListItems.addFirst(bean);
			} while (diagnosis_cursor.moveToNext());
		}
		diagnosis_cursor.close();
		
		//判断是否保存过图片或者视频
		if(judge ==0)
		{
			browse.setVisibility(View.GONE);
		}
		else
		{
			browse.setVisibility(View.VISIBLE);
		}

		mAdapter = new DiagnosisAdapter(this, mListItems);
		// mAdapter = new PatientAdapter(this, mDatas);

		mListView.setAdapter(mAdapter);

		// 设置Topbar中按钮的图片
		mTopbar.setLeftBT(R.drawable.back);
		mTopbar.setRightBT(R.drawable.personal);

		// 显示Topbar中的按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(true);

		mTopbar.setOnTopbarClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		check.setOnClickListener(this);
		diagnosis.setOnClickListener(this);
		browse.setOnClickListener(this);
		path = path + File.separator + pname + "_" + pno;
		image = path + File.separator + "Image";
		videoPath = path + File.separator + "Video";
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		//点击检测按钮触发的事件
		case R.id.check:
			//弹出提示框
			Dialog dialog = new AlertDialog.Builder(PatientActivity.this)
			.setTitle(getString(R.string.prompt))
			.setMessage(getString(R.string.selectmode))
			.setPositiveButton(getString(R.string.wifi),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Intent check_it = new Intent(PatientActivity.this,
							CameraActivity.class);
							check_it.putExtra("Pname", patientName);
							check_it.putExtra("Pno", patientNo);
							check_it.putExtra("Did", did);
							startActivity(check_it);
							PatientActivity.this.finish();

						}
					})
			.setNegativeButton(getString(R.string.usb),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {

							Intent show_it = new Intent(PatientActivity.this,SelectActivity.class);
							
							show_it.putExtra("Pno", patientNo);
							show_it.putExtra("Did", did);
							show_it.putExtra("Pname", patientName);

							startActivity(show_it);
							finish();
						}
					}).create();
			dialog.show();

			break;

		//点击诊断按钮时触发的事件
		case R.id.diagnosis:
			Intent diagnosis_it = new Intent(PatientActivity.this,
					AddDiagnosisActivity.class);
			diagnosis_it.putExtra("Pno", patientNo);
			diagnosis_it.putExtra("Did", did);
			startActivity(diagnosis_it);
			finish();
			break;
			
		//点击浏览按钮时触发的事件
		case R.id.browse:

			Intent browse_it = new Intent(PatientActivity.this,
					BrowseActivity.class);
			browse_it.putExtra("imagePath", image);
			browse_it.putExtra("videoPath", videoPath);
			startActivity(browse_it);
			break;
			

		default:
			break;
		}
	}
	
	
	//点击左边按钮触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(PatientActivity.this,MainActivity.class);
		intent.putExtra("Did", did);
		startActivity(intent);
		finish();
	}

	//点击右边按钮触发的事件
	@Override
	public void rightClick() {
		// TODO Auto-generated method stub
		Intent update_it = new Intent(PatientActivity.this,
				UpdatePatientActivity.class);
		update_it.putExtra("Pno", patientNo);
		update_it.putExtra("Did",did);
		startActivity(update_it);
		finish();
	}

	//长按诊断信息时触发的事件
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
			final int arg2, long arg3) {
		// TODO Auto-generated method stub
		Dialog dialog = new AlertDialog.Builder(PatientActivity.this)
				.setTitle(getString(R.string.prompt))
				.setMessage(getString(R.string.isUpload))
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						new Upload().execute();
						

					}
				})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.show();
		return true;
	}
	
	//按返回键时触发的事件
	public boolean onKeyDown(int keyCode, KeyEvent event){


		
		Intent intent = new Intent(PatientActivity.this,MainActivity.class);
		intent.putExtra("Did", did);
		startActivity(intent);
		finish();
		return true;
	}

	
	//上传功能
	class Upload extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(PatientActivity.this);
			pDialog.setMessage(getString(R.string.uploading)+"...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Birthday();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pno", pno));
			params.add(new BasicNameValuePair("Pid", pid));
			params.add(new BasicNameValuePair("Pname", pname));
			params.add(new BasicNameValuePair("Psex", psex));
			params.add(new BasicNameValuePair("Page", page));
			params.add(new BasicNameValuePair("Pidnum", pidnum));
			params.add(new BasicNameValuePair("Padr", padr));
			params.add(new BasicNameValuePair("Ddate", ddate));
			params.add(new BasicNameValuePair("Dsymptom", dsymptom));
			params.add(new BasicNameValuePair("Dresult", dresult));

			try {
				JSONObject json = jsonParser.makeHttpRequset(url, "POST",
						params);
				// String message;
				// int success = json.getInt(TAG_SUCCESS);
				/*
				 * if (success == 1) { // Intent intent = new
				 * Intent(RegistActivity.this, // LoginActivity.class); //
				 * startActivity(intent); message = "上传成功！"; } else { message =
				 * "上传失败，请重新上传!"; }
				 */
				String message = json.getString(TAG_MESSAGE);
				return message;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}

		}

		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();
		}
	}

	
	//点击诊断信息时触发的事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		TextView  diagnosis_date= (TextView) v.findViewById(R.id.date);
		TextView  description= (TextView) v.findViewById(R.id.symptom);
		TextView  result= (TextView) v.findViewById(R.id.result);
		
		Intent intent = new Intent(PatientActivity.this,UpdateDiagnosisActivity.class);
		intent.putExtra("Pno", patientNo);
		intent.putExtra("Did",did);
		intent.putExtra("diagnosis_date",diagnosis_date.getText());
		intent.putExtra("description",description.getText());
		intent.putExtra("result",result.getText());
		intent.putExtra("imagePath",imagePath);
		
		
		finish();
		startActivity(intent);
	}

}
