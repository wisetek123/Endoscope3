package com.example.endoscope;

import java.util.LinkedList;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endoscope.adapter.PatientAdapter;
import com.example.endoscope.bean.PatientBean;
import com.example.endoscope.db.MyDatabaseHelper;


//查询功能
public class QueryPatientActivity extends Activity implements OnClickListener,
		OnItemLongClickListener, OnItemClickListener, OnItemSelectedListener {

	private EditText search;
	private Spinner searchBy;
	private Button searchBT;
	private TextView sign;
	private TextView Pno;

	private ListView mListView;
	private LinkedList<PatientBean> mListItems;
	// private List<PatientBean> mDatas;
	private PatientAdapter mAdapter;
	private PatientBean bean;

	private MyDatabaseHelper dbHelper;

	private String Did;
	private int PNo;
	private int kind = 0;
	private int No;
	private String Pno1;
	private String pno;
	private String no;

	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_querypatient);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		search = (EditText) findViewById(R.id.search);
		searchBy = (Spinner) findViewById(R.id.searchBy);
		searchBT = (Button) findViewById(R.id.searchBT);
		sign = (TextView) findViewById(R.id.patientSign);
		mListView = (ListView) findViewById(R.id.patient_listview);

		
		//获取上个页面传递的信息
		Intent intent = getIntent();
		Did = intent.getStringExtra("Did");

		searchBy.setOnItemSelectedListener(this);
		searchBT.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	//点击查询按钮
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mListItems = new LinkedList<PatientBean>();
		mListItems.clear();
		mAdapter = new PatientAdapter(this, mListItems);
		mListView.setAdapter(mAdapter);
		dbHelper = new MyDatabaseHelper(QueryPatientActivity.this,
				"PatientManagement.db", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = null;
		Cursor pd_cursor = null;
		if (search.getText().toString().equals("")) {
			Toast.makeText(this, getString(R.string.noMoreInfo), Toast.LENGTH_SHORT).show();
		} else {
			String[] condition = { "%" + search.getText().toString() + "%" };
			switch (kind) {
			
			//根据病人编号查询
			case 0:
				
				String[] selectArgs = {Did, search.getText().toString()};
				pd_cursor = db.query("PD", null, "Did = ? and No = ?", selectArgs,
						null, null, null);
				Log.e("ww", pd_cursor.getCount()+"");
				if(pd_cursor.moveToFirst())
				{
					sign.setVisibility(View.GONE);
						Pno1=pd_cursor.getString(pd_cursor.getColumnIndex("Pno"));
						No = pd_cursor.getInt(pd_cursor.getColumnIndex("No"));
						
						cursor = db.query("Patient",null,"Pno = ?",new String[] {Pno1},null,null,null);
						if(cursor.moveToFirst())
						{
						String name = cursor.getString(cursor
								.getColumnIndex("Pname"));
						String sex = cursor.getString(cursor
								.getColumnIndex("Psex"));
						String age = cursor.getString(cursor.getColumnIndex("Page"));
						String tel = cursor.getString(cursor
								.getColumnIndex("Ptel"));
						String adr = cursor.getString(cursor
								.getColumnIndex("Padr"));
						String IDnum = cursor.getString(cursor
								.getColumnIndex("PIDnum"));
						bean = new PatientBean("" + No, name, sex,  age,
								tel, adr, IDnum);
						mListItems.addFirst(bean);
						}
						cursor.close();
						
						pd_cursor.close();
					}else
					{
						sign.setVisibility(View.VISIBLE);
					}
				mAdapter = new PatientAdapter(this, mListItems);
				mListView.setAdapter(mAdapter);
				
				break;
			//根据病人姓名查询
			case 1:
				cursor = db.query("Patient", null, "Pname like ?", condition,
						null, null, null);
				if (cursor.moveToFirst()) {
					sign.setVisibility(View.GONE);
					do {
						PNo = cursor.getInt(cursor.getColumnIndex("Pno"));
						pd_cursor = db.query("PD", null, "Pno = ?",
								new String[] { "" + PNo }, null, null, null);
						pd_cursor.moveToFirst();
						Log.e("msg", pd_cursor.getColumnIndex("Did")+"");
						Log.e("msg", pd_cursor
								.getString(pd_cursor.getColumnIndex("Did")));
						if (Did.equals(pd_cursor
								.getString(pd_cursor.getColumnIndex("Did"))) ) {
							sign.setVisibility(View.GONE);
							No = pd_cursor.getInt(pd_cursor.getColumnIndex("No"));
							String name = cursor.getString(cursor
									.getColumnIndex("Pname"));
							String sex = cursor.getString(cursor
									.getColumnIndex("Psex"));
							String age = cursor.getString(cursor.getColumnIndex("Page"));
							String tel = cursor.getString(cursor
									.getColumnIndex("Ptel"));
							String adr = cursor.getString(cursor
									.getColumnIndex("Padr"));
							String IDnum = cursor.getString(cursor
									.getColumnIndex("PIDnum"));
							bean = new PatientBean("" + No, name, sex,  age,
									tel, adr, IDnum);
							mListItems.addFirst(bean);
						} else {
							sign.setVisibility(View.VISIBLE);
						}
						pd_cursor.close();
					} while (cursor.moveToNext());
				} else {
					sign.setVisibility(View.VISIBLE);
				}
				cursor.close();
				mAdapter = new PatientAdapter(this, mListItems);
				mListView.setAdapter(mAdapter);
			

				break;
			//根据地址查询
			case 2:
				cursor = db.query("Patient", null, "Padr like ?", condition,
						null, null, null);
				if (cursor.moveToFirst()) {
					sign.setVisibility(View.GONE);
					do {
						PNo = cursor.getInt(cursor.getColumnIndex("Pno"));
						pd_cursor = db.query("PD", null, "Pno = ?",
								new String[] { "" + PNo }, null, null, null);
						pd_cursor.moveToFirst();
						Log.e("msg", pd_cursor.getColumnIndex("Did")+"");
						Log.e("msg", pd_cursor
								.getString(pd_cursor.getColumnIndex("Did")));
						if (Did.equals(pd_cursor
								.getString(pd_cursor.getColumnIndex("Did"))) ) {
							sign.setVisibility(View.GONE);
							No = pd_cursor.getInt(pd_cursor.getColumnIndex("No"));
							String name = cursor.getString(cursor
									.getColumnIndex("Pname"));
							String sex = cursor.getString(cursor
									.getColumnIndex("Psex"));
							String age = cursor.getString(cursor.getColumnIndex("Page"));
							String tel = cursor.getString(cursor
									.getColumnIndex("Ptel"));
							String adr = cursor.getString(cursor
									.getColumnIndex("Padr"));
							String IDnum = cursor.getString(cursor
									.getColumnIndex("PIDnum"));
							bean = new PatientBean("" + No, name, sex,  age,
									tel, adr, IDnum);
							mListItems.addFirst(bean);
						} else {
							sign.setVisibility(View.VISIBLE);
						}
						pd_cursor.close();
					} while (cursor.moveToNext());
				} else {
					sign.setVisibility(View.VISIBLE);
				}
				cursor.close();
				mAdapter = new PatientAdapter(this, mListItems);
				mListView.setAdapter(mAdapter);
			

				break;

			default:
				break;
			}

		}

	}

	//长按病人信息触发的事件
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
			int arg2, long arg3) {
		// TODO Auto-generated method stub
		//弹出提示框
		Dialog dialog = new AlertDialog.Builder(QueryPatientActivity.this)
				.setTitle(getString(R.string.prompt))
				.setMessage(getString(R.string.isDel))
				.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Pno = (TextView) arg1.findViewById(R.id.no);
								SQLiteDatabase db = dbHelper
										.getWritableDatabase();
								String[] args = { Pno.getText().toString() };
								db.delete("Patient", "Pno = ?", args);
								db.delete("PD", "Pno = ?", args);
								db.delete("Diagnosis", "Pno = ?", args);
								Intent refresh_it = new Intent(
										QueryPatientActivity.this,
										MainActivity.class);
								refresh_it.putExtra("Did", Did);
								startActivity(refresh_it);
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
		return true;
	}

	
	//点击病人信息时触发的事件
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
		Intent diagnosis_it = new Intent(QueryPatientActivity.this,
				PatientActivity.class);

		diagnosis_it.putExtra("Pno", pno);
		diagnosis_it.putExtra("Did", Did);

		startActivity(diagnosis_it);
		finish();
		
		// Toast.makeText(MainActivity.this, Pno.getText().toString(),
		// Toast.LENGTH_SHORT).show();
	}

	//按返回键时触发的事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent query_it = new Intent(QueryPatientActivity.this,
					MainActivity.class);
			query_it.putExtra("Did", Did);
			startActivity(query_it);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		kind = arg2;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
