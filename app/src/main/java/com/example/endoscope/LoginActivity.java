package com.example.endoscope;

import android.app.Activity;
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
import android.widget.Toast;

import com.example.endoscope.db.MyDatabaseHelper;


//登录功能
public class LoginActivity extends Activity implements OnClickListener {

	private MyDatabaseHelper dbHelper;

	private EditText userET;
	private EditText pwdET;
	private Button registBT;
	private Button loginBT;

	private String Did;
	private String user;
	private String pwd;

	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		userET = (EditText) findViewById(R.id.userET);
		pwdET = (EditText) findViewById(R.id.pwdET);
		// pwdET.setKeyListener(DialerKeyListener.getInstance());
		registBT = (Button) findViewById(R.id.to_registBT);
		loginBT = (Button) findViewById(R.id.loginBT);

		registBT.setOnClickListener(this);
		loginBT.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		//点击注册按钮触发的事件
		case R.id.to_registBT:
			Intent regist_it = new Intent(LoginActivity.this,
					RegistActivity.class);
			startActivity(regist_it);
			break;

		//点击登录按钮触发的功能
		case R.id.loginBT:
			user = userET.getText().toString();
			pwd = pwdET.getText().toString();
			
			
			//判断帐号密码是否正确
			if (!user.equals("")) {
				if (!pwd.equals("")) {
					dbHelper = new MyDatabaseHelper(LoginActivity.this,
							"PatientManagement.db", null, 1);
					SQLiteDatabase db = dbHelper.getWritableDatabase();

					Cursor cursor = db.query("Doctor", null, "Daccount = ?",
							new String[] { user }, null, null, null);
					if (cursor.moveToFirst()) {
						Did = cursor.getString(cursor.getColumnIndex("Did"));
						String newPwd = cursor.getString(cursor
								.getColumnIndex("Dpassword"));
						if (newPwd.equals(pwd)) {
							Intent login_it = new Intent(LoginActivity.this,
									MainActivity.class);
							login_it.putExtra("Did", Did);
							startActivity(login_it);
							LoginActivity.this.finish();
						} else {
							Toast.makeText(this, getString(R.string.wrongPassword), Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(this, getString(R.string.wrongUsername), Toast.LENGTH_SHORT)
								.show();
					}
					cursor.close();
				} else {
					Toast.makeText(this, getString(R.string.inputPassword), Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(this, getString(R.string.inputUsername), Toast.LENGTH_SHORT).show();
			}

			break;

		default:
			break;
		}
	}

	/*
	 * 点击返回键是触发的事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), getString(R.string.pressAgain),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
