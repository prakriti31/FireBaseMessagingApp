package com.codewithharry.firebase.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithharry.firebase.R;
import com.codewithharry.firebase.models.ModelComment;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.myHolder> {

    Context context;
    List<ModelComment> commentList;

    public AdapterComments(Context context, List<ModelComment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //bind the row_comments.xml layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);

        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int i) {

        //get the data
        String uid = commentList.get(i).getUid();
        String name = commentList.get(i).getuName();
        String email = commentList.get(i).getuEmail();
        String image = commentList.get(i).getuDp();
        String cid = commentList.get(i).getcId();
        String comment = commentList.get(i).getComment();
        String timeStamp = commentList.get(i).getTimeStamp();

        //convert timeStamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set the data
        holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        holder.timeTv.setText(pTime);

        //set user dp
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_default_image_blue).into(holder.avatarIv);
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class myHolder extends RecyclerView.ViewHolder {

        //declare views from row_comments.xml
        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv;

        public myHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }

    }

}
