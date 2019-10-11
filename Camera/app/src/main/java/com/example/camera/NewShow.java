package com.example.camera;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewShow extends AppCompatActivity {

    private  TextView t;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        t = findViewById(R.id.te);
        Bundle bundle=getIntent().getExtras();
        String te=bundle.getString("name");
        t.setText(te);
    }
}
