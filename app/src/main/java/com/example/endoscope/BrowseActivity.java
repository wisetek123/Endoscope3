package com.example.endoscope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.endoscope.adapter.BrowseAdapter;
import com.example.endoscope.bean.BrowseBean;
import com.example.endoscope.bean.ConstantBean;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;


//图片视频目录
public class BrowseActivity extends Activity implements topbarClickListener,
		OnItemClickListener {

	private Topbar mTopbar;

	private ListView mListView;
	private List<BrowseBean> mDatas;
	private BrowseAdapter mAdapter;
	private BrowseBean bean;

	private int photo_num = 0;
	private int video_num = 0;

	// private String pname;
	// private String pno;
	private String imagePath;
	private String videoPath;
	
	private String fresh=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_browse);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		mTopbar = (Topbar) findViewById(R.id.id_topbar);

		mListView = (ListView) findViewById(R.id.browse_list);

		
		//接受图片和视频的路径信息
		Intent diagnosis_it = getIntent();
		imagePath = diagnosis_it.getStringExtra("imagePath");
		videoPath = diagnosis_it.getStringExtra("videoPath");
		
		
		//判断是否获取访问存储空间的权限
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// imagePath = ConstantBean.imagePath + File.separator + pno;
			// videoPath = ConstantBean.videoPath + File.separator + pno;
		} else {
			Toast.makeText(this, getString(R.string.noReadPermession), Toast.LENGTH_LONG).show();
			finish();
		}

		//获取路径内图片和视频的数目
		getImageFiles(imagePath);
		getVideoFiles(videoPath);

		mDatas = new ArrayList<BrowseBean>();

		bean = new BrowseBean(getString(R.string.photo), "(" + photo_num + ")");
		mDatas.add(bean);
		bean = new BrowseBean(getString(R.string.video), "(" + video_num + ")");
		mDatas.add(bean);

		mAdapter = new BrowseAdapter(this, mDatas);

		mListView.setAdapter(mAdapter);

		// 设置Topbar中间的文字
		mTopbar.setToptitle(getString(R.string.browse));

		// 设置左边按钮的图片
		mTopbar.setLeftBT(R.drawable.back);

		//显示左按钮，隐藏右边的按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(false);

		mTopbar.setOnTopbarClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	
	//获取文件夹中图片个数
	private void getImageFiles(String path) {
		// TODO Auto-generated method stub
		File file = new File(path);
		File[] files = file.listFiles();
		for (int j = 0; j < files.length; j++) {
			String name = files[j].getName();
			if (files[j].isDirectory()) {
				String dirPath = files[j].toString().toLowerCase();
				System.out.println(dirPath);
				getImageFiles(dirPath + "/");
			} else if (files[j].isFile() & name.endsWith(".JPEG")) {
				System.out.println("FileName===" + files[j].getName());
				photo_num++;
			}
		}
	}

	
	//获取文件夹中视频的个数
	private void getVideoFiles(String path) {
		// TODO Auto-generated method stub
		File file = new File(path);
		File[] files = file.listFiles();
		for (int j = 0; j < files.length; j++) {
			String name = files[j].getName();
			if (files[j].isDirectory()) {
				String dirPath = files[j].toString().toLowerCase();
				System.out.println(dirPath);
				getImageFiles(dirPath + "/");
			} else if (files[j].isFile() & name.endsWith(".mp4")) {
				System.out.println("FileName===" + files[j].getName());
				video_num++;
			}
		}
	}

	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		BrowseActivity.this.finish();
	}

	@Override
	public void rightClick() {
		// TODO Auto-generated method stub
	}

	//点击图片或视频项目时，跳转页面，显示文件夹中内容
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg2) {
		case 0:
			Intent photo_it = new Intent(BrowseActivity.this,
					PhotoWallActivity.class);
			photo_it.putExtra("folderPath", imagePath);
			//photo_it.putExtra("videoPath", videoPath);
			startActivity(photo_it);
			finish();
			break;

		case 1:
			Intent video_it = new Intent(BrowseActivity.this,
					VideoWallActivity.class);
			video_it.putExtra("folderPath", videoPath);
			startActivity(video_it);
			finish();
			break;

		default:
			break;
		}

	}

}
