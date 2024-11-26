package com.example.uniride;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyHomeChatMessageAdapter extends RecyclerView.Adapter<MyHomeChatMessageAdapter.ViewHolder> {

    private int otherUserID;
    private Context context;
    private List<MessageModel> messageList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth  mAuth = FirebaseAuth.getInstance();

    public MyHomeChatMessageAdapter(Context context, List<MessageModel> messageList, int otherUserID) {
        this.context = context;
        this.messageList = messageList;
        this.otherUserID = otherUserID;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }
    }

    @NonNull
    @Override
    public MyHomeChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate based on viewType
        if (viewType == 1) { // Outgoing message
            view = inflater.inflate(R.layout.home_message_outgoing_item, parent, false);
        } else { // Incoming message
            view = inflater.inflate(R.layout.home_message_incoming_item, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHomeChatMessageAdapter.ViewHolder holder, int position) {
        MessageModel message = messageList.get(position);

        // Populate message content and timestamp
        holder.messageText.setText(message.getMessage());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy · hh:mm a");
        holder.timestampText.setText(dateFormat.format(message.getDate()));
    }

    @Override
    public int getItemViewType(int position) {

        MessageModel message = messageList.get(position);
        int senderID = message.getSenderID();

        // Compare senderID with currentUserID
        if (senderID == otherUserID) {
            return 2; // Incoming message
        } else {
            return 1; // Outgoing message
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}