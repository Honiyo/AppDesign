package com.example.opencv;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Date;

//import static chenyi.ncu.edu.cn.ch01.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {
    private static final String TAG = "CameraActivity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    private static final int       VIEW_MODE_RGBA     = 0;
    private static final int       VIEW_MODE_GRAY     = 1;
    private static final int       VIEW_MODE_CANNY    = 2;
    private static final int       VIEW_MODE_FEATURES = 5;
    private static int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    private int                    mViewMode;
    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;
    private Button bt;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:{
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /**
     * 第一次创建时调用
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "called onCreate");
        //权限检查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
        //将窗口变亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        bt=(Button)findViewById(R.id.shot_btn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRgba != null) {
                    if(!mRgba.empty()) {
                        Mat inter = new Mat(mRgba.width(), mRgba.height(), CvType.CV_8UC4);
                        Log.e("Mat","...............1...............");
                        //将四通道的RGBA转为三通道的BGR，重要！！
                        Imgproc.cvtColor(mRgba, inter, Imgproc.COLOR_RGBA2BGR);
                        Log.e("Mat","...............2...............");
                        File sdDir = null;
                        //判断是否存在机身内存
                        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                        if(sdCardExist) {
                            //获得机身储存根目录
                            sdDir = Environment.getExternalStorageDirectory();
                            Log.e("Mat","...............3...............");
                        }
                        //将拍摄准确时间作为文件名
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        String filename = sdf.format(new Date());
                        String savepath=sdDir + "/Pictures/OpenCV/";
                        File f=new File(savepath);
                        if(!f.exists()){
                            f.mkdirs();
                        }
                        String filePath = sdDir + "/Pictures/OpenCV/" + filename + ".png";
                        Log.e("Mat","..............."+filePath+"...............");
                        //将转化后的BGR矩阵内容写入到文件中
                        Imgcodecs.imwrite(filePath, inter);
                        Toast.makeText(MainActivity.this, "图片保存到: "+ filePath, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause(){
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /**
           *创建菜单
           */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

   /**
          *选择菜单项的处理
          */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.grayItem:
                mViewMode = VIEW_MODE_GRAY;
                break;
            case R.id.rgbItem:
                mViewMode = VIEW_MODE_RGBA;
                break;
            case R.id.cannyItem:
                mViewMode = VIEW_MODE_CANNY;
                break;
            case R.id.exitItem: finish();
        }
        return true;
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();

    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        final int viewMode = mViewMode;
        switch (viewMode) {
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

        return mRgba;
    }
}