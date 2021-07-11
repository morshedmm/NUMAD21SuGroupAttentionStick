package edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import edu.neu.madcourse.numad21sugroupattentionstick.MyItemCard;
import edu.neu.madcourse.numad21sugroupattentionstick.MyRviewAdapter;
import edu.neu.madcourse.numad21sugroupattentionstick.R;
import edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase.models.User;

public class RealtimeDatabaseActivity extends AppCompatActivity {

    private static final String TAG = RealtimeDatabaseActivity.class.getSimpleName();
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";
    private DatabaseReference mDatabase;
    private TextView user1;
    private TextView score_user1;
    private TextView user2;
    private TextView score_user2;
    private RadioButton player;

    // Adding variable to hold my user name
    private User myUser = new User("","","");
    private RecyclerView recyclerView;
    private MyRviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;
    private ArrayList<MyItemCard> itemList = new ArrayList<>();
    private RadioGroup radioGroup;
    //RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());




    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_database);

        //user1 = (TextView) findViewById(R.id.username1);
        //user2 = (TextView) findViewById(R.id.username2);
        //score_user1 = (TextView) findViewById(R.id.score1);
        //score_user2 = (TextView) findViewById(R.id.score2);
        player = (RadioButton) findViewById(R.id.my_stickers);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        //
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.my_stickers:
                        Log.i(TAG, "My Stickers radio button pressed");
                        showReadData(findViewById(android.R.id.content),"");
                        break;
                    case R.id.stickers_i_sent:
                        Log.i(TAG, "Stickers I sent radio button pressed");
                        showReadData(findViewById(android.R.id.content),"sent");
                        break;
                    default:
                        Log.i(TAG, "nothing!");
                        break;
                }
            }
        });


        //

        // Connect with firebase
        //
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Update the score in realtime
        mDatabase.child("users").addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        drawEmotes(dataSnapshot);
                        Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        drawEmotes(dataSnapshot);
                        Log.v(TAG, "onChildChanged: " + dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled:" + databaseError);
                        Toast.makeText(getApplicationContext()
                                , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void setUserName(View view) {

        /*
        String curName = findViewById(R.id.myusername_id).toString();
        User myUser = new User(curName, "0");
        Task t3 = mDatabase.child("users").child(myUser.username).setValue(myUser);
        */

        try {
            EditText givenName = (EditText) findViewById(R.id.myusername_id);
            String curName = givenName.getText().toString();
            Log.i("usernamevalue", curName);
            if(!curName.equals(USER_1) && !curName.equals(USER_2)){
                throw new IllegalArgumentException("Username must be "+ USER_1 + " or "+USER_2);
            }

            //User myUser;
            myUser = new User(curName, "0", "");
            Log.i("Login", "Logged In as " + curName);
            Toast.makeText(RealtimeDatabaseActivity.this,
                    "Logged In as " + curName, Toast.LENGTH_SHORT).show();
        }
        catch (IllegalArgumentException e){
            Log.e("Login", "Login failed! " + e.getMessage());
            Toast.makeText(RealtimeDatabaseActivity.this,
                    "Login failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e("Login", "Login failed!");
            Toast.makeText(RealtimeDatabaseActivity.this,
                    "Login failed!", Toast.LENGTH_SHORT).show();
        }


    }

    public void addImage1(View view) {
        addEmote(view, "1");
    }

    public void addImage2(View view) {
        addEmote(view, "2");
    }

    // Add Emote based on String
    public void addEmote(View view, String imageNum) {

        EditText receiverUsernameField = (EditText) findViewById(R.id.receiverusername_id);
        String receiverUsername = receiverUsernameField.getText().toString();
        if(receiverUsername == null || receiverUsername.equals("")){
            Log.i("Adding Emote", "No receiver username entered!");
            Toast.makeText(RealtimeDatabaseActivity.this,
                    "No receiver username entered!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(myUser == null || myUser.username == null || myUser.username.equals("")){
            Log.i("Adding Emote", "Please log in before sending emotes!");
            Toast.makeText(RealtimeDatabaseActivity.this,
                    "Please log in before sending emotes!", Toast.LENGTH_SHORT).show();
            return;
        }

        //User receiver = new User(curName, "0", "");

        //RealtimeDatabaseActivity.this.onAddScore(mDatabase, player.isChecked() ? "user1" : "user2",imageNum);
        RealtimeDatabaseActivity.this.onAddScore(mDatabase, receiverUsername,imageNum,myUser.username);
        RealtimeDatabaseActivity.this.onAddScore(mDatabase, myUser.username+"sent",imageNum,receiverUsername);
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setNestedScrollingEnabled(false);

        rviewAdapter = new MyRviewAdapter(itemList);

        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);

    }

    // Reset USERS Button
    public void resetUsers(View view) {

        User user;
        //user = new User("user1", "0");
        user = new User(USER_1, "","");
        Task t1 = mDatabase.child("users").child(user.username).setValue(user);

        //user = new User("user2", "0");
        user = new User(USER_2, "","");
        Task t2 = mDatabase.child("users").child(user.username).setValue(user);

        user = new User(USER_1+"sent", "","");
        Task t3 = mDatabase.child("users").child(user.username).setValue(user);

        //user = new User("user2", "0");
        user = new User(USER_2+"sent", "","");
        Task t4 = mDatabase.child("users").child(user.username).setValue(user);

        if(!t1.isSuccessful() && !t2.isSuccessful()){
            Toast.makeText(getApplicationContext(),"Unable to reset players!",Toast.LENGTH_SHORT).show();
        }
        else if(!t1.isSuccessful() && t2.isSuccessful()){
            Toast.makeText(getApplicationContext(),"Unable to reset player1!",Toast.LENGTH_SHORT).show();
        }
        else if(t1.isSuccessful() && t2.isSuccessful()){
            Toast.makeText(getApplicationContext(),"Unable to reset player2!",Toast.LENGTH_SHORT).show();
        }


    }

    // Add data to firebase button
    public void doAddDataToDb(View view) {
        // Write a message to the database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message");

        Task t = myRef.setValue("Hello, World!");
        if(!t.isSuccessful()){
            Toast.makeText(getApplicationContext()
                    , "Failed to write value into firebase. " , Toast.LENGTH_SHORT).show();
            return;
        }

        // Read from the database by listening for a change to that item.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                //TextView tv = (TextView) findViewById(R.id.dataUpdateTextView);
                //tv.setText(value);
                //
                dataSnapshot.getKey().equals("users");
                //
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext()
                        , "Failed to write value into firebase. " , Toast.LENGTH_SHORT).show();
            }

        });

    }


    /**
     * Called on score_user1 add
     *
     * @param postRef
     * @param user
     */
    private void onAddScore(DatabaseReference postRef, String user, String imageNum, String senderName) {
        postRef
                .child("users")
                .child(user)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        User user = mutableData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(mutableData);
                        }

                        //user.score = String.valueOf(Integer.valueOf(user.score) + 5);
                        user.score = user.score + imageNum + " ";
                        //user.senders = user.senders + myUser.username + " ";
                        user.senders = user.senders + senderName + " ";

                        mutableData.setValue(user);
                        int i =0 ;

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                        Toast.makeText(getApplicationContext()
                                , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                    }



                });
    }

    private String parseData(String inputStr) throws JSONException {
        JSONObject jObject = new JSONObject(inputStr);
        Iterator<?> keys = jObject.keys();
        while( keys.hasNext() ){
            String key = (String)keys.next();
            if (key.equals(USER_1)) {
                return jObject.getString(key);
            }
        }
        return "Nothing matched!";

    }

    public void showReadData(View view, String addition) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i("status:","INSIDEEEEE");
                String curUsername = myUser.username;
