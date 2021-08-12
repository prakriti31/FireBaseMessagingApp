package com.codewithharry.firebase.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithharry.firebase.R;
import com.codewithharry.firebase.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    ArrayList<ModelChat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    public AdapterChat(Context context, ArrayList<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layouts: row_chat_left.xml for receiver, row_chat_right for sender
        View view;
        if (i == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
        }
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChat.MyHolder myHolder, int i) {

        //get data
        String message = chatList.get(i).getMessage();
        String timeStamp = chatList.get(i).getTimestamp();
        String status = chatList.get(i).getIsSeen();

        //convert time stamp to dd/mm/yyyy format hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //set data
        myHolder.messageTv.setText(message);
        myHolder.timeTv.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).into(myHolder.profileIv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //click to show delete dialog
        myHolder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete message confirm
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this message?");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(i);
                    }
                });
                //cancel delete button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });
                //create and show dialog box
                builder.create().show();
            }
        });

        //set seen/delivered status of messages
        int n = chatList.size();
        if (i == n - 1) {
            if (Boolean.parseBoolean(status)) {
                myHolder.isSeenTv.setText("Seen");
            } else {
                Toast.makeText(context, "" + status, Toast.LENGTH_SHORT).show();
                myHolder.isSeenTv.setText("Delivered");
            }
        } else {
            myHolder.isSeenTv.setVisibility(View.GONE);
        }

    }

    private void deleteMessage(int position) {
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /*Logic:
         *Get timestamp of clicked message
         * Compare the timestamp of the clicked message with all messages in chats
         * Where both values match, delete that message
         * This will allow sender to delete his and receiver's message*/
        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    /*If you want to allow sender to delete only his message then
                    * compare sender value with current user's UID
                    * If the match, its the message that the sender is trying to delete
                    if(ds.child("sender").getValue().equals(myUID)){
                        We can do one of the two things here
                         * 1)Remove the message from the chats
                         * 2)Set the value of message "This message was deleted
                        //REMOVE THE MESSAGE FROM CHATS
                        ds.getRef().removeValue();
                        //SET THE VALUE OF DELETED MESSAGE
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "This message was deleted");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "Message deleted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "You can only delete your messages", Toast.LENGTH_SHORT).show();
                    }

                    /*We can do one of the two things here
                     * 1)Remove the message from the chats
                     * 2)Set the value of message "This message was deleted*/
                    //REMOVE THE MESSAGE FROM CHATS
                    //    ds.getRef().removeValue();
                    //SET THE VALUE OF DELETED MESSAGE
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("message", "This message was deleted");
                    ds.getRef().updateChildren(hashMap);

                    Toast.makeText(context, "Message deleted!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {

        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        //get currently signed in user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    //view holder class
    static class MyHolder extends RecyclerView.ViewHolder {

        //views
        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLayout; //for clicking listener to show delete option

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }

}