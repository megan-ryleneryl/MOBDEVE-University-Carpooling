package com.example.uniride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private Context context;
    private List<MessageModel> messageList;

    public MyHomeChatMessageAdapter(Context context, List<MessageModel> messageList) {
        this.context = context;
        this.messageList = messageList;
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

        // Inflate outgoing or incoming message layout based on viewType
        if (viewType == 1) {
            view = inflater.inflate(R.layout.home_message_outgoing_item, parent, false);
        } else {
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
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String senderID = String.valueOf(message.getSenderID());

        // Compare senderID with currentUserID
        if (senderID.equals(currentUserID)) {
            return 1; // Outgoing message
        } else {
            return 2; // Incoming message
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}