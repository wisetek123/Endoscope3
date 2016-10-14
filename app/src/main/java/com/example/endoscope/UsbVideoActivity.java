package com.example.endoscope;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.sql.Timestamp;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.opengl.GLES20;
import android.os.Environment;
import android.provider.CallLog;
import android.test.AndroidTestCase;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.media.MediaCodecInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.Object;
import java.util.LinkedList;
import java.util.Locale;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import com.empia.lib.EMPIA_LIB_EM28284;
import com.example.endoscope.bean.ConstantBean;
import com.example.endoscope.bean.DiagnosisBean;
import com.example.endoscope.db.MyDatabaseHelper;
import com.example.endoscope.view.Topbar;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaCodec;
import android.media.MediaMuxer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

//以有线方式接收数据时的显示功能

public class UsbVideoActivity extends Activity implements View.OnClickListener {



    MediaMuxer muxer;// = new MediaMuxer("temp.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
  UsbManager mUsbManager = null;
    IntentFilter filterAttached_and_Detached = null;
    // private TextView textView;
    private EMPIA_LIB_EM28284 empialib = null;
    // private int res;
    private MediaCodec mediaCodec;
    private BufferedOutputStream outputStream;
    private int EMPIA_VIDEOIN_COMPOSITE = 0x00;
    private int EMPIA_VIDEOIN_COMPONENT = 0x01;
    private int EMPIA_VIDEOIN_COMPONENT_P = 0x02;
    private int EMPIA_VIDEOIN_SVIDEO = 0x03;
    private int EMPIA_VIDEOIN_HDMI = 0x04;
    private int EMPIA_VIDEOSTD_NTSC = 0x01;
    private int EMPIA_VIDEOSTD_PAL = 0x02;
    private int EMPIA_VIDEOSTD_SECAM = 0x03;
    private int VideoOffSet = 0;
    private int miss = 0;
    private int framecounter=0;
    private boolean video_OK = false;
    // private boolean Res;
    private boolean RUN_THREAD = true;
    private byte[] databuf = new byte[307200* 3/2];
    private byte[] temp=new byte[307200*3/2];
    private byte[] mEncoderH264Buf;
    private byte[] mMediaHead = null;
    private int length = 460800;
    // private Handler handler = new Handler();
    private int screenWidth;
    private int screenHeight;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Surface surface;
    private Chronometer timer;
    private ProgressDialog pDialog;
    private MyDatabaseHelper dbHelper;
    private ImageButton photo;
    private ImageButton video;
    private ImageButton browse;
    private ImageButton wifi;
    private int resolutionWidth = 640, resolutionHeight = 480;
    private String path = ConstantBean.Path;
    private String imagePath;
    private String videoPath;
    private String recordtime;
    private String recordtimePath;
    private String  did;
    //	private int size=20;
//	private byte[][] databu=new byte[size][720 * 576 * 2];
    ByteArrayOutputStream tempsave = new ByteArrayOutputStream();
    //private int i = 0;
    //private int j = 0;


