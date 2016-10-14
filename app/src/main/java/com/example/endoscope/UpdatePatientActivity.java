package com.example.endoscope;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.DialerKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;

//修改病人信息功能
public class UpdatePatientActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener, topbarClickListener {

	private MyDatabaseHelper dbHelper;

	private Topbar mTopbar;

	private EditText patient_nameET;
	private RadioGroup sexRG;
	private RadioButton male;
	private RadioButton female;
	private EditText ageET;
	private EditText telET;
	private EditText adrET;
	private EditText IDnumET;
	private Button updateBT;
	

	private String patientNo;
	private String patientName;
	private String patientSex = "";
	private String patientAge;
	private String patientTel;
	private String patientAdr;
	private String patientIDnum;
	private String did;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_updatepatient);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		patient_nameET = (EditText) findViewById(R.id.new_patient_nameET);
		sexRG = (RadioGroup) findViewById(R.id.new_sexRG);
		male = (RadioButton) findViewById(R.id.new_male);
		female = (RadioButton) findViewById(R.id.new_female);
		ageET = (EditText) findViewById(R.id.new_ageET);
		ageET.setKeyListener(DialerKeyListener.getInstance());
		telET = (EditText) findViewById(R.id.new_telET);
		telET.setKeyListener(DialerKeyListener.getInstance());
		adrET = (EditText) findViewById(R.id.new_adrET);
		IDnumET = (EditText) findViewById(R.id.new_IDnumET);
		
		updateBT = (Button) findViewById(R.id.updatePatientBT);

		Intent update_it = getIntent();
		patientNo = update_it.getStringExtra("Pno");
		did = update_it.getStringExtra("Did");

		dbHelper = new MyDatabaseHelper(UpdatePatientActivity.this,
				"PatientManagement.db", null, 1);

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor patient_cursor = db.query("Patient", null, "Pno = ?",
				new String[] { patientNo }, null, null, null);
		patient_cursor.moveToFirst();
		patientName = patient_cursor.getString(patient_cursor
				.getColumnIndex("Pname"));
		patient_nameET.setText(patientName);
		patientSex = patient_cursor.getString(patient_cursor
				.getColumnIndex("Psex"));
		String sex = "男";
		if (sex.equals(patientSex)) {
			male.setChecked(true);
		} else {
			female.setChecked(true);
		}
		patientAge = patient_cursor.getString(patient_cursor
				.getColumnIndex("Page"));
		ageET.setText("" + patientAge);
		patientTel = patient_cursor.getString(patient_cursor
				.getColumnIndex("Ptel"));
		telET.setText(patientTel);
		patientAdr = patient_cursor.getString(patient_cursor
				.getColumnIndex("Padr"));
		adrET.setText(patientAdr);
		patientIDnum = patient_cursor.getString(patient_cursor
				.getColumnIndex("PIDnum"));
		IDnumET.setText(patientIDnum);
		patient_cursor.close();

		// 设置Topbar中的文字信息
		mTopbar.setToptitle(getString(R.string.changePatientInfo));

		// 设置Topbar中左边按钮的图片
		mTopbar.setLeftBT(R.drawable.back);

		// 显示左边按钮，隐藏右边按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(false);

		mTopbar.setOnTopbarClickListener(this);
		updateBT.setOnClickListener(this);
		sexRG.setOnCheckedChangeListener(this);
	}

	
	//点击更新按钮时触发的事件
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!patient_nameET.getText().toString().equals("")) {
			patientName = patient_nameET.getText().toString();
		}
		if (!ageET.getText().toString().equals("")) {
			patientAge = ageET.getText().toString();
		}
		if (!telET.getText().toString().equals("")) {
			patientTel = telET.getText().toString();
		}
		if (!adrET.getText().toString().equals("")) {
			patientAdr = adrET.getText().toString();
		}
		if (!IDnumET.getText().toString().equals("")) {
			patientIDnum = IDnumET.getText().toString();
		}

		dbHelper = new MyDatabaseHelper(UpdatePatientActivity.this,
				"PatientManagement.db", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("Pname", patientName);
		values.put("Psex", patientSex);
		values.put("Page", patientAge);
		values.put("Ptel", patientTel);
		values.put("Padr", patientAdr);
		values.put("PIDnum", patientIDnum);
		db.update("Patient", values, "Pno = ?", new String[] { patientNo });
		values.clear();

		Intent intent = new Intent(UpdatePatientActivity.this,
				PatientActivity.class);
		intent.putExtra("Pno", patientNo);
		intent.putExtra("Did",did);
		startActivity(intent);
		finish();

	}

	//性别按钮
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.new_male:
			patientSex = "男";
			break;

		case R.id.new_female:
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
		Intent intent = new Intent(UpdatePatientActivity.this,
				PatientActivity.class);
		intent.putExtra("Pno", patientNo);
		intent.putExtra("Did",did);
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
			Intent intent = new Intent(UpdatePatientActivity.this,
					PatientActivity.class);
			intent.putExtra("Pno", patientNo);
			intent.putExtra("Did",did);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
