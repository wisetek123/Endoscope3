package com.example.endoscope;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.endoscope.adapter.BrowseListAdapter;
import com.example.endoscope.bean.BrowseListBean;
import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;


//以缩略图显示视频信息
public class VideoWallActivity extends Activity implements topbarClickListener,
		OnItemClickListener, OnItemLongClickListener, OnClickListener {

	private Topbar mTopbar;

	private TextView photo_name;
	private Button  delete;
	private RelativeLayout layout;
	// private GridView mPhotoWall;
	private ListView mListView;
	private LinkedList<BrowseListBean> mListItems;
	// private List<PatientBean> mDatas;
	private BrowseListAdapter mAdapter;
	private BrowseListBean bean;

	private String folderPath;
	private String imagePath;
	private Boolean isMulSelect = false;
	
	private MyDatabaseHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_videowall);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);
		mListView = (ListView) findViewById(R.id.video_list);
		layout = (RelativeLayout) findViewById(R.id.video_rel);
	
		delete = (Button) findViewById(R.id.video_delete);

		// 接受上个页面传递的信息
		Intent intent = getIntent();
		folderPath = intent.getStringExtra("folderPath");
		imagePath = folderPath.replace("/Video", "/Image");
	
		//缩略图显示
		mListItems = new LinkedList<BrowseListBean>();
		File folder = new File(folderPath);
		String[] allFileNames = folder.list();
		for (int i = allFileNames.length - 1; i >= 0; i--) {
			if (Utility.isVideo(allFileNames[i])) {
				// imageFilePaths.add(folderPath + allFileNames[i]);
				if (allFileNames[i].indexOf(".mp4") != -1) {
					String VideoPath = folderPath + "/" + allFileNames[i];
					Log.e("VideoPath", VideoPath);
					bean = new BrowseListBean(allFileNames[i], VideoPath);
					mListItems.add(bean);
				}
			}
		}
		mAdapter = new BrowseListAdapter(this, mListItems);
		mListView.setAdapter(mAdapter);

		// 设置Topbar中的文字信息
		mTopbar.setToptitle(getString(R.string.video));

		// 设置Topbar中的按钮图片
		mTopbar.setLeftBT(R.drawable.back);
		mTopbar.setRightBT(R.drawable.ok);

		// 显示左边按钮，隐藏右边按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(false);

		mTopbar.setOnTopbarClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		
		delete.setOnClickListener(this);
	}



	//点击左边按钮时触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		Intent it = new Intent(VideoWallActivity.this, BrowseActivity.class);
		it.putExtra("imagePath", imagePath);
		it.putExtra("videoPath", folderPath);
		startActivity(it);
		
		VideoWallActivity.this.finish();

	}

	//点击视频信息时触发的事件
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		photo_name = (TextView) arg1.findViewById(R.id.photo_name);
		Intent it = new Intent(VideoWallActivity.this, VideoActivity.class);
		it.putExtra("folderPath", folderPath);
		it.putExtra("photo_name", photo_name.getText().toString());
		startActivity(it);

	}

	//长按视频信息时触发的事件
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
			int arg2, long arg3) {
		// TODO Auto-generated method stub
		isMulSelect = true;
		layout.setVisibility(View.VISIBLE);
		mAdapter = new BrowseListAdapter(this, mListItems);
		mAdapter.setMulSelect(isMulSelect);
		mListView.setAdapter(mAdapter);
		mTopbar.setRightButtonIsVisiable(true);

		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		//点击删除按钮触发的事件
		case R.id.video_delete:
			isMulSelect = false;
			List<String> select = new ArrayList<String>();
			select = mAdapter.getlist();
			for (int i = 0; i < select.size(); i++) {
				String name = select.get(i);
				File videofile = new File(folderPath + "/" + name);
				if (videofile.exists()) {
					videofile.delete();
				}
				name=name.replace(".mp4", ".JPEG");
				File pictfile = new File(folderPath + "/" + name);
				if (pictfile.exists()) {
					pictfile.delete();
				}
				name=name.replace(".JPEG", ".txt");
				File textFile=new File(folderPath + "/" + name);
				if (textFile.exists()) {
					textFile.delete();
				}
			}
			mListItems = new LinkedList<BrowseListBean>();
			File folder = new File(folderPath);
			String[] allFileNames = folder.list();
			for (int i = allFileNames.length - 1; i >= 0; i--) {
				if (Utility.isVideo(allFileNames[i])) {
					// imageFilePaths.add(folderPath + allFileNames[i]);
					if (allFileNames[i].indexOf(".mp4") != -1) {
						String VideoPath = folderPath + "/" + allFileNames[i];
						Log.e("VideoPath", VideoPath);
						bean = new BrowseListBean(allFileNames[i], VideoPath);
						mListItems.add(bean);
					}
				}
			}
			mAdapter = new BrowseListAdapter(this, mListItems);
			mAdapter.setMulSelect(isMulSelect);
			mAdapter.clearlist();
			mListView.setAdapter(mAdapter);
			layout.setVisibility(View.INVISIBLE);
			mTopbar.setRightButtonIsVisiable(false);
			break;
		}
	}
	
		//点击右边按钮触发的事件
		public void rightClick() {
			// TODO Auto-generated method stub
			isMulSelect = false;
			List<String> select = new ArrayList<String>();
			select = mAdapter.getlist();
			if(select.size()>0)
			{
			dbHelper = new MyDatabaseHelper(VideoWallActivity.this,
					"PatientManagement.db", null, 1);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			for (int i = 0; i < select.size(); i++) {
				values.put("uploadfilepath",folderPath + "/" +select.get(i).toString() );
				db.insert("uploadlog", null, values);
				values.clear();
			}
			mAdapter = new BrowseListAdapter(this, mListItems);
			mAdapter.clearlist();
			mAdapter.setMulSelect(isMulSelect);
			mListView.setAdapter(mAdapter);
			layout.setVisibility(View.INVISIBLE);
			mTopbar.setRightButtonIsVisiable(false);
			Intent intent=new Intent(VideoWallActivity.this, Upload.class);
			startActivity(intent);
			}
			else
			{
				mAdapter = new BrowseListAdapter(this, mListItems);
				mAdapter.clearlist();
				mAdapter.setMulSelect(isMulSelect);
				mListView.setAdapter(mAdapter);
				layout.setVisibility(View.INVISIBLE);
				mTopbar.setRightButtonIsVisiable(false);
			}

	}
		
		//点击返回按钮触发的事件
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			
			//判断是否处于长按状态
			if(isMulSelect)
			{
				isMulSelect = false;
				mAdapter = new BrowseListAdapter(this, mListItems);
				mAdapter.clearlist();
				mAdapter.setMulSelect(isMulSelect);
				mListView.setAdapter(mAdapter);
				layout.setVisibility(View.INVISIBLE);
				mTopbar.setRightButtonIsVisiable(false);
			}
			else
			{
				Intent it = new Intent(VideoWallActivity.this, BrowseActivity.class);
				it.putExtra("imagePath", imagePath);
				it.putExtra("videoPath", folderPath);
				startActivity(it);
				VideoWallActivity.this.finish();
				
			}
			return true;
			
		}

}
