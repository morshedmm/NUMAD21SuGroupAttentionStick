package edu.neu.madcourse.numad21sugroupattentionstick.fcm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import edu.neu.madcourse.numad21sugroupattentionstick.utils.Utils;
import edu.neu.madcourse.numad21sugroupattentionstick.R;


public class FCMActivity extends AppCompatActivity {


    private static final String TAG = "FCMActivity";
    // Please add the server key from your firebase console in the follwoing format "key=<serverKey>"
    // Suggest to write in .json file
    // How to generate your own key: https://console.firebase.google.com/project/<your-project-name>/settings/cloudmessaging
    private static String SERVER_KEY = "key=AAAAgnRhtk4:APA91bE0rNmwPotfBuAQ_hT_Jrs75GDmKw9ks119_tv4PfqzCyK7OV_ThNDDEdHOehFgb7EexauCkfzxEM2iFK_H-gwTXNO8yKUqA_edxa0dl9nSTn9DFibO4ae75XSG315KSXPzdCPt";


    // This is the client registration token
    private static String CLIENT_REGISTRATION_TOKEN;

    // Test your msg: https://console.firebase.google.com/project/<your-project-name>/notification/compose

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);

        // CHECK NULL when you want to use
      //  SERVER_KEY = "key=" + Utils.getProperties(getApplicationContext()).getProperty("SERVER_KEY");

        // Generate the token for the first time, then no need to do later
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(FCMActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                } else {
                    if (CLIENT_REGISTRATION_TOKEN == null) {
                        CLIENT_REGISTRATION_TOKEN = task.getResult();
                    }
                    Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
                    Toast.makeText(FCMActivity.this, "CLIENT_REGISTRATION_TOKEN Existed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Load button
    public void loadToken(View view) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(FCMActivity.this, "Something is wrong, please check your Internet connection!", Toast.LENGTH_SHORT).show();
                } else {
                    if (CLIENT_REGISTRATION_TOKEN.length() < 1) {
                        CLIENT_REGISTRATION_TOKEN = task.getResult();
                    }
                    Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
                    Toast.makeText(FCMActivity.this, "CLIENT_TOKEN IS: " + CLIENT_REGISTRATION_TOKEN, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // subscribe button
    // Detection of whether this topic is subscribed is important!
    //
    public void subscribeToNews(View view) {

        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                            Toast.makeText(FCMActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FCMActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    /**
     * Button Handler; creates a new thread that sends off a message to all subscribed devices
     *
     * @param view
     */
    public void sendMessageToNews(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToNews();
            }
        }).start();
    }

    private void sendMessageToNews() {

        // Prepare data
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {

            jNotification.put("title", "This is a Firebase Cloud Messaging topic \"news\" message! from 'SEND MESSAGE TO NEWS TOPIC BUTTON'");
            jNotification.put("body", "News Body from 'SEND MESSAGE TO NEWS TOPIC BUTTON'");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            jPayload.put("to", "/topics/news");
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String resp = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
        Utils.postToastMessage("Status from Server: " + resp, getApplicationContext());

    }


    /**
     * Button Handler; creates a new thread that sends off a message to the target(this) device
     *
     * @param view
     */
    public void sendMessageToDevice(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(CLIENT_REGISTRATION_TOKEN);
            }
        }).start();
    }

    /**
     * Pushes a notification to a given device-- in particular, this device,
     * because that's what the instanceID token is defined to be.
     */
    private void sendMessageToDevice(String targetToken) {

        // Prepare data
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Message Title from 'SEND MESSAGE TO CLIENT BUTTON'");
            jNotification.put("body", "Message body from 'SEND MESSAGE TO CLIENT BUTTON'");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            /*
            // We can add more details into the notification if we want.
            // We happen to be ignoring them for this demo.
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            */
            jdata.put("title", "data title from 'SEND MESSAGE TO CLIENT BUTTON'");
            jdata.put("content", "data content from 'SEND MESSAGE TO CLIENT BUTTON'");

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            /*
            // If sending to multiple clients (must be more than 1 and less than 1000)
            JSONArray ja = new JSONArray();
            ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            ja.put(FirebaseInstanceId.getInstance().getToken());
            jPayload.put("registration_ids", ja);
            */

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String resp = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
        Utils.postToastMessage("Status from Server: " + resp, getApplicationContext());

    }

    // This method sends a sticker to another user of the app
    public void sendSticker(View view) {
    }
}