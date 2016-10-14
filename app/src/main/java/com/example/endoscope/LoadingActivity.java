package com.example.endoscope;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


//加载页面显示功能
public class LoadingActivity extends Activity {

	//lzh//private final int SKIP_DELAY_TIME = 3000;
	private final int SKIP_DELAY_TIME = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// 获得当前窗体对象
		Window window = LoadingActivity.this.getWindow();
		// 设置当前窗体为全屏显示
		window.setFlags(flag, flag);
		setContentView(R.layout.activity_loading);

		Timer time = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				startActivity(new Intent(LoadingActivity.this,
						LoginActivity.class));

				finish();
			}
		};
		time.schedule(task, SKIP_DELAY_TIME);
	}
}
