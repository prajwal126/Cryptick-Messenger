package cryptick.firebaseandroid;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private List<Messages> mMessageList;
    FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    public long curtime;
    public String mTime,time1;
    public String curr_user_id;
    public String from_user;
    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public TextView timeText;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);


            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            timeText=(TextView) view.findViewById(R.id.time_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage=(ImageView)view.findViewById(R.id.imageView_2);


        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);
        curtime= c.getTime();
        Date date = new Date(curtime);
        DateFormat formatter = new SimpleDateFormat("E, hh:mm a");
        time1=formatter.format(date);
        from_user = c.getFrom();
        mAuth=FirebaseAuth.getInstance();
        curr_user_id=mAuth.getCurrentUser().getUid();

        //mTime = Long.toString(curtime);
        if(from_user.equals(curr_user_id)){
            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);
        }
        else {
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);
        }


        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name);

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.timeText.setText(time1);
            viewHolder.messageImage.setVisibility(View.INVISIBLE);

        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.timeText.setText(time1);
            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

        }
    }


    @Override
    public int getItemCount () {
        return mMessageList.size();
    }


}