    private boolean isShow = false;
    private boolean isPhoto = false;
    private boolean isVideo = false;
    private boolean isVideoStart = false;
    private boolean isRunning;
    private boolean isSaving = true;
    private boolean iswifi = false;
    private boolean isEmpty = true;
    private int mCount = 0;
    private static final String TAG = "EncodeDecodeTest";
    private static final boolean VERBOSE = false; // lots of logging
    private static final boolean DEBUG_SAVE_FILE = false; // save copy of encoded movie
    private static final String DEBUG_FILE_NAME_BASE = "/sdcard/test.";
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30; // 15fps
    private static final int IFRAME_INTERVAL = 10; // 10 seconds between I-frames
    // movie length, in frames
    private static final int NUM_FRAMES = 30; // two seconds of video
    private static final int TEST_Y = 120; // YUV values for colored rect
    private static final int TEST_U = 160;
    private static final int TEST_V = 200;
    private static final int TEST_R0 = 0; // RGB equivalent of {0,0,0}
    private static final int TEST_G0 = 136;
    private static final int TEST_B0 = 0;
    private static final int TEST_R1 = 236; // RGB equivalent of {120,160,200}
    private static final int TEST_G1 = 50;
    private static final int TEST_B1 = 186;
    final int TIMEOUT_USEC = 0;
    // size of a frame, in pixels
    private int mWidth = -1;
    private int mHeight = -1;
    // bit rate, in bits per second
    private static int mBitRate = 1250000;
    // largest color component delta seen (i.e. actual vs. expected)
    private int mLargestColorDelta;
    private String patientNo;
    private String pno;
    private String pid;
    private String pname;
    private String psex;
    private String page;
    private String pidnum;
    private String padr;
    private String ddate;
    private String dsymptom;
    private String dresult;
    private int mSurfaceWidth,mSurfaceHeight;
    private static final File OUTPUT_DIR = Environment.getExternalStorageDirectory();
    private MediaCodec mEncoder;
    int mTrackIndex = -1;
    boolean mMuxerStarted = false;
    MediaMuxer mMuxer;
    private MediaCodec.BufferInfo mBufferInfo;
    MediaFormat format;
   //// private CodecInputSurface mInputSurface;
byte rawData[];

    private static final String ACTION_USB_PERMISSION = "com.example.endoscope.USB_PERMISSION";
    //

