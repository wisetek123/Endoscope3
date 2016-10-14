package com.example.endoscope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.example.endoscope.view.Topbar;
import com.example.endoscope.view.Topbar.topbarClickListener;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;
//import android.widget.Toast;

//获取usb设备信息
public class SelectActivity extends Activity implements topbarClickListener{
	
	private ListView mListView;
	private UsbManager mUsbManager;
	private Topbar mTopbar;
	private String pno;
	private String did;
	private String pname;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select);
		
		//获取上个页面传递的信息
		Intent diagnosis_it = getIntent();
		pno = diagnosis_it.getStringExtra("Pno");
		did = diagnosis_it.getStringExtra("Did");
		pname=diagnosis_it.getStringExtra("Pname");
		
		
		mListView = (ListView)findViewById(R.id.DeviceList);
		
		
		mTopbar = (Topbar)findViewById(R.id.id_topbar);
		
		//设置Topbar中的文字嘻嘻
		mTopbar.setToptitle(getString(R.string.devicelist));
		
		//设置Topbar中的按钮图片 
		mTopbar.setLeftBT(R.drawable.back);
		mTopbar.setRightBT(R.drawable.refresh);
		
		//显示Topbar中的按钮
		mTopbar.setLeftButtonIsVisiable(true);
		mTopbar.setRightButtonIsVisiable(true);
		
		mTopbar.setOnTopbarClickListener(this);
		
		//获取设备信息
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("Device","emPIA HD Device" +"\n\n" +"Vid:" + device.getVendorId() + "    Pid:"+ device.getProductId());
			
			
			list.add(item);
//			Toast.makeText(this, device.toString(), Toast.LENGTH_LONG).show();
			}
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.list_item,
                new String[] {"Device"},
                new int[] {R.id.name}
                );
               mListView.setAdapter(adapter);
               
         //点击设备信息时触发的时间
         mListView.setOnItemClickListener(new OnItemClickListener()
         {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent show_it = new Intent(SelectActivity.this,
						UsbVideoActivity.class);
				show_it.putExtra("Pno", pno);
				show_it.putExtra("Did", did);
				show_it.putExtra("Pname",pname);
				startActivity(show_it);
				
				
				
			}
        	 
         });

	}

	//点击左边按钮触发的事件
	@Override
	public void leftClick() {
		// TODO Auto-generated method stub
		Intent back = new Intent(SelectActivity.this,PatientActivity.class);
		back.putExtra("Pno", pno);
		back.putExtra("Did", did);
		startActivity(back);
		finish();
	}

	//点击右边按钮触发的事件
	@Override
	public void rightClick() {
		mListView.invalidate();
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("Device","emPIA HD Device" +"\n\n" +"Vid:" + device.getVendorId() + "    Pid:"+ device.getProductId());
			
			
			list.add(item);
			}
		 SimpleAdapter adapter = new SimpleAdapter(
	                this,
	                list,
	                R.layout.list_item,
	                new String[] {"Device"},
	                new int[] {R.id.name}
	                );
		mListView.setAdapter(adapter);
		
	}
	
	//按返回键时触发的事件
	public boolean onKeyDown(int keyCode, KeyEvent event){
		Intent back = new Intent(SelectActivity.this,MainActivity.class);
		back.putExtra("Pno", pno);
		back.putExtra("Did", did);
		startActivity(back);
		finish();
		return true;
	}

	

}
