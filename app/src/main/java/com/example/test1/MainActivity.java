package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {
DrawerLayout drawerLayout;
TextView balance;
Toolbar toolbar;
ListView lv1;
my_account account;
traffic_light traffic;
FragmentManager fm;
static myHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fragment init
        setContentView(R.layout.activity_main);


        fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        account=new my_account();
        ft.add(R.id.content,account,"account").commit();

        handler=new myHandler(this);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer1);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        lv1=(ListView)findViewById(R.id.list1);

        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        List<String> list=new ArrayList<>();
        list.add("我的账户");
        list.add("红绿灯管理");
        lv1.setAdapter(new ArrayAdapter<String>(this,R.layout.list_item,list));

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction tf=fm.beginTransaction();
                if(position==0)
                    tf.replace(R.id.content,account,"account");
                else if(traffic==null){
                    traffic=new traffic_light();
                    tf.replace(R.id.content,traffic,"traffic");}
                else
                    tf.replace(R.id.content,traffic,"traffic");
                tf.commitAllowingStateLoss();
                drawerLayout.closeDrawers();
            }
        });
    }
    class myHandler extends Handler{
        WeakReference<FragmentActivity> mainActivity;
        myHandler(FragmentActivity mainActivity){
            super();
            this.mainActivity=new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HashMap<String,String> map=(HashMap<String,String>)msg.obj;
            if(!map.get("ERROR").equals("NONE")) {
                Toast.makeText(mainActivity.get(), "网络请求失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if(map.containsKey("Balance")) {
                if (!mainActivity.get().getSupportFragmentManager().findFragmentByTag("account").isRemoving()) {
                    balance = (TextView) mainActivity.get().findViewById(R.id.balance);
                    balance.setText("Balance");
                }
                return;
            }
            Toast.makeText(mainActivity.get(),"充值成功",Toast.LENGTH_SHORT).show();
            RechargeBaseHelper rechargeBaseHelper=new RechargeBaseHelper(mainActivity.get(),"traffic_db",1);
            SQLiteDatabase db=rechargeBaseHelper.getWritableDatabase();
            Date date=new Date(System.currentTimeMillis());
            SimpleDateFormat sd=new SimpleDateFormat();
            String time=sd.format(date);
            db.execSQL("insert  into recharge values(?,?,?,?)",new String[]{map.get("UserName"),map.get("CarId"),map.get("extra"),time});
            db.close();
            msg.recycle();//回收
        }
    }
}