    //广播监听事件
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        if (device != null) {
//							onCreate(null);
                            refresh();
                            Log.d("1", "PERMISSION-" + device);
                        }
                    }
                }
            }

        }
    };


    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_usbvideo);

        //setupEncoder("video/avc",640,480);
        //初始化渲染程序

        //
        // textView = (TextView) findViewById(R.id.textView);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        //获取屏幕大小
        DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
        screenWidth = localDisplayMetrics.widthPixels;
        screenHeight = localDisplayMetrics.heightPixels;
        surfaceView = (SurfaceView) this.findViewById(R.id.SurfaceView);
        surfaceHolder = surfaceView.getHolder();


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



        //设置过滤器
     filterAttached_and_Detached = new IntentFilter(ACTION_USB_PERMISSION);
        filterAttached_and_Detached
                .addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filterAttached_and_Detached
               .addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);

        //启动监听
        registerReceiver(mUsbReceiver, filterAttached_and_Detached);
       mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
       HashMap<String,UsbDevice> deviceList = mUsbManager.getDeviceList();
       Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
      UsbDevice device = (UsbDevice) deviceIterator.next();
     PendingIntent mPermissionIntent;
        mPermissionIntent = PendingIntent.getBroadcast(UsbVideoActivity.this,
              0, new Intent(ACTION_USB_PERMISSION),
               PendingIntent.FLAG_ONE_SHOT);
      initView();
       if (!mUsbManager.hasPermission((UsbDevice) device)) {

        //获取访问usb的权限
       mUsbManager.requestPermission(device, mPermissionIntent);
       } else {

       empialib = new EMPIA_LIB_EM28284(mUsbManager);
      empialib.init();
      empialib.set_inputsource(EMPIA_VIDEOIN_COMPOSITE);
        empialib.set_videostandard(1);
      empialib.setup_scaler(640, 480);
        isEmpty = false;


        new Thread(new Runnable() {

            @Override
            public void run() {
               // mEncoderH264Buf = new byte[10240];





                empialib.capture_start();
                while (RUN_THREAD) {

                    //接收数据，放入databuf
                  //////  databuf[2000] = 20;





                   length = empialib.read_yuv_data(databuf);
                   ////// frameratelimit(60);








                    if(isVideoStart){
                        prepareEncoder();
                        drainEncoder();}
                    else toDraw(databuf, length);


                }

            }
        }).start();
    }}

    //}
    private void initView() {

        timer = (Chronometer) this.findViewById(R.id.sign);

        surfaceHolder = surfaceView.getHolder();
        surfaceView.setOnClickListener(this);

        photo = (ImageButton) findViewById(R.id.photo);
        photo.setOnClickListener(this);

        video = (ImageButton) findViewById(R.id.video);
        video.setOnClickListener(this);

        browse = (ImageButton) findViewById(R.id.browse);
        browse.setOnClickListener(this);

        wifi = (ImageButton) findViewById(R.id.wifi);
        wifi.setOnClickListener(this);

        // button.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // button.setText("");
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // startVideo();//
        // }
        // }).start();
        // }
        // });

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
                if (isEmpty)
                    return;
                else {

                    isPhoto = true;
                    //弹出Toast提示拍照成功
                    Toast.makeText(this, getString(R.string.photosuccess), Toast.LENGTH_SHORT).show();
                }
                break;


            //点击录像按钮触发的事件
            case R.id.video:

                //判断是否在接收数据
                if (mUsbReceiver.isOrderedBroadcast())//这个还是不对，应该检测是否读取到数据
                    return;
                else {

                    //判断是第几次点击
                    if (isVideo) {
                        isVideoStart=false;
                        isVideo = false;


                        //停止计时
                       // timer.stop();
                      //  recordtime = FormatMiss(miss);

                     //   if (recordtime != null) {
                            //mux here
                    //    clearall();



                      //  }
                     //   miss = -2;

                        //显示加载框
                    //    new Load().execute();
                        //隐藏计时窗口
                     ///   timer.setVisibility(View.GONE);


                    } else {
                        isVideo = true;
                        isVideoStart = true;
                        // setRecorder();

                      ///  timer.setVisibility(View.VISIBLE);

                     ///   timer.setBase(SystemClock.elapsedRealtime());
                      ///  timer.start();
                       // length=1;
                       // length=2;
                       // timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

                         //   @Override
                         //   public void onChronometerTick(Chronometer ch) {
                         //       miss++;
                        //        ch.setText(FormatMiss(miss));


                            //}
                      //  });
                    }


                }
                break;

            //点击目录按钮触发的事件
            case R.id.browse:


                Intent it = new Intent(UsbVideoActivity.this, BrowseActivity.class);
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


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void prepareEncoder() {
        mBufferInfo = new MediaCodec.BufferInfo();

        format = MediaFormat.createVideoFormat(MIME_TYPE, 640, 480);


        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_BIT_RATE, 2000000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,21);//may be fixed later
        if (VERBOSE) Log.d(TAG, "format: " + format);

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        //
        // If you want to have two EGL contexts -- one for display, one for recording --
        // you will likely want to defer instantiation of CodecInputSurface until after the
        // "display" EGL context is created, then modify the eglCreateContext call to
        // take eglGetCurrentContext() as the share_context argument.
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);


       mEncoder.start();


      //  /*ByteBuffer aaa=*///ByteBuffer[] inputBuffers =mEncoder.getInputBuffers();
      //  ByteBuffer bbb=
         //       mEncoder.getOutputBuffers();

   //    ByteBuffer[] inputBuffers = mEncoder.getOutputBuffers();
      // ByteBuffer[] OutputBuffers = mEncoder.getOutputBuffers();

        // Output filename.  Ideally this would use Context.getFilesDir() rather than a
        // hard-coded output directory.
        String time = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault())


                .format(new Date(System.currentTimeMillis()));

       /* String time = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
        if (bitmap == null)
            return;
        String imageName = pno+time +".JPEG";
        File imageFile = new File(imagePath + File.separator + imageName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        imageFile.createNewFile();
        FileOutputStream imageOut = null;*/







        String pathName = pno+time + ".mp4";
        String outputPath = new File(videoPath,
                 pathName).toString();
        Log.d(TAG, "output file is " + outputPath);


        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        try {
            mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException ioe) {
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    private void drainEncoder(){



        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
        ByteBuffer[] outputBuffers = mEncoder.getOutputBuffers();


        while (isVideoStart) {
            length=empialib.read_yuv_data(databuf);
            toDraw(databuf,length);

            for (int i = 0; i < 307200; i++)
            {
                //convert 420p to semi420p
               temp[i]=databuf[i];}
            for (int i = 0; i < 76800; i++){
               temp[307200+i*2]=databuf[307200+i];
              temp[307200+i*2+1]=databuf[307200+i+76800];
          }
            long timestamp = framecounter++ * 1000000 / FRAME_RATE;
            int inputBufferIndex = mEncoder.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                // fill inputBuffers[inputBufferIndex] with valid data
               /// ...
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(temp);
                mEncoder.queueInputBuffer(inputBufferIndex,0 ,length,timestamp,0);
            }

            int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo,0);
            if (outputBufferIndex >= 0) {
                ByteBuffer encodedData=outputBuffers[outputBufferIndex];
                // outputBuffer is ready to be processed or rendered.
                mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);

                mEncoder.releaseOutputBuffer(outputBufferIndex, false);

            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mEncoder.getOutputBuffers();
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // Subsequent data will conform to new format.
                format = mEncoder.getOutputFormat();
                mTrackIndex = mMuxer.addTrack(format);

                mMuxer.start();
                mMuxerStarted = true;

            }
        }

        mEncoder.stop();
        mEncoder.release();
        mEncoder = null;
        mMuxer.stop();
        mMuxer.release();





    }










