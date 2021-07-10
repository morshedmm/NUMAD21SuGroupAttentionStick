package edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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

import java.util.ArrayList;

import edu.neu.madcourse.numad21sugroupattentionstick.MyItemCard;
import edu.neu.madcourse.numad21sugroupattentionstick.MyRviewAdapter;
import edu.neu.madcourse.numad21sugroupattentionstick.R;
import edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase.models.User;

public class RealtimeDatabaseActivity extends AppCompatActivity {

    private static final String TAG = RealtimeDatabaseActivity.class.getSimpleName();

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


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_database);

        //user1 = (TextView) findViewById(R.id.username1);
        //user2 = (TextView) findViewById(R.id.username2);
        //score_user1 = (TextView) findViewById(R.id.score1);
        //score_user2 = (TextView) findViewById(R.id.score2);
        player = (RadioButton) findViewById(R.id.player1);

        // Connect with firebase
        //
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Update the score in realtime
        mDatabase.child("users").addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        showScore(dataSnapshot);
                        Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        showScore(dataSnapshot);
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

        EditText givenName = (EditText) findViewById(R.id.myusername_id);
        String curName = givenName.getText().toString();
        Log.i("usernamevalue", curName);



        //User myUser;
        myUser = new User(curName, "0", "");


        //}

    }

    public void addImage1(View view) {
        addFivePoints(view, "1");
    }

    public void addImage2(View view) {
        addFivePoints(view, "2");
    }

    // Add 5 points Button
    public void addFivePoints(View view, String imageNum) {

        EditText givenName = (EditText) findViewById(R.id.receiverusername_id);
        String curName = givenName.getText().toString();

        //User receiver = new User(curName, "0", "");

        //RealtimeDatabaseActivity.this.onAddScore(mDatabase, player.isChecked() ? "user1" : "user2",imageNum);
        RealtimeDatabaseActivity.this.onAddScore(mDatabase, curName,imageNum,myUser.username);
        RealtimeDatabaseActivity.this.onAddScore(mDatabase, myUser.username+"sent",imageNum,curName);
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
        user = new User("user1", "","");
        Task t1 = mDatabase.child("users").child(user.username).setValue(user);

        //user = new User("user2", "0");
        user = new User("user2", "","");
        Task t2 = mDatabase.child("users").child(user.username).setValue(user);

        user = new User("user1sent", "","");
        Task t3 = mDatabase.child("users").child(user.username).setValue(user);

        //user = new User("user2", "0");
        user = new User("user2sent", "","");
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

    private String[] getStringList(String inputString) {
        return inputString.split(" ");
    }


    private void showScore(DataSnapshot dataSnapshot) {
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
                    MyItemCard itemCard = new MyItemCard(R.drawable.foo, "", userList[idx], false);
                    itemList.add(itemCard);
                    //continue;
                } else if (stickerIdList[idx].equals("2")) {
                    MyItemCard itemCard = new MyItemCard(R.drawable.thinking_face, "", userList[idx], false);
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
