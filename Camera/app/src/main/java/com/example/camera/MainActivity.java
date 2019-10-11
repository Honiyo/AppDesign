package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button bt_camera;
    private Button bt_save;
    private Button bt_scan;
    public Bitmap bitmap;
    private EditText editText,root;
    private ImageView imageView;
    private TextView te;
    private NewShow no=new NewShow();
    private static int REQUEST_CAMERA = 0;
    private static int REQUEST_SCAN = 1;
    private static int REQUEST_SAVE=2;
    private static final int WRITE_STORAGE_REQUEST_CODE = 2;
    private static final int READ_STORAGE_REQUESR_CODE = 3;
    private  final int REQUEST_EXTERNAL_STORAGE = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_camera = findViewById(R.id.shot);
        bt_save = findViewById(R.id.save);
        bt_scan=findViewById(R.id.scan);
        editText = findViewById(R.id.filename);
        root=findViewById(R.id.root);
        imageView = findViewById(R.id.preview);
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA);}//这一块红色的是开启手机里的相机权限，安卓6.0以后的系统需要，否则会报错
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_STORAGE_REQUEST_CODE);}//这一块红色的是开启手机里的相机权限，安卓6.0以后的系统需要，否则会报错
                save(bitmap, editText.getText().toString());
            }
        });
        bt_scan.setOnClickListener(new View.OnClickListener(){
           public void onClick(View view)
           {
               if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                       != PackageManager.PERMISSION_GRANTED) {
                   //申请WRITE_EXTERNAL_STORAGE权限
                   ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                           REQUEST_CAMERA);}//这一块红色的是开启手机里的相机权限，安卓6.0以后的系统需要，否则会报错
               Intent intent = new Intent(MainActivity.this, CaptureActivity.class);//黄色是第三方类库里面的类
               startActivityForResult(intent,REQUEST_SCAN);
           }

        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        Bundle bundle = data.getExtras();
        if (requestCode == REQUEST_CAMERA) {
            bitmap = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(bitmap);
        }
        if (requestCode == REQUEST_SCAN)
        {
            if (bundle == null)
            {
                return;
            }
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS)
            {
                String name = bundle.getString(CodeUtils.RESULT_STRING);
                Toast.makeText(this, "解析结果" + name, Toast.LENGTH_LONG).show();
//                Intent intent0=new Intent();
//                intent0.setClass(MainActivity.this,NewShow.class);
//                intent0.putExtra("name",name);
//                startActivity(intent0);
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED)
            {
                Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void save(Bitmap bitmap, String fileName) {
//        try {
//            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        FileOutputStream fos=null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            String imagepath=Environment.getExternalStorageDirectory().getPath();
            try{
                File file=new File(imagepath,fileName+".jpg");
                System.out.println(file.getPath());
                fos=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                Toast.makeText(this,"成功",Toast.LENGTH_SHORT).show();
            }catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.parse("file://"+imagepath+fileName);
            intent.setData(uri);
            MainActivity.this.sendBroadcast(intent);
        }
    }

//    private void updateSystemGallery() {
//        //把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(this.getContentResolver(),
//                    mImageFile.getAbsolutePath(),editText.getText().toString(), null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 最后通知图库更新
//        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mImagePath)));
//    }
}

