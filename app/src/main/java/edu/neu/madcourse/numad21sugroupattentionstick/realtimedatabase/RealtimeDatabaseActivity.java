package edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.numad21sugroupattentionstick.MyItemCard;
import edu.neu.madcourse.numad21sugroupattentionstick.MyRviewAdapter;
import edu.neu.madcourse.numad21sugroupattentionstick.R;
import edu.neu.madcourse.numad21sugroupattentionstick.realtimedatabase.models.User;
import edu.neu.madcourse.numad21sugroupattentionstick.utils.Utils;

public class RealtimeDatabaseActivity extends AppCompatActivity {

  private static final String TAG = RealtimeDatabaseActivity.class.getSimpleName();
  private static final String USER_1 = "user1";
  private static final String USER_2 = "user2";
  private DatabaseReference mDatabase;
  private TextView sender;
  private TextView score_sender;
  private TextView recipient;
  private TextView score_recipient;
  private RadioButton player;
  private String imageNumToSend = "";
  private String userNameToReceive = "";

  // Adding variable to hold my user name
  private User myUser = new User("", "", "");
  private RecyclerView recyclerView;
  private MyRviewAdapter rviewAdapter;
  private RecyclerView.LayoutManager rLayoutManger;
  private ArrayList<MyItemCard> itemList = new ArrayList<>();
  private RadioGroup radioGroup;
  //RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(radioGroup
  // .getCheckedRadioButtonId());


