package com.example.opencv.Vedio;

import android.Manifest;
import android.content.pm.PackageManager;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.opencv.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Date;

public class VedioActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "MainActivity";
    // 程序中的两个按钮
    ImageButton record, stop;
    // 系统的视频文件
    Camera camera;
    File videoFile;
    MediaRecorder mRecorder;
    // 显示视频预览的SurfaceView
    SurfaceView sView;
    // 记录是否正在进行录制
    private boolean isRecording = false;
    private int MY_PERMISSIONS_REQUEST_Vedio=2;
    private int MY_PERMISSIONS_WRITE=5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio);
        // 获取程序界面中的两个按钮
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_Vedio);
        }
        record = (ImageButton) findViewById(R.id.record);
        stop = (ImageButton) findViewById(R.id.stop);
        // 让stop按钮不可用
        stop.setEnabled(false);
        // 为两个按钮的单击事件绑定监听器
        record.setOnClickListener(this);
        stop.setOnClickListener(this);
        // 获取程序界面中的SurfaceView
        sView = (SurfaceView) this.findViewById(R.id.sView);
        // 设置分辨率
        sView.getHolder().setFixedSize(1920, 1080);   // 1080P
        // 设置该组件让屏幕不会自动关闭
        sView.getHolder().setKeepScreenOn(true);
    }

    @Override
    public void onClick(View v) {
        // 单击录制按钮
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE);
        }
        if (v.getId() == R.id.record) {
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
            String savepath=sdDir + "/DCIM/Camera/";
            File f=new File(savepath);
            if(!f.exists()){
                f.mkdirs();
            }
            String filePath = sdDir + "/DCIM/Camera/" + filename + ".mp4";
            try {
                // 创建保存录制视频的视频文件
                videoFile = new File(filePath);
                // 创建MediaPlayer对象
                mRecorder = new MediaRecorder();
                camera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                if(camera!=null)
                {
                    camera.setDisplayOrientation(90);//摄像图旋转90度
                    camera.unlock();
                    mRecorder.setCamera(camera);
                }
                mRecorder.reset();
                // 设置从麦克风采集声音
                mRecorder.setAudioSource(MediaRecorder
                        .AudioSource.MIC);
                // 设置从摄像头采集图像
                mRecorder.setVideoSource(MediaRecorder
                        .VideoSource.CAMERA);
                // 设置视频文件的输出格式
                // 必须在设置声音编码格式、图像编码格式之前设置
                mRecorder.setOutputFormat(MediaRecorder
                        .OutputFormat.MPEG_4);
                // 设置声音编码的格式
                mRecorder.setAudioEncoder(MediaRecorder
                        .AudioEncoder.DEFAULT);
                // 设置图像编码的格式
                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
     //           mRecorder.setVideoSize(1920, 1080);  // 1080P
                // 每秒16帧
                mRecorder.setVideoFrameRate(16);
                mRecorder.setOutputFile(videoFile.getAbsolutePath());
                // 指定使用SurfaceView来预览视频
                mRecorder.setPreviewDisplay(sView.getHolder().getSurface());  // ①
                mRecorder.prepare();
                // 开始录制
                mRecorder.start();
                Log.d(TAG, "---recording---");
                // 让record按钮不可用
                record.setEnabled(false);
                // 让stop按钮可用
                stop.setEnabled(true);
                isRecording = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (v.getId() == R.id.stop) {   // 单击停止按钮
            // 如果正在进行录制
            if (isRecording) {
                // 停止录制
                mRecorder.stop();
                // 释放资源
                mRecorder.release();
                mRecorder = null;
                // 让record按钮可用
                record.setEnabled(true);
                // 让stop按钮不可用
                stop.setEnabled(false);
            }
        }
    }
}