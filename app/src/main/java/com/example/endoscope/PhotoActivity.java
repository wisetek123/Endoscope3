package com.example.endoscope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.endoscope.adapter.PageAdapter;
import com.example.endoscope.bean.BrowseListBean;
import com.example.logic.Img;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.Gallery;
import android.widget.ImageView;

//浏览图片
public class PhotoActivity extends Activity {

	private ImageView photoImg;
	private Gallery gallery;
	private String folderPath;
	private String photo_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo);

		init();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		
		gallery = (Gallery) findViewById(R.id.gallery1);
		List<Img> list=new ArrayList<Img>();
		Img img;
		
		//获取上个页面传递的信息
		Intent intent = getIntent();
		folderPath = intent.getStringExtra("folderPath");
		photo_name = intent.getStringExtra("photo_name");

		File file = new File(folderPath);
		String[] allFileNames = file.list();
		for (int i = allFileNames.length - 1; i >= 0; i--) {
			if (Utility.isImage(allFileNames[i])) {
				// imageFilePaths.add(folderPath + allFileNames[i]);
				String imagePath = folderPath + "/" + allFileNames[i];
				img=new Img();
				img.setDes(allFileNames[i]);
				img.setImgurl(imagePath);
				list.add(img);
			}
		}
		PageAdapter pageAdapter=new PageAdapter(PhotoActivity.this, list);
		gallery.setAdapter(pageAdapter);
	}

}
