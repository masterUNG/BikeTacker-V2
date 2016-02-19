package com.project.nilawan.biketracker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nilawan on 18/2/2559.
 */
public class AlarmActivity extends Activity {

    private GoogleApiClient client;

    private TextView text1, text2;
    private Button statusbike;
    private Button statusalarm;
    private Button Bback2;
    private Button bt_on;
    private Button bt_off;
    private String alarm ;
    private String id ;
    private Double al;
    private static final String TAG = "sendAlarmOff.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Receivealarm();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Bback2 = (Button) findViewById(R.id.Bback2);
        Bback2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmActivity.this, AfterClickActivity.class);  //Notification
                startActivity(intent);

                finish();
            }
        });

        bt_on = (Button) findViewById(R.id.Balarmon);

        bt_on.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sendAlarmOn();

            }
        });

        bt_off = (Button) findViewById(R.id.Balarmoff);

        bt_off.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sendAlarmOff();
            }
        });


    }

    private void sendAlarmOff() {

        id="1";
        alarm = "0";
        new UploadTask1().execute();
    }

    private void sendAlarmOn() {

        id="1";
        alarm = "1";
        new UploadTask2().execute();
    }

    private void Receivealarm() {
        {


            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    OkHttpClient okHttpClient = new OkHttpClient();

                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url("http://tr.ddnsthailand.com/status.php").build();

                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        if (response.isSuccessful()) {
                            return response.body().string();
                        } else {
                            return "Not Success - code : " + response.code();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "Error - " + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String string) {
                    super.onPostExecute(string);


                    try {

                        JSONArray data = new JSONArray(string);
                        final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
                        HashMap<String, String> map;

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);
                            map = new HashMap<>();
                            map = new HashMap<String, String>();
                            map.put("id", c.getString("id"));
                            map.put("alarm", c.getString("alarm"));
                            MyArrList.add(map);

                        }
                        String salarm = MyArrList.get(0).get("alarm");

                        double alarm = Double.parseDouble(salarm);

                        al = alarm;


                        condition();


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

                private void condition() {

                    if (al == 1)  {
                        TextView text1 = (TextView) findViewById(R.id.textView);
                        text1.setText("ON");
                        Button Status = (Button)findViewById(R.id.button_status);
                        Status.setBackgroundResource(R.drawable.alred);
                    }

                    else if (al == 0)  {
                        TextView text1 = (TextView) findViewById(R.id.textView);
                        text1.setText("OFF");
                        Button Status = (Button)findViewById(R.id.button_status);
                        Status.setBackgroundResource(R.drawable.algreen);
                    }


                    Loop();

                }


            }.execute();

        }
    }

    private void Loop() {

        android.os.Handler h = new android.os.Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                Receivealarm();
            }
        }, 4000);

    }


    private class UploadTask1 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://tr.ddnsthailand.com/uploadalarm.php");

            Log.v(TAG, "doIn;:");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("id", "1"));
                nameValuePairs.add(new BasicNameValuePair("alarm", "0"));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                Log.v(TAG,"Post::::" + response);
                if (resEntity != null) {
                    String responseStr = EntityUtils.toString(resEntity).trim();
                    Log.v(TAG,"respSTR:" + responseStr);
                    Log.v(TAG,"456");

                    return "1";
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.v(TAG,"Post");
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG,"Post");
            }

            return null;
        }

    }

    private class UploadTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://tr.ddnsthailand.com/uploadalarm.php");

            Log.v(TAG, "doIn;:");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("id", "1"));
                nameValuePairs.add(new BasicNameValuePair("alarm", "1"));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                Log.v(TAG,"Post::::" + response);
                if (resEntity != null) {
                    String responseStr = EntityUtils.toString(resEntity).trim();
                    Log.v(TAG,"respSTR:" + responseStr);
                    Log.v(TAG,"456");

                    return "1";
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.v(TAG,"Post");
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG,"Post");
            }

            return null;
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Showlocation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.project.nilawan.biketracker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Showlocation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.project.nilawan.biketracker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
