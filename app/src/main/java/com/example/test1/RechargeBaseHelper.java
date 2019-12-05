package com.example.test1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RechargeBaseHelper extends SQLiteOpenHelper {
    RechargeBaseHelper(Context context,String dbName,int version){
        super(context,dbName,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists record(UserName varchar(30),CarId int,Amount int,time datetime)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