//                curUsername = user.username;
                //if (user == null) return;
                //Log.i("All Data",user.score);
                //Log.i("MYDATA",dataSnapshot.getValue().toString());
                //Log.i("usersdata", String.valueOf(dataSnapshot.child("users").child("user1").child("score").getValue()));

                String [] stickerIdList = getStringList(String.valueOf(dataSnapshot.child("users")
                        .child(curUsername + addition).child("score").getValue()));
                String [] userList = getStringList(String.valueOf(dataSnapshot.child("users")
                        .child(curUsername + addition).child("senders").getValue()));


//                String [] stickerIdList = getStringList(String.valueOf(dataSnapshot.child("users").child("user1" + addition).child("score").getValue()));
//                String [] userList = getStringList(String.valueOf(dataSnapshot.child("users").child("user1" + addition).child("senders").getValue()));
                itemList = new ArrayList<>();
                for (int idx = 0; idx < stickerIdList.length; idx++) {
                    if (stickerIdList[idx].equals("1")){
                        MyItemCard itemCard = new MyItemCard(R.drawable.foo, "",
                                userList[idx], false);
                        itemList.add(itemCard);
                        //continue;
                    } else if (stickerIdList[idx].equals("2")) {
                        MyItemCard itemCard = new MyItemCard(R.drawable.thinking_face, "",
                                userList[idx], false);
                        itemList.add(itemCard);
                    }
                }
                createRecyclerView();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private String[] getStringList(String inputString) {
        return inputString.split(" ");
    }


    private void drawEmotes(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        String whoseInfo = player.isChecked() ? myUser.username : myUser.username+"sent";
        Log.i("INFO", whoseInfo);

        if (dataSnapshot.getKey().equalsIgnoreCase(whoseInfo)) {
            //score_user1.setText(String.valueOf(user.score));
            //user1.setText(user.username);
            String [] stickerIdList = getStringList(String.valueOf(user.score));
            String [] userList = getStringList(String.valueOf(user.senders));
            itemList = new ArrayList<>();
            for (int idx = 0; idx < stickerIdList.length; idx++) {
                if (stickerIdList[idx].equals("1")){
                    MyItemCard itemCard = new MyItemCard(R.drawable.foo, "",
                            userList[idx], false);
                    itemList.add(itemCard);
                    //continue;
                } else if (stickerIdList[idx].equals("2")) {
                    MyItemCard itemCard = new MyItemCard(R.drawable.thinking_face,
                            "", userList[idx], false);
                    itemList.add(itemCard);
                }
            }
            createRecyclerView();
        } else {
            //score_user2.setText(String.valueOf(user.score));
            //user2.setText(user.username);
        }
    }


}
