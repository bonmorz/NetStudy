package com.example.netstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView responseText;
    EditText cityText;
    String city;
    private JSONObject resultJSON;
    private JSONObject dataJSON;
    private JSONObject todayJSON;
    private JSONArray forecastJSONArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest = (Button) findViewById(R.id.button);
        responseText = (TextView) findViewById(R.id.response_text);
        cityText = (EditText) findViewById(R.id.editTextTextPersonName);
        city =cityText.getText().toString();
        sendRequest.setOnClickListener(this);

        //webview.getSettings().setDefaultTextEncodingName("GBK") ;
        //webview.loadUrl("http://wthrcdn.etouch.cn/weather_mini?city=南京");
    }
    @Override
    public void onClick(View v){
        if(v.getId()==R.id.button){

            //String city="南京";
            //city = cityText.getText().toString();
            sendRequestWithHttpURLConnection(city);
        }
    }
    private void sendRequestWithHttpURLConnection(String cityName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try{
                    URL url = new URL("http://wthrcdn.etouch.cn/weather_mini?city="+cityName);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        response.append(line);
                    }
                    //
                    showResponse(response.toString());
                } catch (Exception e){
                    e.printStackTrace();
                }finally{
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    resultJSON = new JSONObject(response);
                    dataJSON = resultJSON.getJSONObject("data");
                    forecastJSONArray = dataJSON.getJSONArray("forecast");
                    todayJSON = (JSONObject) forecastJSONArray.get(1);

                    String str = dataJSON.getString("ganmao");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    String str = todayJSON.getString("date");
                    responseText.setText(str +"\n");
                    str = todayJSON.getString("type");
                    responseText.append(str+"\n");
                    str = todayJSON.getString("high");
                    responseText.append(str+"\n");
                    str = todayJSON.getString("low");
                    responseText.append(str+"\n");
                    str = todayJSON.getString("fengli");
                    responseText.append(str+"\n");
                    str = todayJSON.getString("fengxiang");
                    responseText.append(str+"\n");

                    str = dataJSON.getString("ganmao");
                    responseText.append(str+"\n");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //responseText.setText(response);
            }
        });
    }
}