/*private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }
        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (isVideoStart) {
            toDraw(databuf,length);
            int inputBufferIndex = mEncoder.dequeueInputBuffer(0);
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            inputBuffer.clear();
            inputBuffer.put(databuf);
            long timestamp = framecounter++ * 1000000 / FRAME_RATE;
            mEncoder.queueInputBuffer(inputBufferIndex,0 ,length,timestamp,0);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer.addTrack(newFormat);

                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }*//*
    private void drainEncoder(boolean endOfStream) {


        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }


        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
        ByteBuffer[] OutputBuffers = mEncoder.getOutputBuffers();

        MediaFormat newFormat = mEncoder.getOutputFormat();

        mTrackIndex = mMuxer.addTrack(newFormat);

        mMuxer.start();
        mMuxerStarted = true;
        while (isVideoStart) {
            toDraw(databuf,length);
            int inputBufferIndex = mEncoder.dequeueInputBuffer(0);
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();


            inputBuffer.put(databuf);
            long timestamp = framecounter++ * 1000000 / FRAME_RATE;
            mEncoder.queueInputBuffer(inputBufferIndex,0 ,length,timestamp,0);



           // ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    isVideoStart=false;
                    isVideo=false;
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                OutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
              //  if (mMuxerStarted) {
            //        throw new RuntimeException("format changed twice");
           //     }
                newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer


            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = OutputBuffers[encoderStatus];


                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                  //  mBufferInfo.size=framecounter*460800;
                   // encodedData.position(mBufferInfo.offset);
                  //  encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                     int a=1;
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

               /// if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
               ///     if (!endOfStream) {
              ///          Log.w(TAG, "reached end of stream unexpectedly");
             ///       } else {
            ///            if (VERBOSE) Log.d(TAG, "end of stream reached");
             ///       }
                   // break;      // out of while
            ///    }
            }

        }
    }*/

