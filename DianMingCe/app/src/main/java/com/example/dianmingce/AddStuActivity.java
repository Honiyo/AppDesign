package com.example.dianmingce;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class AddStuActivity extends AppCompatActivity {
    DBOpenHelper dbOH;
    SQLiteDatabase sqLD;
    private ImageView ivAddTouXiang;
    private Button btnAdd, btnCancel, btnAddClear, btnAddAgain;
    private EditText etAddSno, etAddSname, etAddSclass;
    private Bitmap addBitmap;
    private boolean isAddTouXiang = false; //判断是否自己添加了头像
    private int REQUEST_CAMERA=1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student);
        dbOH = new DBOpenHelper(this);
        sqLD = dbOH.getReadableDatabase();
        ivAddTouXiang = (ImageView)findViewById(R.id.iv_AddTouXiang);
        btnAdd = (Button)findViewById(R.id.btn_Add);
        btnCancel = (Button)findViewById(R.id.btn_Cancel);
        btnAddClear = (Button)findViewById(R.id.btn_AddClear);
        btnAddAgain = (Button)findViewById(R.id.btn_AddAgain);
        etAddSno = (EditText)findViewById(R.id.et_AddSno);
        etAddSname = (EditText)findViewById(R.id.et_AddSname);
        etAddSclass = (EditText)findViewById(R.id.et_AddSclass);

        ivAddTouXiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*"); //定义打开图片类型
//                intent.setAction(Intent.ACTION_GET_CONTENT); //设置action为A。。。，代表图片内容
//                startActivityForResult(intent,1); //得到图片返回当前页
                if (ContextCompat.checkSelfPermission(AddStuActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(AddStuActivity.this, new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA);}//这一块红色的是开启手机里的相机权限，安卓6.0以后的系统需要，否则会报错
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etAddSno.getText().toString().trim().equals(""))
                        Toast.makeText(getApplicationContext(), "添加失败！学号不允许为空",
                                Toast.LENGTH_SHORT).show();
                    else if (etAddSname.getText().toString().trim().equals(""))
                        Toast.makeText(getApplicationContext(), "添加失败！姓名不允许为空",
                                Toast.LENGTH_SHORT).show();
                    else if (etAddSclass.getText().toString().trim().equals(""))
                        Toast.makeText(getApplicationContext(), "添加失败！班级不允许为空",
                                Toast.LENGTH_SHORT).show();
                    else {
                        ContentValues cv = new ContentValues();
                        cv.put(Student.KEY_SNO, etAddSno.getText().toString());
                        cv.put(Student.KEY_SNAME, etAddSname.getText().toString());
                        cv.put(Student.KEY_SCLASS, etAddSclass.getText().toString());
                        if (isAddTouXiang == true) {
                            ivAddTouXiang.setDrawingCacheEnabled(true); //能够获取ImageView中的图片
                            cv.put(Student.KEY_STOUXIANG, getPicture(addBitmap));
                            ivAddTouXiang.setDrawingCacheEnabled(false);
                        }
                        Cursor c = sqLD.query(Student.TABLE,new String[]{Student.KEY_SNO},Student.KEY_SNO+"=?",
                                new String[]{etAddSno.getText().toString()},null,null,null);
                        if(c.getCount()==0) {
                            sqLD.insert(Student.TABLE, null, cv);
                            Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"已存在学号为："+etAddSno.getText().toString()+"的学生",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    // Toast.makeText(getApplicationContext(),"已存在学号为："+etAddSno.getText().toString()+"的学生",
                    //         Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        /**
         * 取消按钮
         */
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddStuActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 清空按钮
         */
        btnAddClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddSno.setText("");
                etAddSname.setText("");
                etAddSclass.setText("");
             //   ivAddTouXiang.setImageResource(R.mipmap.unnamed);
                etAddSno.requestFocus(); //获取焦点
            }
        });
        /**
         * 继续添加
         */
        btnAddAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddSno.setText("");
                etAddSname.setText("");
                etAddSclass.setText("");
            //    ivAddTouXiang.setImageResource(R.mipmap.unnamed);
                etAddSno.requestFocus(); //获取焦点
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //接受用户通过其他activity返回的数据
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        Bundle bundle = data.getExtras();
        if (requestCode == 1) { //请求为1，进行处理
//            if (resultCode == RESULT_OK) { //成功得到照片返回
//                ContentResolver cr = this.getContentResolver();//通过Activity的content得到ContentR。。
//                Uri uri = data.getData(); //得到返回的data数据
//                try {
//                    //通过ContentR。。得到对应图片的Bitmap
//                    //  addBitmap = MediaStore.Images.Media.getBitmap(cr,uri);
//
//                    ivAddTouXiang.setImageBitmap(addBitmap); //图片放Image。。去
//                    isAddTouXiang = true;
//                } catch (Exception e) {
//                    Log.e("Exception", e.getMessage(), e);
//                }
            bitmap = (Bitmap) bundle.get("data");
            ivAddTouXiang.setImageBitmap(bitmap);
            isAddTouXiang=true;
        }
    }
    private byte[] getPicture(Bitmap bitmap){
        if(bitmap == null)
            return null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
    }
}