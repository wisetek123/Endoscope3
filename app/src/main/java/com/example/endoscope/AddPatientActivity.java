package com.example.endoscope;

import java.util.UUID;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.DialerKeyListener;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;

//添加病人功能
public class AddPatientActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener, topbarClickListener {

	private MyDatabaseHelper dbHelper;

	private Topbar mTopbar;

	private EditText patient_nameET;
	private RadioGroup sexRG;
	private EditText ageET;
	private EditText telET;
	private EditText adrET;
	private EditText IDnumET;
	private Button addBT;

	private String Did;
	private String Pno;
	private String patientName;
	private String patientSex = "";
	private String patientAge;
	private String patientTel;
	private String patientAdr;
	private String patientIDnum;

	private int no;
//	private int judge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addpatient);

		init();
	}

	
	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		patient_nameET = (EditText) findViewById(R.id.patient_nameET);
		sexRG = (RadioGroup) findViewById(R.id.sexRG);
		ageET = (EditText) findViewById(R.id.ageET);
		ageET.setKeyListener(DialerKeyListener.getInstance());
		telET = (EditText) findViewById(R.id.telET);
		telET.setKeyListener(DialerKeyListener.getInstance());
		adrET = (EditText) findViewById(R.id.adrET);
		IDnumET = (EditText) findViewById(R.id.IDnumET);
		addBT = (Button) findViewById(R.id.addPatientBT);

		
		//获取上个页面传递的信息
		Intent intent = getIntent();
		Did = intent.getStringExtra("Did");
	
		//设置Topbar中间的文字信息
		mTopbar.setToptitle(getString(R.string.addPatient));

		//设置左边按钮显示的图片
		mTopbar.setLeftBT(R.drawable.back);

		//显示左边按钮，隐藏右边按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(false);

		mTopbar.setOnTopbarClickListener(this);
		addBT.setOnClickListener(this);
		sexRG.setOnCheckedChangeListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//获取编辑框中的信息
		patientName = patient_nameET.getText().toString();
		if (ageET.getText().toString().equals("")) {
			patientAge = "0";
		} else {
			patientAge = ageET.getText().toString();
		}
		patientTel = telET.getText().toString();
		patientAdr = adrET.getText().toString();
		patientIDnum = IDnumET.getText().toString();
		
		
		//保存新添加的病人信息
		if (!patientName.equals("")) {
			if (!patientSex.equals("")) {
				if (patientAge != "0") {
					dbHelper = new MyDatabaseHelper(AddPatientActivity.this,
							"PatientManagement.db", null, 1);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = null;
					cursor = db.query("PD", null, "Did = ? ", new String[]{Did},
							null, null, null);
					no = cursor.getCount() + 1;
					
					ContentValues values = new ContentValues();
					values.put("Pid", getUUID());
					values.put("Pname", patientName);
					values.put("Psex", patientSex);
					values.put("Page", patientAge);
					values.put("Ptel", patientTel);
					values.put("Padr", patientAdr);
					values.put("PIDnum", patientIDnum);
					values.put("Judge",0);
					
					db.insert("Patient", null, values);
					values.clear();
					//Log.e("patient",patientName+"/"+patientSex+"/"+patientTel+"/"+patientAdr);
					cursor = db.query("Patient", null, null, null, null,
							null, null);
					cursor.moveToLast();
					Pno = cursor.getString(cursor
							.getColumnIndex("Pno"));
					cursor.close();

					ContentValues pd_values = new ContentValues();
					pd_values.put("Did", Did);
					pd_values.put("Pno", Pno);
					pd_values.put("No", no);
					db.insert("PD", null, pd_values);
					pd_values.clear();

					Intent intent = new Intent(AddPatientActivity.this,
							MainActivity.class);
					intent.putExtra("Did", Did);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(this, getString(R.string.inputAge), Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(this, getString(R.string.inputSex), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, getString(R.string.inputName), Toast.LENGTH_SHORT).show();
		}

	}

	
	//性别按钮
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.male:
			patientSex = "男";
			break;

		case R.id.female:
			patientSex = "女";
			break;

		default:
			break;
		}
	}
	
	
	//点击左边按钮触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(AddPatientActivity.this, MainActivity.class);
		intent.putExtra("Did", Did);
		startActivity(intent);
		finish();
	}

	@Override
	public void rightClick() {
		// TODO Auto-generated method stub

	}
	
	//获取UUID
	public static String getUUID() {
		String uuidStr = UUID.randomUUID().toString();
		uuidStr = uuidStr.substring(0, 8) + uuidStr.substring(9, 13)
				+ uuidStr.substring(14, 18) + uuidStr.substring(19, 23)
				+ uuidStr.substring(24);
		return uuidStr;
	}
	
	//按返回键触发的事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(AddPatientActivity.this,
					MainActivity.class);
			intent.putExtra("Did", Did);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
