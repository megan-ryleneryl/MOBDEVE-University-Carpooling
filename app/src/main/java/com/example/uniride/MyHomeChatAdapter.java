package com.example.uniride;

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyHomeChatAdapter extends RecyclerView.Adapter<MyHomeChatAdapter.ViewHolder> {

    private List<MessageModel> chatList;
    private List<MessageModel> messageList;
    private int userID;
    private Context context;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();;

    public MyHomeChatAdapter(Context context, List<MessageModel> chatList, List<MessageModel> messageList, int userID) {
        this.context = context;
        this.chatList = chatList;
        this.messageList = messageList;
        this.userID = userID;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView pfpImage;
        TextView nameText, lastMessageText, timestampText;

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
    public MyHomeChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MyHomeChatAdapter", "onCreateViewHolder called?????");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_chat_item, parent, false);
        Log.d("MyHomeChatAdapter", "onCreateViewHolder called");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHomeChatAdapter.ViewHolder holder, int position) {
        MessageModel chat = chatList.get(position);

        if (chat != null) {
            Log.d("MyHomeChatAdapter", "Received chatList : " + chatList.size());
            UserModel otherUser = null;
            if (userID == chat.getSenderID()) {
                otherUser = chat.getRecipient();
            } else if (userID == chat.getRecipientID()) {
                otherUser = chat.getSender();
            }

            if (otherUser != null) {
                holder.pfpImage.setImageResource(otherUser.getPfp());
                holder.nameText.setText(otherUser.getName());

                holder.lastMessageText.setText(chat.getMessage());

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy · hh:mm a");
                holder.timestampText.setText(dateFormat.format(chat.getDate()));

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, HomeChatMessageActivity.class);
                    intent.putExtra("chatID", chat.getChatID());
                    intent.putExtra("chatmate", holder.nameText.getText().toString());
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}