package edu.neu.madcourse.numad21sugroupattentionstick;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.neu.madcourse.numad21sugroupattentionstick.fcm.FCMActivity;
import edu.neu.madcourse.numad21sugroupattentionstick.notification.SendNotificationActivity;
import edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase.RealtimeDatabaseActivity;
import edu.neu.madcourse.numad21sugroupattentionstick.showpermissions.ShowPermActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        // Turn on strict mode only if we're debugging
//        if (BuildConfig.DEBUG) {
//            // The code below is from Android docs on StrictMode.
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    //  .penaltyDeath()  uncomment this to crash if policy is violated instead of just logging
//                    .build());
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if(extras != null){
            extractDataFromNotification(extras);
        }
    }

    // Details are in 'fcm' package
    public void openFCMActivity(View view) {
        startActivity(new Intent(MainActivity.this, FCMActivity.class));
    }

    // Details are in 'realtime' package
    public void openDBActivity(View view) {
        startActivity(new Intent(MainActivity.this, RealtimeDatabaseActivity.class));
    }

    // Details are in 'notification' package
    public void openSendNotificationActivity(View view){
        startActivity(new Intent(MainActivity.this, SendNotificationActivity.class));
    }

    // Details are in 'showpermissions' package
    public void openPermissionsActivity(View view){
        startActivity(new Intent(MainActivity.this, ShowPermActivity.class));
    }


    /**
     * This is the method called to extract the payload sent to the device from the server.
     * Notice that the data is present in the extras. Note that this will be executed Only
     * when the message is received when app is in background
     * OnMessageReceived the funtion in the MEssagingservice class is the function called when
     * the message is received and the app is in foreground
     */
    private void extractDataFromNotification(Bundle extras){

        if(extras != null){
            String dataTitle = extras.getString("title","Nothing");
            String dataContent = extras.getString("content","Nothing");
            postToastMessage("Received : "+ dataTitle + " "+ dataContent);
        }

    }

    private void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

}


//private static final String SERVER_KEY = "AAAAgnRhtk4:APA91bE0rNmwPotfBuAQ_hT_Jrs75GDmKw9ks119_tv4PfqzCyK7OV_ThNDDEdHOehFgb7EexauCkfzxEM2iFK_H-gwTXNO8yKUqA_edxa0dl9nSTn9DFibO4ae75XSG315KSXPzdCPt";