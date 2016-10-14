package com.example.endoscope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

//播放视频功能
public class VideoActivity extends Activity implements OnClickListener {

	private String folderPath;
	private String photo_name;
	private String filePath;
	private String recordtime;
	
	private SurfaceView player;
	private SurfaceHolder surfaceHolder;
	private RelativeLayout contrl;
	private ImageButton playbt;
	private TextView time;
	private TextView time_end;
	private SeekBar bar;
	byte[] flagHead;// jpegͷ��FFD8
	byte[] flagTail;// jpegβ��FFD9
	private int screenWidth;
	private int screenHeight;
	int video_i;
	int position;
	long Time = 0;
	private Boolean isOver = false;
	private Boolean isshowcontrl = false;
	private Boolean isStop = false;
	private Boolean mRunning = false;
	private Boolean isJump=false;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video);

		init();

	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub

		Intent intent = getIntent();
		folderPath = intent.getStringExtra("folderPath");
		Log.e("adf", folderPath);
		photo_name = intent.getStringExtra("photo_name");
		Log.e("name", photo_name);

		player = (SurfaceView) findViewById(R.id.videoPlayer);
		contrl = (RelativeLayout) findViewById(R.id.playVideo);
		playbt = (ImageButton) findViewById(R.id.controlVideo);
		time = (TextView) findViewById(R.id.Time_start);
		time_end = (TextView) findViewById(R.id.Time_end);
		bar = (SeekBar) findViewById(R.id.videobar);

		filePath = folderPath + "/" + photo_name;
		
		String recordtimePath=filePath.replace(".mp4", ".txt");
		File file = new File(recordtimePath);      
        FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			int length = 0;
			 try {
				length = fis.available();
				byte [] buffer = new byte[length];
				fis.read(buffer);
				 recordtime = EncodingUtils.getString(buffer, "UTF-8"); 
				 fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}      
			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
  
		
		surfaceHolder = player.getHolder();

		player.setOnClickListener(this);
		playbt.setOnClickListener(this);
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				double ing = 0;
				double ingg;
				ing = (double) bar.getProgress();
				ing = ing / 100;
				ingg = ing * (double) Time;
				position=(int)ingg;
				isJump=true;
			}
		});;

		//获取屏幕大小
		DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
		screenWidth = localDisplayMetrics.widthPixels;
		screenHeight = localDisplayMetrics.heightPixels;
		initData();

		HandlerThread thread = new HandlerThread("MyHandlerThread");
		thread.start();// 创建一个HandlerThread并启动它
		mHandler = new Handler(thread.getLooper());// 使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
		mHandler.post(mBackgroundRunnable);// 将线程post到Handler中
		
		
	}

	private void initData() {
		flagHead = new byte[] { (byte) 255, (byte) 216 };// jpeg 头部
		flagTail = new byte[] { (byte) 255, (byte) 217 };// jpeg 尾部

	}

	Runnable mBackgroundRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			startVideo();
			
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		mRunning = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mRunning = false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁线程
		mHandler.removeCallbacks(mBackgroundRunnable);

	}

	protected void startVideo() {
		// TODO Auto-generated method stub
		try {
			byte[] video = new byte[70000];
			isOver = false;
			double playing = 0;
			int hour, min = 0, sec = 0;
			RandomAccessFile randomFile = new RandomAccessFile(filePath, "rw");
			try {
				long lenth = randomFile.length();
				Time = randomFile.length() / 70000;
				recordtime=recordtime.replace("REC ", "");
				time_end.setText(recordtime);
				Log.e("len", lenth + "");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			FileChannel fc = randomFile.getChannel();
			// randomFile.length()/70000
			try {
				MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_WRITE,
						0, randomFile.length());
				for (video_i = 0; video_i < randomFile.length() / 70000; video_i++) {
					int k = 0;
					for (int j = video_i * 70000; j < (video_i + 1) * 70000; j++) {

						video[k] = out.get(j);
						k++;
					}
					
					
					playing = ((double) video_i / (double) Time) * 100;

					bar.setProgress((int) playing);
					Log.e("read", video + "");
					toDraw(video);
					if(isJump)
					{
						video_i=position;
						isJump=false;
					}
					while (isStop)
						;
					if(isStop)
					{
						
					}
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				fc.close();
				bar.setProgress(100);
				isOver=true;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//点击播放界面触发的事件
		case R.id.videoPlayer:
			if (isshowcontrl) {
				contrl.setVisibility(View.INVISIBLE);
				isshowcontrl = false;
			} else {
				contrl.setVisibility(View.VISIBLE);
				isshowcontrl = true;
			}
			break;
		
		//点击控制栏触发的事件
		case R.id.controlVideo:
			//重新播放
			if (isOver) {
				isOver=false;
				refresh();
			}
			//停止播放
			if (isStop) {
				isStop = false;
				playbt.setImageDrawable(getResources().getDrawable(
						R.drawable.play));

			} else {
				playbt.setImageDrawable(getResources().getDrawable(
						R.drawable.stop));
				isStop = true;
			}
			break;
		default:
			break;
		}

	}
	
	//刷新
	 private void refresh() {  
	        finish();  
	        Intent intent = new Intent(VideoActivity.this, VideoActivity.class);  
	        intent.putExtra("folderPath", folderPath);
	        intent.putExtra("photo_name", photo_name);
	        startActivity(intent);  
	    }  

	 //画图
	private void toDraw(byte[] bytes) {
		Bitmap bitmap = getBitmap(bytes);
		if (bitmap != null)
			SimpleDraw(bitmap);
	}

	//将二进制转化为bitmap
	public Bitmap getBitmap(byte[] data) {
		byte[] imageData;

		int indexHead = 0;
		int indexTail = 0;
		int lenth = data.length - 1;
		int i = 0;
		for (i = 0; i < lenth; i++) {
			if (data[i] == flagHead[0] && data[i + 1] == flagHead[1]) {
				indexHead = i;// ��i��ʼ
				break;
			}
		}
		for (; i < lenth; i++) {
			if (data[i] == flagHead[0] && data[i + 1] == flagHead[1]) {
				indexTail = i;// ��i+1����
				if (indexTail - indexHead > 20000)
					break;
			}
		}
		if (indexTail - indexHead > 20000 && indexHead > 0 && indexTail > 0) {
			return BitmapFactory.decodeByteArray(data, indexHead, indexTail
					- indexHead + 1);
		} else {
			return null;
		}

	}

	//将数据显示到surfaceView
	public void SimpleDraw(Bitmap bitmap) {
		Rect rect = new Rect(0, 0, screenWidth, screenHeight);
		Canvas canvas = surfaceHolder.lockCanvas();// �ؼ�:��ȡ����
		Paint mPaint = new Paint();
		if (canvas == null || rect == null || mPaint == null)
			return;
		canvas.drawBitmap(bitmap, null, rect, mPaint);
		surfaceHolder.unlockCanvasAndPost(canvas);
	}
}
