package com.example.caculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button bt_0,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6,bt_7,bt_8,bt_9,bt_multiply,bt_divide,bt_add,bt_sub,bt_dot,bt_clear,bt_equ,bt_AC,bt_left,bt_right;
    EditText et_show;  //获取文本
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_0 = (Button)findViewById(R.id.button_number0);   //获取button
        bt_1 = (Button)findViewById(R.id.button_number1);
        bt_2 = (Button)findViewById(R.id.button_number2);
        bt_3 = (Button)findViewById(R.id.button_number3);
        bt_4 = (Button)findViewById(R.id.button_number4);
        bt_5 = (Button)findViewById(R.id.button_number5);
        bt_6 = (Button)findViewById(R.id.button_number6);
        bt_7 = (Button)findViewById(R.id.button_number7);
        bt_8 = (Button)findViewById(R.id.button_number8);
        bt_9 = (Button)findViewById(R.id.button_number9);
        bt_multiply = (Button)findViewById(R.id.button_multiple);
        bt_divide = (Button)findViewById(R.id.button_divide);
        bt_add = (Button)findViewById(R.id.button_add);
        bt_sub = (Button)findViewById(R.id.button_sub);
        bt_dot = (Button)findViewById(R.id.button_dot);
        bt_clear = (Button)findViewById(R.id.button_clear);
        bt_equ = (Button)findViewById(R.id.button_equ);
        bt_AC = (Button)findViewById(R.id.button_AC);
        bt_left=(Button)findViewById(R.id.button_left);
        bt_right=(Button)findViewById(R.id.button_right);

        et_show=(EditText)findViewById(R.id.et_show);///获取EditText控件

        bt_0.setOnClickListener(this);  //设置事件监听器
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        bt_4.setOnClickListener(this);
        bt_5.setOnClickListener(this);
        bt_6.setOnClickListener(this);
        bt_7.setOnClickListener(this);
        bt_8.setOnClickListener(this);
        bt_9.setOnClickListener(this);
        bt_multiply.setOnClickListener(this);
        bt_divide.setOnClickListener(this);
        bt_sub.setOnClickListener(this);
        bt_dot.setOnClickListener(this);
        bt_add.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_equ.setOnClickListener(this);
        bt_AC.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
    }

    public void onClick(View v){
        String str=et_show.getText().toString();
        switch(v.getId()){
            case R.id.button_number0:
            case R.id.button_number1:
            case R.id.button_number2:
            case R.id.button_number3:
            case R.id.button_number4:
            case R.id.button_number5:
            case R.id.button_number6:
            case R.id.button_number7:
            case R.id.button_number8:
            case R.id.button_number9:
            case R.id.button_dot:
            case R.id.button_add:
            case R.id.button_divide:
            case R.id.button_multiple:
            case R.id.button_sub:
            case R.id.button_right:
            case R.id.button_left:
                et_show.setText(str+((Button)v).getText());
                break;
            case R.id.button_AC:
                et_show.setText("");
                break;
            case R.id.button_clear:
                if(str != null && !str.equals("")){
                    et_show.setText(str.substring(0,str.length()-1));
                }
                else
                    et_show.setText("");
                break;
            case R.id.button_equ:
                et_show.setText(str+((Button)v).getText());
                Result();
                break;
        }
    }
    private void Result()
    {
        String str0=et_show.getText().toString();
        char []exp=str0.toCharArray();
        if(str0.contains("."))
        {
            Seqstack s0=new Seqstack();
            et_show.setText(String.valueOf(s0.calculate1(exp)));
        }
        else
        {
            Seqstack s1=new Seqstack();
            et_show.setText(String.valueOf(s1.calculate(exp)));
        }
    }
}
