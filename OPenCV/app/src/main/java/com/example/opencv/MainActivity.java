package com.example.opencv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.opencv.Vedio.VedioActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private String TAG = "OpenCV_Test";
    //OpenCV的相机接口
    private CameraBridgeViewBase mCVCamera;
    //缓存相机每帧输入的数据
    private int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    private Mat mRgba;
    private ImageButton button;
    private ImageButton exchange;
    private int mCameraIndexCount = 0;
    private ImageButton vedio;
    private static final int VIEW_MODE_RGBA = 0;
    private static final int VIEW_MODE_GRAY = 1;
    private static final int VIEW_MODE_CANNY = 2;
    private static final int VIEW_MODE_FEATURES = 5;
    private int mViewMode;
    private Mat mIntermediateMat;
    private Mat mGray;

    /**
     * 通过OpenCV管理Android服务，初始化OpenCV
     **/
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mCVCamera.setCameraIndex(frontCamera);
                    mCVCamera.enableView();
                    break;
                default:
                    break;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                button.performClick();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化并设置预览部件
        mCVCamera = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mCVCamera.setVisibility(SurfaceView.VISIBLE);
        mCVCamera.setCvCameraViewListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        //拍照按键
        button = (ImageButton) findViewById(R.id.deal_btn);
        exchange = (ImageButton) findViewById(R.id.frontCamera);
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCVCamera.disableView();
                mCVCamera.setCameraIndex(++mCameraIndexCount % getmCameraCount());
                mCVCamera.enableView();
            }
        });

        vedio = findViewById(R.id.vedio);
        vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this, VedioActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRgba != null) {
                    if (!mRgba.empty()) {
                        Mat inter = new Mat(mRgba.width(), mRgba.height(), CvType.CV_8UC4);
                        Log.e("Mat", "...............1...............");
                        //将四通道的RGBA转为三通道的BGR，重要！！
                        Imgproc.cvtColor(mRgba, inter, Imgproc.COLOR_RGBA2BGR);
                        Log.e("Mat", "...............2...............");
                        File sdDir = null;
                        //判断是否存在机身内存
                        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                        if (sdCardExist) {
                            //获得机身储存根目录
                            sdDir = Environment.getExternalStorageDirectory();
                            Log.e("Mat", "...............3...............");
                        }
                        //将拍摄准确时间作为文件名
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        String filename = sdf.format(new Date());
                        String savepath = sdDir + "/DCIM/Camera/";
                        File f = new File(savepath);
                        if (!f.exists()) {
                            f.mkdirs();
                        }
                        String filePath = sdDir + "/DCIM/Camera/" + filename + ".png";
                        Log.e("Mat", "..............." + filePath + "...............");
                        //将转化后的BGR矩阵内容写入到文件中
                     //   Toast.makeText(MainActivity.this, "测试", Toast.LENGTH_SHORT).show();

                        if (Imgcodecs.imwrite(filePath, inter)) {
                            Toast.makeText(MainActivity.this, "图片保存到: " + filePath, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();//通过广播通知图片进行管理。
                            intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.parse("file://" + filePath);
                            intent.setData(uri);
                            MainActivity.this.sendBroadcast(intent);
                        } else
                            Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.grayItem:
                mViewMode = VIEW_MODE_GRAY;
                break;
            case R.id.rgbItem:
                mViewMode = VIEW_MODE_RGBA;
                break;
            case R.id.cannyItem:
                mViewMode = VIEW_MODE_CANNY;
                break;
            case R.id.exitItem:
                finish();
        }
        return true;
    }

    private int getmCameraCount() {
        return Camera.getNumberOfCameras();
    }

    protected void onResume() {
        /***强制横屏***/
//        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
            //横屏后才加载部件，否则会FC
            if (!OpenCVLoader.initDebug()) {
                Log.d(TAG, "OpenCV library not found!");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
            } else {
                Log.d(TAG, "OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
      //  }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mCVCamera != null) {
            mCVCamera.disableView();
        }
        super.onDestroy();
    }

    //对象实例化及基本属性的设置，包括长度、宽度和图像类型标志
    public void onCameraViewStarted(int width, int height) {
        Log.e("Mat", "...............4...............");
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    /**
     * 图像处理都写在这里！！！
     **/
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final int viewMode = mViewMode;
        mRgba = inputFrame.rgba();
        switch (viewMode){
            case VIEW_MODE_GRAY:
                Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case VIEW_MODE_RGBA:
                mRgba = inputFrame.rgba();
                break;
            case VIEW_MODE_CANNY:
                mRgba = inputFrame.rgba();
                Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
        }
       // mRgba = inputFrame.rgba();  //一定要有！！！不然数据保存不进MAT中！！！
        //直接返回输入视频预览图的RGB数据并存放在Mat数据中
        return mRgba;
    }


    //结束时释放
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
//
//    }
//
//
//
//}
//package com.example.opencv;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.media.MediaRecorder;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import java.io.File;
//import java.util.Date;
//
//public class MainActivity extends Activity implements OnClickListener {
//
//    private static final String TAG = "MainActivity";
//    // 程序中的两个按钮
//    Button record, stop,exchange;
//    // 系统的视频文件
//    File videoFile;
//    MediaRecorder mRecorder;
//    // 显示视频预览的SurfaceView
//    SurfaceView sView;
//    // 记录是否正在进行录制
//    private boolean isRecording = false;
//    private int MY_PERMISSIONS_REQUEST_Vedio=2;
//    private int MY_PERMISSIONS_WRITE=5;
//    private int MY_CAMERA=3;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        // 获取程序界面中的两个按钮
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=        PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO},
//                    MY_PERMISSIONS_REQUEST_Vedio);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=        PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA},
//                    MY_CAMERA);
//        }
//        record = (Button) findViewById(R.id.record);
//        stop = (Button) findViewById(R.id.stop);
//        exchange=(Button)findViewById(R.id.ex);
//        // 让stop按钮不可用
//        stop.setEnabled(false);
//        // 为两个按钮的单击事件绑定监听器
//        record.setOnClickListener(this);
//        stop.setOnClickListener(this);
//        // 获取程序界面中的SurfaceView
//        sView = (SurfaceView) this.findViewById(R.id.sView);
//        // 设置分辨率
//        sView.getHolder().setFixedSize(1920, 1080);   // 1080P
//        // 设置该组件让屏幕不会自动关闭
//        sView.getHolder().setKeepScreenOn(true);
//    }
//
//    @Override
//    public void onClick(View v) {
//        // 单击录制按钮
//        String filePath="";
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_WRITE);
//        }
//        if (v.getId() == R.id.record) {
//            File sdDir = null;
//            //判断是否存在机身内存
//            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//            if(sdCardExist) {
//                //获得机身储存根目录
//                sdDir = Environment.getExternalStorageDirectory();
//                Log.e("Mat","...............3...............");
//            }
//            //将拍摄准确时间作为文件名
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//            String filename = sdf.format(new Date());
//            String savepath=sdDir + "/DCIM/Camera/";
//            File f=new File(savepath);
//            if(!f.exists()){
//                f.mkdirs();
//            }
//            filePath = sdDir + "/DCIM/Camera/" + filename + ".mp4";
//            try {
//                // 创建保存录制视频的视频文件
//                videoFile = new File(filePath);
//                // 创建MediaPlayer对象
//                mRecorder = new MediaRecorder();
//                mRecorder.reset();
//                // 设置从麦克风采集声音
//                mRecorder.setAudioSource(MediaRecorder
//                        .AudioSource.MIC);
//                // 设置从摄像头采集图像
//                mRecorder.setVideoSource(MediaRecorder
//                        .VideoSource.CAMERA);
//                // 设置视频文件的输出格式
//                // 必须在设置声音编码格式、图像编码格式之前设置
//                mRecorder.setOutputFormat(MediaRecorder
//                        .OutputFormat.MPEG_4);
//                // 设置声音编码的格式
//                mRecorder.setAudioEncoder(MediaRecorder
//                        .AudioEncoder.DEFAULT);
//                // 设置图像编码的格式
//                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//            //    mRecorder.setVideoSize(1920, 1080);  // 1080P
//                // 每秒16帧
//            //    mRecorder.setVideoFrameRate(16);
//                mRecorder.setOutputFile(videoFile.getAbsolutePath());
//                // 指定使用SurfaceView来预览视频
//                mRecorder.setPreviewDisplay(sView.getHolder().getSurface());  // ①
//                mRecorder.prepare();
//                // 开始录制
//                mRecorder.start();
//                Log.d(TAG, "---recording---");
//                // 让record按钮不可用
//                record.setEnabled(false);
//                // 让stop按钮可用
//                stop.setEnabled(true);
//                isRecording = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        else if (v.getId() == R.id.stop) {   // 单击停止按钮
//            // 如果正在进行录制
//            if (isRecording) {
//                // 停止录制
//                mRecorder.stop();
//                // 释放资源
//                mRecorder.release();
//                Toast.makeText(MainActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent();//通过广播通知图片进行管理。
//                intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                Uri uri = Uri.parse("file://" + filePath);
//                intent.setData(uri);
//                MainActivity.this.sendBroadcast(intent);
//
//                mRecorder = null;
//                // 让record按钮可用
//                record.setEnabled(true);
//                // 让stop按钮不可用
//                stop.setEnabled(false);
//            }
//        }
//    }
    }
}