package cryptick.firebaseandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TickStore extends AppCompatActivity {

   // public String recipient;
    public String flag;
    private int radiox;
    private FirebaseAuth mAuth;
    String balance;
    int bal;

    private String mCurrentUserId;
   // private String mChatUser="fezTQKTuxROMk2pylEAhFOa0Wbk1";
    private DatabaseReference mRootRef,updateBal;


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton:
                if (checked)
                    radiox = 100;
                break;
            case R.id.radioButton2:
                if (checked)
                    radiox = 500;
                break;
            case R.id.radioButton3:
                if (checked)
                    radiox = 1000;
                break;
            case R.id.radioButton4:
                if (checked)
                    radiox = 10000;
                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tick_store);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
       // mChatUser = getIntent().getStringExtra("user_id");

        Button earnTicks=(Button)findViewById(R.id.button4);
        Button buyButton = (Button) findViewById(R.id.buyButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        updateBal = mRootRef.child("Users").child(mCurrentUserId);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           // recipient = extras.getString("EXTRA_RID");
            flag = extras.getString("EXTRA_FLAG");
        }

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        if (flag != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }
        updateBal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                balance = dataSnapshot.child("tickBal").getValue().toString();
                bal= Integer.parseInt(balance);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        buyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(), bal, Toast.LENGTH_SHORT).show();
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(TickStore.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("₹" + radiox)
                        .setTitle(R.string.are_you_sure);

                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateBal = mRootRef.child("Users").child(mCurrentUserId);
                        if(bal==0) {
                            Map<String, Object> tickupdates = new HashMap<>();
                            tickupdates.put("tickBal", radiox);
                            updateBal.updateChildren(tickupdates);
                            Toast.makeText(getApplicationContext(), "Congo! You have bought" + radiox + " ticks!", Toast.LENGTH_SHORT).show();
                            //  Log.i("Recipient ID", recipient);
                        }
                        else{
                            Map<String, Object> tickupdates = new HashMap<>();
                            tickupdates.put("tickBal", bal+radiox);
                            updateBal.updateChildren(tickupdates);
                            Toast.makeText(getApplicationContext(), radiox + " ticks! Added Successfully", Toast.LENGTH_SHORT).show();
                        }

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
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        earnTicks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=EPKmYheOmiw")));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addTick();
                    }
                }, 120000);

                }

        });

    }
    
    public void addTick() {

        Map<String, Object> tickupdates = new HashMap<>();
        tickupdates.put("tickBal", bal + 5);
        updateBal.updateChildren(tickupdates);
        Toast.makeText(getApplicationContext(), 5 + " ticks! Added Successfully", Toast.LENGTH_SHORT).show();
    }
}
