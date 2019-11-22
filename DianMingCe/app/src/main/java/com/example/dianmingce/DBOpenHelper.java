package com.example.dianmingce;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    public DBOpenHelper(Context context) {
        super(context, "DianMing.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+Student.TABLE+"("+Student.KEY_SNO+" varchar(12) primary key," +
                Student.KEY_SNAME+" varchar(20) not null,"+Student.KEY_SCLASS+" varchar(20) not null," +
                Student.KEY_SBEIZHU+" text,"+Student.KEY_STOUXIANG+" blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Student.TABLE); //表存在就删去
        onCreate(db); //再次创建
    }
}
