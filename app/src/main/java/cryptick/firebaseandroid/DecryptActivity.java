package cryptick.firebaseandroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;


public class DecryptActivity extends AppCompatActivity {
    private DatabaseReference mRootRef,updateBal;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    int bal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
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
        final EditText mEdit1 = (EditText) findViewById(R.id.editText);
        Button mButton = (Button) findViewById(R.id.button2);
        final EditText mEdit2 = (EditText) findViewById(R.id.editText3);
        final EditText mEdit3 = (EditText) findViewById(R.id.editText4);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DecryptActivity.this);
                builder.setMessage("Use 1 Tick").setTitle(R.string.are_you_sure);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> tickupdates = new HashMap<>();
                        tickupdates.put("tickBal", bal - 1);
                        updateBal.updateChildren(tickupdates);

                        String password = mEdit2.getText().toString();
                        String encryptedMsg = mEdit1.getText().toString();
                        try {


                            String messageAfterDecrypt = AESCrypt.decrypt(password, encryptedMsg);
                            //mEdit3.setText(messageAfterDecrypt);
                            mEdit3.setText(messageAfterDecrypt);

                        } catch (GeneralSecurityException e) {
                            //handle error - could be due to incorrect password or tampered encryptedMsg
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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
    }
}



