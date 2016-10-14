package com.example.endoscope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endoscope.bean.ConstantBean;
import com.example.endoscope.db.MyDatabaseHelper;

//以无线方式接收数据时的显示功能
public class CameraActivity extends Activity implements OnClickListener {

	private ProgressDialog pDialog;
	private MyDatabaseHelper dbHelper;
	private ImageButton photo;
	private ImageButton video;
	private ImageButton browse;
	private ImageButton wifi;

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Chronometer timer;
	

	byte[] flagHead;// jpeg头部
	byte[] flagTail;// jpeg尾部

	private String pname;
	private String pno;
	

	private int screenWidth;
	private int screenHeight;
	private int miss=0;
	private byte[] videoByte;
	// private int recLen = 0;
	private int index = 1;
	private int part = 1;
	private boolean isShow = false;
	private boolean isPhoto = false;
	private boolean isVideo = false;
	private boolean isVideoStart = false;
	private boolean isRunning;
	private boolean isSaving = true;
	private boolean iswifi=false;
	private boolean isEmpty = true;
	private File videoFile;
	private String did;

	private String path = ConstantBean.Path;
	private String imagePath;
	private String videoPath;
	private String recordtime;
	private String recordtimePath;
	
	OutputStream os = null;
	Bitmap frame = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);
		
		//获取屏幕的大小
		DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
		screenWidth = localDisplayMetrics.widthPixels;
		screenHeight = localDisplayMetrics.heightPixels;

		
		//获取上个页面传递的信息
		Intent diagnosis_it = getIntent();
		pname = diagnosis_it.getStringExtra("Pname");
		pno = diagnosis_it.getStringExtra("Pno");
		did = diagnosis_it.getStringExtra("Did");

		File Folder = new File(path);
		path = path + File.separator + pname + "_" + pno;
		imagePath = path + File.separator + "Image";
		videoPath = path + File.separator + "Video";
		File Folder_image_pno = new File(imagePath);
		File Folder_video_pno = new File(videoPath);
		if (!Folder.exists()) {
			Folder.mkdirs();
		}
		if (!Folder_image_pno.exists()) {
			Folder_image_pno.mkdirs();
		}
		if (!Folder_video_pno.exists()) {
			Folder_video_pno.mkdirs();
		}


		initView();
		initData();

	}

	private void initView() {
		surfaceView = (SurfaceView) this.findViewById(R.id.SurfaceView01);
		timer = (Chronometer) this.findViewById(R.id.sign);
		
		surfaceHolder = surfaceView.getHolder();
		surfaceView.setOnClickListener(this);
		

		new Thread(new Runnable() {
			@Override
			public void run() {
				startVideo();
			}
		}).start();

		photo = (ImageButton) findViewById(R.id.photo);
		photo.setOnClickListener(this);

		video = (ImageButton) findViewById(R.id.video);
		video.setOnClickListener(this);

		browse = (ImageButton) findViewById(R.id.browse);
		browse.setOnClickListener(this);
		
		wifi = (ImageButton)findViewById(R.id.wifi);
		wifi.setOnClickListener(this);

		// button.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// button.setText("��ʼ��������,��ȴ�");
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// startVideo();// ��ʼ������Ƶ
		// }
		// }).start();
		// }
		// });

	}

	//开一个线程接受数据
	private void startVideo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getData();
			}
		}).start();

	}

	private void initData() {
		flagHead = new byte[] { (byte) 255, (byte) 216 };
		flagTail = new byte[] { (byte) 255, (byte) 217 };
		isRunning = false;
	}


	public Bitmap getBitmap(byte[] data) {
		byte[] imageData;

		int indexHead = 0;
		int indexTail = 0;
		int lenth = data.length - 1;
		int i = 0;
		for (i = 0; i < lenth; i++) {
			if (data[i] == flagHead[0] && data[i + 1] == flagHead[1]) {
				indexHead = i;
				break;
			}
		}
		for (; i < lenth; i++) {
			if (data[i] == flagHead[0] && data[i + 1] == flagHead[1]) {
				indexTail = i;
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

	//将接收到的数据显示到surfaceView上
	public void SimpleDraw(Bitmap bitmap) {
		if (bitmap == null)
			return;
		if (isPhoto) {
			// Toast.makeText(CameraActivity.this, "aaaaa",
			// Toast.LENGTH_SHORT).show();

			try {
				saveImage(bitmap);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			isPhoto = false;
		}
		Rect rect = new Rect(0, 0, screenHeight*4/3, screenHeight);
		Canvas canvas = surfaceHolder.lockCanvas();// 获取画布
		Paint mPaint = new Paint();
		if (canvas == null || rect == null || mPaint == null)
			return;
		canvas.drawBitmap(bitmap, null, rect, mPaint);
		surfaceHolder.unlockCanvasAndPost(canvas);// 将画布的内容显示到surfaceView
	}

	
	//接受数据
	public void getData() {
		try {
			String FilePath = null;
			Socket socket = new Socket("192.168.10.123", 7060);//从发射板上接受数据
			InputStream inputStream = socket.getInputStream();
			byte dataBuffer[] = new byte[65535];// 设置每个dataBuffer大小为65535
			byte dataImage[] = new byte[70000];
			int totalLen = 0;// 总长度
			int len = 0;// 当前长度
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			isRunning = true;
			isEmpty = false;
			while (isRunning && (len = inputStream.read(dataBuffer)) != -1) {

				totalLen += len;
				baos.write(dataBuffer, 0, len);
				if (totalLen > 70000) {
					
					// 第一次点击录像按钮，检测到isVideoStart为1
					if (isVideoStart) {
						String time = new SimpleDateFormat("yyyyMMddHHmmss",
								Locale.getDefault()).format(new Date(System
								.currentTimeMillis()));
						FilePath = videoPath + File.separator+time+".mp4";
						videoFile = new File(FilePath);
						if (!videoFile.exists()) {
							videoFile.createNewFile();
						}
						 recordtimePath=videoPath + File.separator + time+".txt";
						String pictPath = videoPath + File.separator + time
								+ ".JPEG";
						File pict = new File(pictPath);
						if (!pict.exists()) {
							pict.createNewFile();
						}
						
						//将接收的二进制数组转换为位图
						Bitmap bitmap = getBitmap(baos.toByteArray());
						if (bitmap != null) {
							FileOutputStream imageOut = null;
							imageOut = new FileOutputStream(pictPath);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
									imageOut);
							imageOut.flush();
							imageOut.close();
						}
						Log.e("save", baos.toByteArray() + "");
						isVideoStart = false;
					}
					//第二次点击录像按钮时
					if (isVideo) {
						videoByte = baos.toByteArray();
						if(videoByte.length!=0)
						{
							
						saveVideoTobyte(FilePath, videoByte);
						
						}
						else
						{
							timer.setVisibility(View.GONE);
						}

					}
					toDraw(baos.toByteArray());
					baos.reset();// 关闭文件流
					totalLen = 0;// 重置总长度
					// break;
				}

			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	//画图
	private void toDraw(byte[] bytes) {
		Bitmap bitmap = getBitmap(bytes);
		SimpleDraw(bitmap);
	}

	/*
	 * 保存图片
	 */
	public void saveImage(Bitmap bitmap) throws IOException, Exception {
		String time = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault())
				.format(new Date(System.currentTimeMillis()));
		if (bitmap == null)
			return;
		String imageName = time + ".JPEG";
		File imageFile = new File(imagePath + File.separator + imageName);
		if (imageFile.exists()) {
			imageFile.delete();
		}
		imageFile.createNewFile();
		FileOutputStream imageOut = null;
		imageOut = new FileOutputStream(imageFile);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
		imageOut.flush();
		imageOut.close();
		dbHelper = new MyDatabaseHelper(CameraActivity.this,
				"PatientManagement.db", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("Judge", 1); //将数据库中Judge字段设置为1
		db.update("Patient", values, "Pno = ?", new String[] { pno });
		values.clear();
	}

//	public void setRecorder() {
//		String time = new SimpleDateFormat("yyyyMMddHHmmss",
//				Locale.getDefault())
//				.format(new Date(System.currentTimeMillis()));
//		recorder = new FFmpegFrameRecorder(videoPath + File.separator + time
//				+ ".mp4", screenWidth, screenHeight);
//		recorder.setFormat("mp4");
//		recorder.setFrameRate(5f);
//		try {
//			recorder.start();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	/*
	 * 保存视频
	 */
	public void saveVideo() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isSaving = false;

			}
		}.start();

	}

	public static void saveVideoTobyte(String fileName, byte[] content) {
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.write(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void DeleteImage() {
		Log.e("lw", "part=" + part);

	}

	
	public static String FormatMiss(int miss){     
        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return "●REC "+hh+":"+mm+":"+ss;
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		//点击播放界面触发的事件
		case R.id.SurfaceView01:
			
			//判断按钮是否显示
			if (isShow) {
				photo.setVisibility(View.GONE);
				video.setVisibility(View.GONE);
				browse.setVisibility(View.GONE);
				wifi.setVisibility(View.GONE);
				isShow = false;
			} else {
				photo.setVisibility(View.VISIBLE);
				video.setVisibility(View.VISIBLE);
				browse.setVisibility(View.VISIBLE);
				wifi.setVisibility(View.VISIBLE);
				isShow = true;
			}
			break;

		//点击拍照按钮
		case R.id.photo:
			//判断是否在接收数据
			if(isEmpty)
				return;
			else{
			isPhoto = true;
			//弹出Toast提示拍照成功
			Toast.makeText(this, getString(R.string.photosuccess), Toast.LENGTH_SHORT).show();
			}
			break;

			
		//点击录像按钮触发的事件
		case R.id.video:
			
			//判断是否在接收数据
			if(isEmpty)
				return;
			else{
				
			//判断是第几次点击
			if (isVideo) {
				isVideo = false;
				
				//停止计时
				timer.stop();
				recordtime=FormatMiss(miss);
				
				if(recordtime!=null)
				{
					
					File recordtimeFile=new File(recordtimePath);
					if(!recordtimeFile.exists())
					{
							try {
								recordtimeFile.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
					
					try {
						FileOutputStream fos;
						fos = new FileOutputStream(recordtimeFile);
						byte [] bytes = recordtime.getBytes();   
				        
							fos.write(bytes);     
							fos.close(); 
				        recordtime=null;
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}    
			        
				}
				miss=-2;
				
				//显示加载框
				new Load().execute();
				//隐藏计时窗口
				timer.setVisibility(View.GONE);

			} else {
				isVideo = true;
				isVideoStart = true;
				// setRecorder();
				
				timer.setVisibility(View.VISIBLE);
				
				timer.setBase(SystemClock.elapsedRealtime());
				timer.start();
				timer.setOnChronometerTickListener(new OnChronometerTickListener() {  
					  
                    @Override  
                    public void onChronometerTick(Chronometer ch) {  
                    	 miss++; 
                        ch.setText(FormatMiss(miss));  
                        
  
                    }  
                });  
				}
			
			
				//后台保存
				new Save().execute();
			}
			break;

		//点击目录按钮触发的事件
		case R.id.browse:


			Intent it = new Intent(CameraActivity.this, BrowseActivity.class);
			it.putExtra("imagePath", imagePath);
			it.putExtra("videoPath", videoPath);
			startActivity(it);
			break;
			// CameraActivity.this.finish();

		//点击wifi设置按钮出发的功能
		case R.id.wifi:
			Intent wifi = new Intent(Settings.ACTION_WIFI_SETTINGS);
			startActivity(wifi);
			break;
			
		default:
			break;
		}
		
	}

	class Save extends AsyncTask<string, string, string> {

		@Override
		protected string doInBackground(string... params) {
			// TODO Auto-generated method stub
			saveVideo();
			dbHelper = new MyDatabaseHelper(CameraActivity.this,
					"PatientManagement.db", null, 1);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("Judge", 1);//将数据库中的Judge设置为1
			db.update("Patient", values, "Pno = ?", new String[] { pno });
			values.clear();

			return null;
		}

	}

	class Load extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(CameraActivity.this);
			pDialog.setMessage("正在处理中...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			while (isSaving) {
			}
			return "";
			// TODO Auto-generated method stub
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isRunning = false;
		super.onDestroy();
	}
	
	//点击返回键时触发的事件
	public boolean onKeyDown(int keyCode, KeyEvent event){
	
			
		
		Intent intent = new Intent(CameraActivity.this,PatientActivity.class);
		intent.putExtra("Pno", pno);
		intent.putExtra("Did", did);
		startActivity(intent);
		finish();
		return true;
	}

}
