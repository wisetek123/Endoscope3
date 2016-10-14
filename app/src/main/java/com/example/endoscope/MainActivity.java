package com.example.endoscope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endoscope.adapter.PatientAdapter;
import com.example.endoscope.bean.PatientBean;
import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;


//主界面
public class MainActivity extends Activity implements OnClickListener,
		topbarClickListener, OnItemLongClickListener, OnItemClickListener {

	private Topbar mTopbar;

	private Button search;
	private TextView Pno;
	private TextView sign;
	private Button delete;
	private ListView mListView;
	private RelativeLayout layout;
	
	private LinkedList<PatientBean> mListItems;
	// private List<PatientBean> mDatas;
	private PatientAdapter mAdapter;
	private PatientBean bean;

	private MyDatabaseHelper dbHelper;

	private String Did;
	private String PNo;
	private String no;
	private String pno;

	private long exitTime = 0;
	private Boolean isMulSelect=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		search = (Button) findViewById(R.id.search);
		sign = (TextView) findViewById(R.id.patientSign);
		delete=(Button)findViewById(R.id.patient_delete);
		layout = (RelativeLayout) findViewById(R.id.patient_rel);
		mListView = (ListView) findViewById(R.id.patient_listview);

		//获取上个页面传递的值
		Intent intent = getIntent();
		Did = intent.getStringExtra("Did");

		dbHelper = new MyDatabaseHelper(MainActivity.this,
				"PatientManagement.db", null, 1);

		mListItems = new LinkedList<PatientBean>();
		// mDatas = new ArrayList<PatientBean>();

		//读取病人信息
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor pd_cursor = db.query("PD", null, "Did = ?", new String[] { ""
				+ Did }, null, null, null);
		if (pd_cursor.moveToFirst()) {
			sign.setVisibility(View.GONE);
			do {
				PNo = pd_cursor.getString(pd_cursor
						.getColumnIndex("Pno"));
				int no = pd_cursor.getInt(pd_cursor.getColumnIndex("No"));

				Cursor cursor = db.query("Patient", null, "Pno = ?",
						new String[] { "" + PNo }, null, null, null);
				cursor.moveToFirst();
				String name = cursor.getString(cursor.getColumnIndex("Pname"));
				String sex = cursor.getString(cursor.getColumnIndex("Psex"));
				String age = cursor.getString(cursor.getColumnIndex("Page"));
				String tel = cursor.getString(cursor.getColumnIndex("Ptel"));
				String adr = cursor.getString(cursor.getColumnIndex("Padr"));
				
				String IDnum = cursor
						.getString(cursor.getColumnIndex("PIDnum"));
				bean = new PatientBean("" + no, name, sex,  age, tel, adr,
						IDnum);
				// mDatas.add(bean);
				mListItems.addFirst(bean);
				cursor.close();
			} while (pd_cursor.moveToNext());
		}
		pd_cursor.close();

		mAdapter = new PatientAdapter(this, mListItems);
		// mAdapter = new PatientAdapter(this, mDatas);

		mListView.setAdapter(mAdapter);

		// 设置Topbar中的文字信息
		mTopbar.setToptitle(getString(R.string.patientInfo));

		// 设置Topbar中按钮的图片
		mTopbar.setLeftBT(R.drawable.out);
		mTopbar.setRightBT(R.drawable.more);

		// 显示Topbar中的按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(true);

		mTopbar.setOnTopbarClickListener(this);
		search.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		delete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		//点击搜索按钮触发的功能
		case R.id.search:
			Intent query_it = new Intent(MainActivity.this,
					QueryPatientActivity.class);
			query_it.putExtra("Did", Did);
			startActivity(query_it);
			finish();
			break;

		//点击删除按钮触发的事件
		case R.id.patient_delete:
			
			//删除病人信息
			isMulSelect=false;
			List<String> select = new ArrayList<String>();
			layout.setVisibility(View.INVISIBLE);
			select=mAdapter.getlist();
			for (int i = 0; i < select.size(); i++) {
				String no =select.get(i).toString() ;
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				String condition =  "Did = ? and  No = ?";
				String[] selectionArgs = {Did,no};
				Cursor cursor = db.query("PD", null, condition,
								selectionArgs, null, null, null);
				cursor.moveToFirst();
				pno =cursor.getString(cursor.getColumnIndex("Pno"));
				cursor.close();
			
				String[] args = { pno };
				db.delete("Patient", "Pno = ?", args);
				db.delete("PD", "Pno = ?", args);
				db.delete("Diagnosis", "Pno = ?", args);
			}
			mListItems = new LinkedList<PatientBean>();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor pd_cursor = db.query("PD", null, "Did = ?", new String[] { ""
					+ Did }, null, null, null);
			if (pd_cursor.moveToFirst()) {
				sign.setVisibility(View.GONE);
				do {
					PNo = pd_cursor.getString(pd_cursor
							.getColumnIndex("Pno"));
					int no = pd_cursor.getInt(pd_cursor.getColumnIndex("No"));
					Cursor cursor = db.query("Patient", null, "Pno = ?",
							new String[] { "" + PNo }, null, null, null);
					cursor.moveToFirst();
					String name = cursor.getString(cursor.getColumnIndex("Pname"));
					String sex = cursor.getString(cursor.getColumnIndex("Psex"));
					String age = cursor.getString(cursor.getColumnIndex("Page"));
					String tel = cursor.getString(cursor.getColumnIndex("Ptel"));
					String adr = cursor.getString(cursor.getColumnIndex("Padr"));
					
					String IDnum = cursor
							.getString(cursor.getColumnIndex("PIDnum"));
					bean = new PatientBean("" + no, name, sex, age, tel, adr,
							IDnum);
					// mDatas.add(bean);
					mListItems.addFirst(bean);
					cursor.close();
				} while (pd_cursor.moveToNext());
			}
			pd_cursor.close();
			mAdapter = new PatientAdapter(this, mListItems);
			mListView.setAdapter(mAdapter);
			break;
		}
		
	}

	//点击左边按钮触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		//弹出提示框
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle(getString(R.string.prompt))
				.setMessage(getString(R.string.isQuit))
				.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Intent out_it = new Intent(MainActivity.this,
										LoginActivity.class);
								startActivity(out_it);
								MainActivity.this.finish();
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
	}

	//点击右边按钮触发的事件
	@Override
	public void rightClick() {
		// TODO Auto-generated method stub
		Intent add_it = new Intent(MainActivity.this, AddPatientActivity.class);
		add_it.putExtra("Did", Did);
		startActivity(add_it);
		finish();
	}

	
	//长按病人列表时触发的事件
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
			int arg2, long arg3) {
		isMulSelect=true;
		layout.setVisibility(View.VISIBLE);
		mAdapter = new PatientAdapter(this, mListItems);
		mAdapter.setMulSelect(isMulSelect);
		mListView.setAdapter(mAdapter);
		// TODO Auto-generated method stub
		return true;
	}

	//点击病人列表触发的事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Pno = (TextView) arg1.findViewById(R.id.no);
		no = Pno.getText().toString();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String condition =  "Did = ? and  No = ?";
		String[] selectionArgs = {Did,no};
		Cursor cursor = db.query("PD", null, condition,
						selectionArgs, null, null, null);
		cursor.moveToFirst();
		pno =cursor.getString(cursor.getColumnIndex("Pno"));
		cursor.close();
		
		
		Intent diagnosis_it = new Intent(MainActivity.this,
				PatientActivity.class);
		diagnosis_it.putExtra("Pno", pno);
		diagnosis_it.putExtra("Did",Did);
		Log.e("11",Did);
		startActivity(diagnosis_it);
		finish();
		// Toast.makeText(MainActivity.this, Pno.getText().toString(),
		// Toast.LENGTH_SHORT).show();
	}

	/*
	 * 点击返回键触发的事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(isMulSelect)
		{
			isMulSelect=false;
			mAdapter=new PatientAdapter(this, mListItems);
			mAdapter.clearlist();
			mAdapter.setMulSelect(isMulSelect);
			mListView.setAdapter(mAdapter);
			layout.setVisibility(View.INVISIBLE);
			return true;
		}
		else
		{
			
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
		}
		return super.onKeyDown(keyCode, event);
	}

}
