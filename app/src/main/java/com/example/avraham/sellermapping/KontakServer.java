package com.example.avraham.sellermapping;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Avraham on 02/05/2017.
 */

public class KontakServer extends AppCompatActivity {

    TextView tvHasil;
    String isi;
    JSONObject jsonObj;
    String lat ;
    String lon ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        setContentView(R.layout.activity_main);
        tvHasil= (TextView) findViewById(R.id.textView);*/
    }

    private class AmbilData extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strUrl) {

            //Log.v("yw","mulai ambil data");
            String hasil="";
            InputStream inStream=null;
            int len=500;  //ini adalah besaran untuk buffernya

            try{
                URL url = new URL(strUrl[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //timeout
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setRequestMethod("GET");
                conn.connect();

                int  response = conn.getResponseCode();
                inStream = conn.getInputStream();

                //konversi stream ke string
                Reader r = null;
                r = new InputStreamReader(inStream,"UTF-8");
                char[] buffer = new char[len];
                r.read(buffer);
                hasil= new String(buffer);

            }catch(MalformedURLException e){
                e.printStackTrace();

            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(inStream!= null){
                    try{
                        inStream.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            return hasil;
        }

        protected void onPostExecute(String result){
            tvHasil.setText(result);
            isi= result;
            try {
                jsonObj= new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                lat = jsonObj.getString("latitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                lon = jsonObj.getString("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void onClick(String txt){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo= connMgr.getActiveNetworkInfo();

        if(netinfo!=null && netinfo.isConnected()){
            new AmbilData().execute(txt);
        }else{
            Toast t = Toast.makeText(getApplicationContext(),"Tidak ada koneksi !", Toast.LENGTH_LONG);
            t.show();
        }
    }
}

