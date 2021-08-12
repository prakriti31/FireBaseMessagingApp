package com.codewithharry.firebase.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithharry.firebase.ChatActivity;
import com.codewithharry.firebase.R;
import com.codewithharry.firebase.TheirProfileActivity;
import com.codewithharry.firebase.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    ArrayList<ModelUser> userList;

    //constructor
    public AdapterUsers(Context context, ArrayList<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout(row_user.xml)
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_users, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        String hisUID = userList.get(i).getUid();
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        String userEmail = userList.get(i).getEmail();
        //set data
        myHolder.mNameTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_profile_black)
                    .into(myHolder.mAvatarIv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //handle item click
        myHolder.itemView.setOnClickListener(v -> {
            //show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(new String[] {"Profile", "Message"}, (dialog, which) -> {
                if (which == 0) {

                    //profile clicked
                    /*Click to go to TheirProfileActivity with UID,
                    this UID is clicked of the clicked user
                    which will be used to show user specific data/posts*/

                    Intent intent = new Intent(context, TheirProfileActivity.class);
                    intent.putExtra("uid", hisUID);
                    context.startActivity(intent);

                } if (which == 1) {

                    //chat clicked
                    /*Click user from list to start chatting with
                     * Start activity by putting UID of receiver
                     * We will use that IUD to identify the user we're texting*/

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("hisUID", hisUID);
                    context.startActivity(intent);

                }
            });
            builder.create().show();
        });
    }

    @Override
    public int getItemCount() {

        return userList.size();
    }

    //View holder class
    class MyHolder extends RecyclerView.ViewHolder {

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
        }
    }

}