  @SuppressLint("RestrictedApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_realtime_database);
    player = findViewById(R.id.my_stickers);
    radioGroup = findViewById(R.id.radioGroup1);

    //
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup arg0, int id) {
        switch (id) {
          case R.id.my_stickers:
            Log.i(TAG, "My Stickers radio button pressed");
            showReadData(findViewById(android.R.id.content), "");
            break;
          case R.id.stickers_i_sent:
            Log.i(TAG, "Stickers I sent radio button pressed");
            showReadData(findViewById(android.R.id.content), "sent");
            break;
          default:
            Log.i(TAG, "nothing!");
            break;
        }
      }
    });
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

  private void createRecyclerView() {
    rLayoutManger = new LinearLayoutManager(this);

    recyclerView = findViewById(R.id.recycler_view2);
    recyclerView.setHasFixedSize(true);
    //recyclerView.setNestedScrollingEnabled(false);

    rviewAdapter = new MyRviewAdapter(itemList);

    recyclerView.setAdapter(rviewAdapter);
    recyclerView.setLayoutManager(rLayoutManger);
  }

  public void addImage1(View view) {
    addEmote(view, "1");
  }

  public void addImage2(View view) {
    addEmote(view, "2");
  }

  public void login(View view) {
    try {
      EditText givenName = (EditText) findViewById(R.id.myusername_id);
      String curName = givenName.getText().toString();
      Log.i("usernamevalue", curName);
      myUser = new User(curName, "", "");
      mDatabase.child("users").child(curName).get().addOnCompleteListener(task -> {
        if (Objects.requireNonNull(task.getResult()).getValue() == null) {
          mDatabase.child("users").child(curName).setValue(myUser);
          mDatabase.child("users").child(curName + "sent").setValue(myUser);
        }
      });
      showReadData(findViewById(android.R.id.content), player.isChecked() ? "" : "sent");
      //User myUser;
      Log.i("Login", "Logged In as " + curName);
      Toast.makeText(RealtimeDatabaseActivity.this,
                     "Logged In as " + curName, Toast.LENGTH_SHORT).show();
    } catch (IllegalArgumentException e) {
      Log.e("Login", "Login failed! " + e.getMessage());
      Toast.makeText(RealtimeDatabaseActivity.this,
                     "Login failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      Log.e("Login", "Login failed!");
      Toast.makeText(RealtimeDatabaseActivity.this,
                     "Login failed!", Toast.LENGTH_SHORT).show();
    }
  }

  // Add Emote based on String
  public void addEmote(View view, String imageNum) {

    EditText receiverUsernameField = findViewById(R.id.receiverusername_id);
    String receiverUsername = receiverUsernameField.getText().toString();
    if (receiverUsername.equals("")) {
      Log.i("Adding Emote", "No receiver username entered!");
      Toast.makeText(RealtimeDatabaseActivity.this,
                     "No receiver username entered!", Toast.LENGTH_SHORT).show();
      return;
    }
    if (myUser == null || myUser.username == null || myUser.username.equals("")) {
      Log.i("Adding Emote", "Please log in before sending emotes!");
      Toast.makeText(RealtimeDatabaseActivity.this,
                     "Please log in before sending emotes!", Toast.LENGTH_SHORT).show();
      return;
    }
    imageNumToSend = imageNum;
    userNameToReceive = receiverUsername;
    RealtimeDatabaseActivity.this.onAddScore(mDatabase, receiverUsername, imageNum,
                                             myUser.username);
    RealtimeDatabaseActivity.this.onAddScore(mDatabase, myUser.username + "sent", imageNum,
                                             receiverUsername);
  }


  /**
   * Called on score_user1 add
   *
   * @param postRef
   * @param user
   */
  private void onAddScore(DatabaseReference postRef, String user, String imageNum,
                          String senderName) {
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
                user.score = user.score + imageNum + ",";
                user.senders = user.senders + senderName + ",";
                user.scoreTimes = user.scoreTimes + Utils.date() + ",";
                mutableData.setValue(user);
                return Transaction.success(mutableData);
              }

              @Override
              public void onComplete(DatabaseError databaseError, boolean comitted,
                                     DataSnapshot dataSnapshot) {
                if (!comitted) {
                  Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                  Toast.makeText(getApplicationContext()
                          , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(getApplicationContext()
                          , "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                }
              }
            });
  }

  private String parseData(String inputStr) throws JSONException {
    JSONObject jObject = new JSONObject(inputStr);
    Iterator<?> keys = jObject.keys();
    while (keys.hasNext()) {
      String key = (String) keys.next();
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
        String curUsername = myUser.username;

        String[] stickerIdList = getStringList(String.valueOf(dataSnapshot.child("users")
                                                                      .child(curUsername + addition).child("score").getValue()));
        String[] userList = getStringList(String.valueOf(dataSnapshot.child("users")
                                                                 .child(curUsername + addition).child("senders").getValue()));
        String[] msgTimeList = getStringList(String.valueOf(dataSnapshot.child("users")
                                                                    .child(curUsername + addition).child("scoreTimes").getValue()));
        itemList = new ArrayList<>();
        for (int idx = 0; idx < stickerIdList.length; idx++) {
          if (stickerIdList[idx].equals("1")) {
            MyItemCard itemCard = new MyItemCard(R.drawable.foo,
                                                 userList[idx], msgTimeList[idx], false);
            itemList.add(itemCard);
            //continue;
          } else if (stickerIdList[idx].equals("2")) {
            MyItemCard itemCard = new MyItemCard(R.drawable.thinking_face,
                                                 userList[idx], msgTimeList[idx], false);
            itemList.add(itemCard);
          }
        }
        createRecyclerView();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });
  }

  private String[] getStringList(String inputString) {
    return inputString.split(",");
  }


  private void drawEmotes(DataSnapshot dataSnapshot) {
    User user = dataSnapshot.getValue(User.class);

    String whoseInfo = player.isChecked() ? myUser.username : myUser.username + "sent";
    Log.i("INFO", whoseInfo);

    if (dataSnapshot.getKey().equalsIgnoreCase(whoseInfo)) {
      String[] stickerIdList = getStringList(String.valueOf(user.score));
      String[] userList = getStringList(String.valueOf(user.senders));
      itemList = new ArrayList<>();
      for (int idx = 0; idx < stickerIdList.length; idx++) {
        if (stickerIdList[idx].equals("1")) {
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
    }
  }

  public void sendDatabaseMessage(View view) {

    System.out.println(userNameToReceive);
    System.out.println(imageNumToSend);
    if (myUser == null || myUser.username == null || myUser.username.equals("")) {
      Log.i("Adding Emote", "Please log in before sending emotes!");
      Toast.makeText(RealtimeDatabaseActivity.this,
                     "Please log in before sending emotes!", Toast.LENGTH_SHORT).show();
      return;
    } else {
      RealtimeDatabaseActivity.this.onAddScore(mDatabase, userNameToReceive, imageNumToSend,
                                               myUser.username);
      RealtimeDatabaseActivity.this.onAddScore(mDatabase, myUser.username + "sent",
                                               imageNumToSend, userNameToReceive);
    }
  }

  // Add data to firebase button
  public void doAddDataToDb(View view) {
    // Write a message to the database
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message");

    Task t = myRef.setValue("Hello, World!");
    if (!t.isSuccessful()) {
      Toast.makeText(getApplicationContext(), "Failed to write value into firebase. ",
                     Toast.LENGTH_SHORT).show();
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
      }

      @Override
      public void onCancelled(DatabaseError error) {
        // Failed to read value
        Log.w(TAG, "Failed to read value.", error.toException());
        Toast.makeText(getApplicationContext(), "Failed to write value into firebase. ",
                       Toast.LENGTH_SHORT).show();
      }

    });
  }

  // Reset USERS Button
  public void resetUsers(View view) {

    User user;
    //user = new User("user1", "0");
    user = new User(USER_1, "", "");
    Task t1 = mDatabase.child("users").child(user.username).setValue(user);

    //user = new User("user2", "0");
    user = new User(USER_2, "", "");
    Task t2 = mDatabase.child("users").child(user.username).setValue(user);

    user = new User(USER_1 + "sent", "", "");
    Task t3 = mDatabase.child("users").child(user.username).setValue(user);

    //user = new User("user2", "0");
    user = new User(USER_2 + "sent", "", "");
    Task t4 = mDatabase.child("users").child(user.username).setValue(user);

    if (!t1.isSuccessful() && !t2.isSuccessful()) {
      Toast.makeText(getApplicationContext(), "Unable to reset players!", Toast.LENGTH_SHORT).show();
    } else if (!t1.isSuccessful() && t2.isSuccessful()) {
      Toast.makeText(getApplicationContext(), "Unable to reset player1!", Toast.LENGTH_SHORT).show();
    } else if (t1.isSuccessful() && t2.isSuccessful()) {
      Toast.makeText(getApplicationContext(), "Unable to reset player2!", Toast.LENGTH_SHORT).show();
    }
  }
}