private void putDataIntoEncoderBuffer(int framecounter){





}

    private void releaseEncoder() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }






    //将数据显示到surfaceView上
    public void toDraw(byte[] databuf, int length) {




        if (databuf.length <= 0) return;
        Bitmap bitmap = rawByteArray2RGBABitmap2(databuf, 640, 480);
        Rect rect = new Rect(0, 0, screenHeight * 4 / 3, screenHeight);
        //Rect rect = new Rect(0, 0, 640, 480);
        Canvas canvas = surfaceHolder.lockCanvas();
        Paint mPaint = new Paint();
        if (canvas == null || rect == null || mPaint == null) return;
        canvas.drawBitmap(bitmap, null, rect, mPaint);
        surfaceHolder.unlockCanvasAndPost(canvas);
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

    }

    //将接收的yuv数据转化为bitmapja
    public Bitmap rawByteArray2RGBABitmap2(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                //转化效率调试代码


                int y = (0b11111111 & ((int) data[i * width + j]));
				/*int u = (0b11111111 & ((int) data[307200+i * width + j]));
				int v = (0b11111111 & ((int) data[307200+76800+i * width + j]));*/

                int u = (0xff & ((int) data[frameSize + (i >> 2) * width + (j >> 1)]));
                int v = (0xff & ((int) data[frameSize + (i >> 2) * width + (j >> 1) + 76800]));
                //y = y < 0 ? 0 : y;
                y = y < 0 ? 0 : (y > 255 ? 255 : y);
                int r = y + 7 * (v - 128) / 5;//减去10调整白平衡
                int g = y - 35 * (u - 128) / 100 - 7 * (v - 128) / 10;
                int b = y + 18 * (u - 128) / 10;
				/*int r=10;
				int g=100;
				int b=200;//Color Checkpoint
				int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
				int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128)
						- 0.391f * (u - 128));
				int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));*/
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = 0xff000000 + (r << 16) + (g << 8) + b;
            }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }


    //刷新
    public void refresh() {
        Intent refresh = new Intent(UsbVideoActivity.this, UsbVideoActivity.class);
        startActivity(refresh);
        finish();
    }

    /*	public static void saveVideoTobyte(String fileName, byte[] content) {
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
        }*/
    //start the surface capturing.


    private static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }


    public void saveImage(Bitmap bitmap) throws IOException, Exception {
        String time = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
        if (bitmap == null)
            return;
        String imageName = pno+time +".JPEG";
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
        dbHelper = new MyDatabaseHelper(UsbVideoActivity.this,
                "PatientManagement.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Judge", 1); //将数据库中Judge字段设置为1
        db.update("Patient", values, "Pno = ?", new String[] { pno });
        values.clear();
    }









    public  String FormatMiss(int miss) {


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }
    public void ininameinfo(){

        //获取上个页面传递过来的信息
        Intent diagnosis_it = getIntent();
        patientNo = diagnosis_it.getStringExtra("Pno");
        did = diagnosis_it.getStringExtra("Did");


        //读取病人的信息
        dbHelper = new MyDatabaseHelper(UsbVideoActivity.this,
                "PatientManagement.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor patient_cursor = db.query("Patient", null, "Pno = ?",
                new String[] { patientNo }, null, null, null);
        Cursor pd_cursor = db.query("PD", null, "Pno = ?",
                new String[] { patientNo }, null, null, null);
        pd_cursor.moveToFirst();

        pd_cursor.close();
        patient_cursor.moveToFirst();
        pno = patientNo;
        pid = patient_cursor.getString(patient_cursor.getColumnIndex("Pid"));
        psex = patient_cursor.getString(patient_cursor.getColumnIndex("Psex"));
        patient_cursor.close();
        Cursor diagnosis_cursor = db.query("Diagnosis", null, "Pno = ?",
                new String[] { patientNo }, null, null, null);
                String date = diagnosis_cursor.getString(diagnosis_cursor
                        .getColumnIndex("Ddate"));
                ddate = diagnosis_cursor.getString(diagnosis_cursor
                        .getColumnIndex("Ddate"));
        diagnosis_cursor.close();}

public void clearall(){
    if (VERBOSE) Log.d(TAG, "releasing encoder objects");
    if (mEncoder != null) {
        mEncoder.stop();
        mEncoder.release();
        mEncoder = null;
    }

    if (mMuxer != null) {
        mMuxer.stop();
        mMuxer.release();
        mMuxer = null;
    }


}


    public void saveVideo() {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                isSaving = false;

            }
        }.start();

    }

  /*这个方法真的够强行的啊  public static void saveVideoTobyte(String fileName, byte[] content) {
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
    }/*/
public void frameratelimit(int limitation){
    limitation=1000/limitation;
    while((System.currentTimeMillis()%(limitation)!=0));


}





    class Load extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(UsbVideoActivity.this);
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


}
