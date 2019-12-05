package com.example.test1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class my_account extends Fragment  {
EditText recharge;
TextView balance;
Button ok,cancel;
Spinner car_number;
static HelperReturnListener myListener=new HelperReturnListener() {
    @Override
    public void onHelperReturn(Map<String, String> map) {
        Message msg=Message.obtain();
        msg.obj=map;
        MainActivity.handler.sendMessage(msg);
    }
};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_my_account,null);

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recharge=(EditText)getActivity().findViewById(R.id.recharge);
        balance=(TextView)getActivity().findViewById(R.id.balance);
        ok=(Button)getActivity().findViewById(R.id.btn_ok);
        cancel=(Button)getActivity().findViewById(R.id.btn_cancel);
        car_number=(Spinner)getActivity().findViewById(R.id.car_number);

        OnClickListener listener1=new OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestHelper helper=new RequestHelper(myListener);
                switch (v.getId()){
                    case R.id.btn_ok:
                        String money=recharge.getText().toString();
                        if (money.contains(".") || money.isEmpty()) {
                            Toast.makeText(getActivity(), "请输入正确的数值", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        HashMap<String,String> parameter=new HashMap<>();
                        String CarId=String.valueOf(car_number.getSelectedItemPosition()+1);
                        String username="User4";
                        helper.Recharge(CarId,username,money);
                        helper.getBalance(username,CarId);
                        break;
                    case R.id.btn_cancel:
                        return;
                }
            }
        };
        ok.setOnClickListener(listener1);
        cancel.setOnClickListener(listener1);




    }
}
