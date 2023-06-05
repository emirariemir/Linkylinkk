package com.emirari.linkylinkk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emirari.linkylinkk.databinding.RecyclerRowBinding;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class PostAdopter extends RecyclerView.Adapter<PostAdopter.PostHolder> {

    public PostAdopter(Context context, ArrayList<Post> postArrayList) {
        if (postArrayList == null) {
            this.context = context;
            this.postArrayList = new ArrayList<>();
        } else {
            this.context = context;
            this.postArrayList = postArrayList;
        }
    }

    private ArrayList<Post> postArrayList;
    private Context context;

    private String calculateTimeAgo(String datePost) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        try {
            long time = sdf.parse(datePost).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    class PostHolder extends RecyclerView.ViewHolder {
        RecyclerRowBinding recyclerRowBinding;

        public PostHolder(@NonNull RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.recyclerUserMail.setText(postArrayList.get(position).userMail);
        holder.recyclerRowBinding.recyclerLink.setText(postArrayList.get(position).title);
        holder.recyclerRowBinding.recyclerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                String url = postArrayList.get(clickedPosition).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });
        if (postArrayList.get(position).strTime != null) {
            String timeAgo = calculateTimeAgo(postArrayList.get(position).strTime);
            holder.recyclerRowBinding.timeTextView.setText(timeAgo);
        } else {
            holder.recyclerRowBinding.timeTextView.setText("Uploaded eternity ago.");
        }
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
}
