package com.example.test1;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

interface HelperReturnListener {
    void onHelperReturn(Map<String,String> map);
}

public class RequestHelper {

    RequestHelper(HelperReturnListener activity){
        this.activity=activity;
    }

    HelperReturnListener activity;

    /***
     *  只用post方法
     *  会直接创建一个线程并回调Activity中某个方法;
     * @param target_url 目标服务器地址
     * @param parameter map类型请求参数，将被做成JSONObject
     * @param target ArrayList类型,你需要获取的，服务器将会返回的参数的名称列表;
     * @param extra 附加信息必须string...
     */
    private void request(final String target_url, final Map parameter, final ArrayList<String> target,@Nullable final String extra){
        new Thread(){
            HttpURLConnection connection;
            HashMap<String,String> map=new HashMap<String, String>();

            @Override
            public void run() {
                try {
                    URL url = new URL(target_url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.addRequestProperty("Content-Type", "application/json");
                    connection.addRequestProperty("Accept","application/json");
                    connection.connect();
                    DataOutputStream outPut = new DataOutputStream(connection.getOutputStream());
                    JSONObject parameters = new JSONObject(parameter);
                    String json = java.net.URLEncoder.encode(parameters.toString(), "utf-8");
                    outPut.writeBytes(json);
                    outPut.flush();
                    outPut.close();
                    if (connection.getResponseCode() == 200) {
                        BufferedReader bf1 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line = null;
                        StringBuilder sb1 = new StringBuilder();
                        while ((line = bf1.readLine()) != null) {
                            sb1.append(line);
                        }
                        JSONObject jsonObject=new JSONObject(sb1.toString());
                        map=analyseJson(jsonObject,target);
                        map.put("ERROR","NONE");
                    } else
                        map.put("ERROR",String.valueOf(connection.getResponseCode()));
                } catch (MalformedURLException e) {
                    map.put("ERROR","URLError");
                } catch(SocketTimeoutException e) {
                    map.put("ERROR", "TimeOut");
                }catch (IOException e){
                    map.put("ERROR","IO");
                } catch (JSONException e) {
                    map.put("ERROR","JSONError");
                }finally {
                    connection.disconnect();
                }
                if(extra!=null)
                    map.put("extra",extra);
                activity.onHelperReturn(map);
            }
        }.start();
    }

    private HashMap<String,String> analyseJson(JSONObject jsonObject, ArrayList<String> parameters) {
        HashMap<String,String> map=new HashMap<>();
        try {
            for (String para : parameters) {
                String result = jsonObject.getString(para);
                map.put(para,result);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return map;
    }


    public void Recharge(String CarId,String UserName,String Money){
        String url="http://192.168.1.104:8088/transportservice/action/SetCarAccountRecharge.do";
        HashMap<String,String> parameters=new HashMap<String, String>();
        parameters.put("CarId",CarId);
        parameters.put("Money",Money);
        parameters.put("UserName",UserName);
        ArrayList<String> arrayList=new ArrayList<String>();
        arrayList.add("RESULT");
        arrayList.add("ERRMESSAGE");
        request(url,parameters,arrayList,null);
    }

    public void getBalance(String CarId,String UserName){
        Map<String,String> parameters=new HashMap<>();
        parameters.put("CarId",CarId);
        parameters.put("UserName",UserName);
        String url="http://192.168.1.104:8088/transportservice/action/GetCarAccountBalance.do";
        ArrayList<String> arrayList=new ArrayList<String>();
        arrayList.add("RESULT");
        arrayList.add("ERRMESSAGE");
        arrayList.add("Balance");
        request(url,parameters,arrayList,null);
    }

}
