package com.project.nilawan.biketracker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Control extends Activity {

    private GoogleApiClient client;

    private Button bt_where;
    private Button bt_alarm;
    private Button bt_logout;
    private Button bt_bike;
    private Button bt_back;
    private Double motion;
    private Double mo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);

        Receivemotion();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        bt_where = (Button) findViewById(R.id.button_where);
        bt_alarm = (Button) findViewById(R.id.button_alarm);
        bt_logout = (Button) findViewById(R.id.Blogout);
        bt_bike = (Button) findViewById(R.id.statusbike);
        bt_back = (Button) findViewById(R.id.Block);


        bt_alarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent al = new Intent(Control.this,AlarmActivity.class);
                finish();
                startActivity(al);

            }
        });

        bt_where.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent inte = new Intent(Control.this, MapsActivity.class);
                finish();
                startActivity(inte);
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent inte = new Intent(Control.this, NotificationSet.class);
                finish();
                startActivity(inte);
            }
        });


        bt_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent inte = new Intent(Control.this, Login.class);
                startActivity(inte);

            }
        });


    }

    private void Receivemotion() {
        {


            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    OkHttpClient okHttpClient = new OkHttpClient();

                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url("http://tr.ddnsthailand.com/motion.php").build();

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
                            map.put("motion", c.getString("motion"));
                            MyArrList.add(map);

                        }
                        String smotion = MyArrList.get(0).get("motion");

                        double motion = Double.parseDouble(smotion);

                        mo = motion;


                        condition();


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

                private void condition() {


                    /**station1*/
                    if (mo == 1)  {
                        Button alarmOn = (Button)findViewById(R.id.statusbike);
                        alarmOn.setBackgroundResource(R.drawable.checkbike);
                        showNotification();
                    }

                    else if (mo == 0)  {
                        Button alarmOn = (Button)findViewById(R.id.statusbike);
                        alarmOn.setBackgroundResource(R.drawable.safe);
                    }


                    Loop();

                }


            }.execute();

        }

    }

    private void showNotification() {

        NotificationCompat.Builder notification = new NotificationCompat.Builder(Control.this);

        notification.setSmallIcon(R.drawable.red);
        notification.setTicker("New notification!!!");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("This is a new notification!!");
        notification.setContentText("Please check your bike!");
        notification.setAutoCancel(true);


        Uri sound = RingtoneManager.getDefaultUri(android.app.Notification.DEFAULT_SOUND);
        notification.setSound(sound);

        Bitmap picture = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        notification.setLargeIcon(picture);

        PendingIntent myPendingIntent;
        Intent myIntent = new Intent();
        Context myContext = getApplicationContext();

        myIntent.setClass(myContext, Control.class);
        myIntent.putExtra("ID",1);

        myPendingIntent = PendingIntent.getActivities(myContext, 0, new Intent[]{myIntent}, 0);
        notification.setContentIntent(myPendingIntent);

        android.app.Notification n = notification.build();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1,n);


    }

    private void Loop() {

        android.os.Handler h = new android.os.Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                Receivemotion();
            }
        }, 4000);

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


