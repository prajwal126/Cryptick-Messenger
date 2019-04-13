package cryptick.firebaseandroid;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;


@RequiresApi(api = Build.VERSION_CODES.N)
public class scheduleMessage extends AppCompatActivity implements View.OnClickListener {
    EditText Edit_Time;
    EditText edMessage3, password;
    ImageView send;
    long calinsec, seconds,timee;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private Users userModel;
    private String mChatUser;
    private String message;
    private String key;
    private String time1;
    private String encryptedMsg = "";

    int bal;
    private DatabaseReference mRootRef,updateBal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_message);
        bindViews();
        //mFirebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        message = edMessage3.getText().toString();
        key = password.getText().toString();
        String userName = getIntent().getStringExtra("user_name");
        //   userModel = new Users(name,image,status,thumb_image);
        SharedPreferences pref = getSharedPreferences("MyPref", MODE_READ_ONLY);
         mChatUser = pref.getString("user_id", null);
        updateBal = mRootRef.child("Users").child(mCurrentUserId);

        updateBal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String balance= dataSnapshot.child("tickBal").getValue().toString();
                bal= Integer.parseInt(balance);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }



    private void bindViews() {
        edMessage3 = (EditText) findViewById(R.id.editText5);

        password = (EditText) findViewById(R.id.editText7);
        Edit_Time = (EditText) findViewById(R.id.editText6);
        Edit_Time.setOnClickListener(this);
        send = (ImageView) findViewById(R.id.imageView3);
        send.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editText6:
                timesch();
                break;
            case R.id.imageView3:
                send();
                break;

        }
    }
    //public void onRadioButtonClicked(View view) {
        //  Is the button now checked?
      //  boolean checked = ((RadioButton) view).isChecked();

        //    Check which radio button was clicked
     //   switch (view.getId()) {
      //      case R.id.radioButton:
      //          if (checked)
    //                mChatUser="fezTQKTuxROMk2pylEAhFOa0Wbk1";
    //            break;
   //         case R.id.radioButton2:
   //             if (checked)
     //               mChatUser="Zyj1IUA1W1PSHF9fIRHjrhbWctI3";
     //           break;
     //   }
//    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    //private void timesch() {
            //    Calendar mcurrentTime = Calendar.getInstance();
            //    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            //   int minute = mcurrentTime.get(Calendar.MINUTE);

            //    TimePickerDialog mTimePicker;
            //    mTimePicker = new TimePickerDialog(scheduleMessage.this, new TimePickerDialog.OnTimeSetListener() {
            //       @Override
            //        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            //           Edit_Time.setText(selectedHour + ":" + selectedMinute);
            //       }
            //   }, hour, minute, true);
            //   mTimePicker.setTitle("Select Time");
            //    mTimePicker.show();



    Calendar myCalendar = Calendar.getInstance();
   // Calendar newcal=Calendar.getInstance();


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            calinsec = System.currentTimeMillis();

            updateLabel();
        }

    };

    private void timesch() {
        // TODO Auto-generated method stub
        new DatePickerDialog(scheduleMessage.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {

        myCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
       seconds = myCalendar.getTimeInMillis();
        Edit_Time.setText(String.valueOf(seconds));


    }


    private void send() {


       // String mChatUser;
        try {
            if (calinsec == seconds) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String message = edMessage3.getText().toString();
                        String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                        String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                        DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();

                        String push_id = user_message_push.getKey();
                        Map messageMap = new HashMap();
                        messageMap.put("message", message);
                        messageMap.put("seen", false);
                        messageMap.put("type", "text");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null) {

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });
                    }
                    }, 15000);
                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(scheduleMessage.this);
                builder.setMessage("Use 1 Tick").setTitle(R.string.are_you_sure);
                updateBal = mRootRef.child("Users").child(mCurrentUserId);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                message = edMessage3.getText().toString();
                                key = password.getText().toString();

                                // this code will be executed after 2 seconds
                                try {
                                    encryptedMsg = AESCrypt.encrypt(key, message);
                                } catch (GeneralSecurityException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(), "Encrypted", Toast.LENGTH_SHORT).show();
                                String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                                String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;
                                Map<String, Object> tickupdates = new HashMap<>();
                                tickupdates.put("tickBal", bal - 1);
                                updateBal.updateChildren(tickupdates);
                                DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();

                                String push_id = user_message_push.getKey();

                                Map messageMap = new HashMap();
                                messageMap.put("message", encryptedMsg);
                                messageMap.put("seen", false);
                                messageMap.put("type", "text");
                                messageMap.put("time", seconds);
                                messageMap.put("from", mCurrentUserId);

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                                edMessage3.setText("");
                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        if (databaseError != null) {

                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                        }

                                    }
                                });
                            }
                        }, 15000);

                    }

                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Set other dialog properties

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), mChatUser, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), mCurrentUserId, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Null Pointer", Toast.LENGTH_SHORT).show();

                }
            }


        }



