package com.example.uniride;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyHomeChatMessageAdapter extends RecyclerView.Adapter<MyHomeChatMessageAdapter.ViewHolder> {

    ArrayList<MessageModel> myChatData;
    Context context;
    int chatID;

    public MyHomeChatMessageAdapter(ArrayList<MessageModel> rawChatData, int chatID, HomeChatMessageActivity activity) {
        this.chatID = chatID;
        updateData(rawChatData);

        this.context = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timestampText;
        TextView messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }
    }

    public void updateData(ArrayList<MessageModel> newData) {
        myChatData = new ArrayList<>();

        // Only retrieve from a specific chatID
        for (MessageModel message : newData) {
            if (message.getChatID() == chatID) {
                myChatData.add(message);
            }
        }

        // Sort chats by date
        Collections.sort(myChatData, new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel msg2, MessageModel msg1) {
                return msg2.getDate().compareTo(msg1.getDate());
            }
        });

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHomeChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == 1) {
            view = layoutInflater.inflate(R.layout.home_message_outgoing_item, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.home_message_incoming_item, parent, false);
        }
        MyHomeChatMessageAdapter.ViewHolder viewHolder = new MyHomeChatMessageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHomeChatMessageAdapter.ViewHolder holder, int position) {
        final MessageModel chat = myChatData.get(position);

        holder.messageText.setText(chat.getMessage());

        // Format timestamp
        Date chatDate = chat.getDate();
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, yyyy · hh:mm a");
        holder.timestampText.setText(timeFormat.format(chatDate));
    }

    @Override
    public int getItemViewType(int position) {
        UserModel currentUser = DataGenerator.loadUserData().get(0);
        MessageModel message = myChatData.get(position);

        if (message.getSender().equals(currentUser)) {
            return 1; // Outgoing message
        } else {
            return 2; // Incoming message
        }
    }

    @Override
    public int getItemCount() { return myChatData.size(); }
}
