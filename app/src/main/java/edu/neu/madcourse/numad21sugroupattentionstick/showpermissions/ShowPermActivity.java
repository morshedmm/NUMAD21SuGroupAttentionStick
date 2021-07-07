package edu.neu.madcourse.numad21sugroupattentionstick.showpermissions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import edu.neu.madcourse.numad21sugroupattentionstick.R;

public class ShowPermActivity extends AppCompatActivity {

    private static String TAG = "ShowPermActivity";
    TextView responseTV;

    // Used to indicate which permission is being responded to.
    private static final int DEV_ID = 1;
    private static final int PHOTO_PERMISSION = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pref);

        responseTV = (TextView) findViewById(R.id.permResponseTextView);

        // Use this for example
        String statePermission = android.Manifest.permission.READ_PHONE_STATE;
        CheckPermission(statePermission);
    }

    // Button Click
    private void showDeviceId(String info) {
        // We need the READ_PHONE_STATE permission and READ_PRIVILEGED_PHONE_STATE in order to get the IMEI of the device.
        // If we don't already have the READ_PHONE_STATE permission before we run this code,
        // our application will crash.

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // Check permissions defined in the 'AndroidManifest.xml'
        @SuppressLint("MissingPermission") String devId = tm.getDeviceId();
        responseTV.setText(info + devId);
    }

    // Check the permission
    private void CheckPermission(String statePermission) {
        Log.v(TAG, "About to check permissions");
        int permissionCheck = ContextCompat.checkSelfPermission(this, statePermission);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Already have permission-- groovy!");
            Toast.makeText(getApplicationContext(), "Already have READ_PHONE_STATE permission!", Toast.LENGTH_SHORT).show();
            showDeviceId("I have the permission I need; IMEI =  ");
        } else {
            Log.e(TAG, "Don't have permissions yet, gotta ask the user");

            // This updates the TextView with our info
            responseTV.setText("I don't have permission; you'll see a dialog");

            // This launches the permissions dialog
            ActivityCompat.requestPermissions(this,
                    new String[]{statePermission}, DEV_ID);
        }
    }

    // Callback for the result from requesting permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d(TAG, "Request Permission returned");
        switch (requestCode) {
            case DEV_ID: {
                Log.i(TAG, "Do I have DEV_ID permissions? ");

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(TAG, "The user gave access-- yay!!");
                    // permission was granted, yay! Do the thing
                    showDeviceId("The user granted access. IMEI: ");

                } else {
                    Log.e(TAG, "User denied permission. ");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // showDeviceId("The user denied permission. Error!! ");

                    responseTV.setText("The user denied permission. Error!! ");
                }
            }
            case PHOTO_PERMISSION: {
                Log.i(TAG, "Some code that isn't used. ");
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
