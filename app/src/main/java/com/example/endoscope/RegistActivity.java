package com.example.endoscope;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import json.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;


//注册功能
public class RegistActivity extends Activity implements OnClickListener,
		topbarClickListener {

	private MyDatabaseHelper dbHelper;

	private Topbar mTopbar;

	private EditText userET;
	private EditText pwdET;
	private EditText confirmPwdET;
	private Button registBT;
	private String user;
	private String pwd;
	private String cfmPwd;

	
	private String did;
	private static String url = "http://192.168.1.14:8088/android/json/" + "register_handle.php";
	//private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		userET = (EditText) findViewById(R.id.new_userET);
		pwdET = (EditText) findViewById(R.id.new_pwdET);
		confirmPwdET = (EditText) findViewById(R.id.confirm_pwdET);
		registBT = (Button) findViewById(R.id.registBT);

		// 设置Topbar中的文字信息
		mTopbar.setToptitle(getString(R.string.regist));

		// 设置左边按钮的图片
		mTopbar.setLeftBT(R.drawable.back);

		// 显示左边按钮，隐藏右边按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(false);

		mTopbar.setOnTopbarClickListener(this);
		registBT.setOnClickListener(this);
	}

	
	//点击注册按钮时触发的信息
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//获取编辑框中信息
		user = userET.getText().toString();
		pwd = pwdET.getText().toString();
		cfmPwd = confirmPwdET.getText().toString();

		if (!user.equals("")) {
			if (!pwd.equals("")) {
				if (!cfmPwd.equals("") && cfmPwd.equals(pwd)) {
					dbHelper = new MyDatabaseHelper(RegistActivity.this,
							"PatientManagement.db", null, 1);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = db.query("Doctor", null, "Daccount = ?",
							new String[] { user }, null, null, null);
					if (cursor.moveToFirst()) {
						Toast.makeText(this, getString(R.string.usernameIsTeken),
								Toast.LENGTH_SHORT).show();
					} else {
						ContentValues values = new ContentValues();
						did = getUUID();
						values.put("Did",did);
						values.put("Daccount", user);
						values.put("Dpassword", pwd);
						db.insert("Doctor", null, values);// ��������
						values.clear();
						
					//	new Regist().execute();
						finish();

						// Intent intent = new Intent(RegistActivity.this,
						// LoginActivity.class);
						// startActivity(intent);
						//finish();
					}
				} else {
					Toast.makeText(this, getString(R.string.passwordNotSame),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, getString(R.string.inputPassword), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, getString(R.string.inputUsername), Toast.LENGTH_SHORT).show();
		}
	}
	
	//上传到服务器
	class Regist extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(RegistActivity.this);
			pDialog.setMessage(getString(R.string.registing)+"...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Did", did));
			params.add(new BasicNameValuePair("Username", user));


			try {
				JSONObject json = jsonParser.makeHttpRequset(url, "POST",
						params);
				/*int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// Intent intent = new Intent(RegistActivity.this,
					// LoginActivity.class);
					// startActivity(intent);
					finish();
				}*/
				String message = json.getString(TAG_MESSAGE);
				return message;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	//获取UUID
	public static String getUUID() {
		String uuidStr = UUID.randomUUID().toString();
		uuidStr = uuidStr.substring(0, 8) + uuidStr.substring(9, 13)
				+ uuidStr.substring(14, 18) + uuidStr.substring(19, 23)
				+ uuidStr.substring(24);
		return uuidStr;
	}
	
	//点击左边按钮触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void rightClick() {
		// TODO Auto-generated method stub

	}
}
