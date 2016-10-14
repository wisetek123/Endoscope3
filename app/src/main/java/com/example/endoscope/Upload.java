package com.example.endoscope;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endoscope.FTP.FTP;
import com.example.endoscope.FTP.ProgressInputStream;
import com.example.endoscope.FTP.FTP.UploadProgressListener;
import com.example.endoscope.adapter.UploadAdapter;
import com.example.endoscope.bean.UploadBean;
import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.db.UploadLogService;
import com.example.endoscope.utils.StreamTool;
import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;


//上传功能
public class Upload extends Activity implements topbarClickListener {
    private TextView filenameText;
    private TextView resulView;
    private ProgressBar uploadbar;
    private UploadLogService logService;
    private Topbar topbar;
    private ListView upload_list;
	private MyDatabaseHelper dbHelper;
	private UploadAdapter upAdapter;
	private UploadBean bean;
	private List<String> up=new ArrayList<String>(); 
	private int j=0;
	private static final String TAG = "Upload";
	
	public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
	public static final String FTP_CONNECT_FAIL = "ftp连接失败";
	public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
	public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";
	
	public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
	public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
	public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

	public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
	public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
	public static final String FTP_DOWN_FAIL = "ftp文件下载失败";
	
	public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
	public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
	private Handler UIclear=new Handler();
	private Handler updateUI=new Handler();
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int length = msg.getData().getInt("size");
			uploadbar.setProgress(length);
			float num = (float)uploadbar.getProgress()/(float)uploadbar.getMax();
			int result = (int)(num * 100);
			resulView.setText(result+ "%");
			if(uploadbar.getProgress()==uploadbar.getMax()){
				Toast.makeText(Upload.this, R.string.success, 1).show();
			}
		}
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload);
        
        //logService = new UploadLogService(this);
       
       topbar = (Topbar)findViewById(R.id.id_topbar);
        
        
        upload_list = (ListView)findViewById(R.id.upload_list);
        
//       upload_list.setContext(R.layout.upload_item);
        Log.e("",getString(R.string.upload));
        
        //设置Topbar中的文字信息
        topbar.setToptitle(getString(R.string.upload));
        
        topbar.setLeftBT(R.drawable.back);
        topbar.setLeftButtonIsVisiable(true);
        topbar.setRightButtonIsVisiable(false);
        topbar.setOnTopbarClickListener(this);
        
        dbHelper = new MyDatabaseHelper(Upload.this,
				"PatientManagement.db", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        
		final LinkedList<UploadBean> upload=new LinkedList<UploadBean>();
		Cursor cursor = db.query("uploadlog", new String[] {"uploadfilepath"}, null,
				null, null, null, null);
		
		if(cursor.moveToFirst()){
		for (int i = 0; i < cursor.getCount(); i++) {
			String num;
			num=Integer.toString(i+1);
			
			String filename = cursor.getString(cursor
					.getColumnIndex("uploadfilepath"));
			bean=new UploadBean(filename, 0);
			up.add(filename);
			upload.add(bean);
			cursor.moveToNext();
		}
		}
		cursor.close();
		upAdapter=new UploadAdapter(this, upload);
		upload_list.setAdapter(upAdapter);
		Log.e("lw",upload.get(0).getuploadfile() );
		new Thread(new Runnable() {			
			@Override
			public void run() {
				int count=up.size();
				 j=0;
				while(count>0){
				
				
                // 上传
            	File file = new File(up.get(j));
                try {
                	
                	//单文件上传
					new FTP().uploadSingleFile(file, "/fff",new UploadProgressListener(){

						@Override
						public void onUploadProgress(String currentStep,long uploadSize,File file) {
							// TODO Auto-generated method stub
							Log.d(TAG, currentStep);										
							if(currentStep.equals(FTP_UPLOAD_SUCCESS)){
								Log.d(TAG, "-----shanchuan--successful");
								
							} else if(currentStep.equals(FTP_UPLOAD_LOADING)){
								long fize = file.length();
								float num = (float)uploadSize / (float)fize;
								final int result = (int)(num * 100);
								updateUI.post(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										upload.get(j).setnum(result);
										Log.e("sd", upload.get(j).getnum()+"");
										upAdapter=new UploadAdapter(Upload.this, upload);
										upload_list.setAdapter(upAdapter);
									}
								});
								Log.d(TAG, "-----shangchuan---"+result + "%");
							}
						}							
					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count--;
				j++;
			}
				upload.clear();
				UIclear.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						
						upAdapter=new UploadAdapter(Upload.this, upload);
						upload_list.setAdapter(upAdapter);
					}
					
				});
				
				
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.delete("uploadlog", null, null);
				
			}
		}).start();
		
//        button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String filename = filenameText.getText().toString();
//				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//					File uploadFile = new File(Environment.getExternalStorageDirectory(), filename);
//					if(uploadFile.exists()){
//						uploadFile(uploadFile);
//					}else{
//						Toast.makeText(Upload.this, R.string.filenotexsit, 1).show();
//					}
//				}else{
//					Toast.makeText(Upload.this, R.string.sdcarderror, 1).show();
//				}
//			}
//		});
    }
    public void leftClick() {
		// TODO Auto-generated method stub
    	//Intent it=new Intent(Upload.this,BrowseActivity.class);
    	//startActivity(it);
		Upload.this.finish();
	}
    
 /*   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
    	
    	filenameText = (TextView)arg1.findViewById(R.id.uploadfile);
    	String path = filenameText.getText().toString();
    	File uploadfile = new File(path);
    	uploadFile(uploadfile);
    }
    /**
     * 上传文件
     * @param uploadFile
     */
  /*private void uploadFile(final File uploadFile) {
	new Thread(new Runnable() {			
		@Override
		public void run() {
			try {
				uploadbar.setMax((int)uploadFile.length());
					String souceid = logService.getBindId(uploadFile);
				String head = "Content-Length="+ uploadFile.length() + ";filename="+ uploadFile.getName() + ";sourceid="+
						(souceid==null? "" : souceid)+"\r\n";
					Socket socket = new Socket("192.168.1.100", 7878);
					OutputStream outStream = socket.getOutputStream();
				outStream.write(head.getBytes());
					
				PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());	
				String response = StreamTool.readLine(inStream);
		        String[] items = response.split(";");
				String responseid = items[0].substring(items[0].indexOf("=")+1);
				String position = items[1].substring(items[1].indexOf("=")+1);
				if(souceid==null){//代表原来没有上传过此文件，往数据库添加一条绑定记录
					logService.save(responseid, uploadFile);
				}
				RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");
					fileOutStream.seek(Integer.valueOf(position));
					byte[] buffer = new byte[1024];
					int len = -1;
					int length = Integer.valueOf(position);
					while( (len = fileOutStream.read(buffer)) != -1){
					outStream.write(buffer, 0, len);
					length += len;
						Message msg = new Message();
						msg.getData().putInt("size", length);
					handler.sendMessage(msg);
					}
				fileOutStream.close();
					outStream.close();
		            inStream.close();
		            socket.close();
		            if(length==uploadFile.length()) logService.delete(uploadFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}*/
	@Override
	public void rightClick() {
		// TODO Auto-generated method stub
		
	}

		
}