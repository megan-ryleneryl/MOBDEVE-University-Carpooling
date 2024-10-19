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

public class MyHomeChatAdapter extends RecyclerView.Adapter<MyHomeChatAdapter.ViewHolder> {

    ArrayList<MessageModel> myChatData;
    Context context;

    public MyHomeChatAdapter(ArrayList<MessageModel> rawChatData, HomeChatActivity activity) {
        // Not an efficient approach, to be refined in MCO3 by adding a chat-list class

        // Sort chats by date
        Collections.sort(rawChatData, new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel msg1, MessageModel msg2) {
                return msg2.getDate().compareTo(msg1.getDate());
            }
        });

        // Use a HashSet to track unique chat IDs and filter duplicates
        UserModel currentUser = DataGenerator.loadUserData().get(0);
        myChatData = new ArrayList<>();
        HashSet<Integer> uniqueChatIds = new HashSet<>();

        for (MessageModel message : rawChatData) {
            if (!uniqueChatIds.contains(message.getChatID())) {
                if (currentUser == message.getSender() || currentUser == message.getRecipient()) {
                    uniqueChatIds.add(message.getChatID());
                    myChatData.add(message);
                }
            }
        }

        this.context = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView pfpImage;
        TextView nameText;
        TextView lastMessageText;
        TextView timestampText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pfpImage = itemView.findViewById(R.id.pfpImage);
            nameText = itemView.findViewById(R.id.nameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.home_chat_item,parent,false);
        MyHomeChatAdapter.ViewHolder viewHolder = new MyHomeChatAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel currentUser = DataGenerator.loadUserData().get(0);
        final MessageModel chat = myChatData.get(position);

        if (chat.getSender() == currentUser) {
            holder.pfpImage.setImageResource(chat.getRecipient().getPfp());
            holder.nameText.setText(chat.getRecipient().getName());
        } else if(chat.getRecipient() == currentUser) {
            holder.pfpImage.setImageResource(chat.getSender().getPfp());
            holder.nameText.setText(chat.getSender().getName());
        }
        holder.lastMessageText.setText(chat.getMessage());

        // Format timestamp
        Date chatDate = chat.getDate();
        Calendar currentCal = Calendar.getInstance();
        Calendar chatCal = Calendar.getInstance();
        chatCal.setTime(chatDate);

        boolean isSameDay = currentCal.get(Calendar.YEAR) == chatCal.get(Calendar.YEAR) &&
                currentCal.get(Calendar.DAY_OF_YEAR) == chatCal.get(Calendar.DAY_OF_YEAR);
        boolean isSameYear = currentCal.get(Calendar.YEAR) == chatCal.get(Calendar.YEAR);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a"); // Time format
        SimpleDateFormat dateFormatSameYear = new SimpleDateFormat("MMM dd"); // Date format without year
        SimpleDateFormat dateFormatDifferentYear = new SimpleDateFormat("MMM dd, yyyy"); // Date format with year

        if (isSameDay) {
            holder.timestampText.setText(timeFormat.format(chatDate));
        } else {
            if (isSameYear) {
                holder.timestampText.setText(dateFormatSameYear.format(chatDate));
            } else {
                holder.timestampText.setText(dateFormatDifferentYear.format(chatDate));
            }
        }

        // Clickable items
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomeChatMessageActivity.class);

                UserModel currentUser = DataGenerator.loadUserData().get(0);
                String chatmate = "";
                if (chat.getSender() == currentUser) {
                    chatmate = chat.getRecipient().getName();
                } else if(chat.getRecipient() == currentUser) {
                    chatmate = chat.getSender().getName();
                }

                intent.putExtra("chatID", chat.getChatID());
                intent.putExtra("chatmate", chatmate);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return myChatData.size(); }
}
