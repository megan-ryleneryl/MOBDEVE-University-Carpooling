package com.example.uniride;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import android.util.Log;

public class HomeChatMessageActivity extends AppCompatActivity {

    int chatID;
    UserModel currentUser = DataGenerator.loadUserData().get(0);
    UserModel chatmate;
    EditText inputText;
    TextView nameTitleText;
    CircleImageView pfpImage;
    private RecyclerView recyclerView;
    private MyHomeChatMessageAdapter adapter;
    private ArrayList<UserModel> userData;
    private ArrayList<MessageModel> myChatData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_chat_message);

        nameTitleText = findViewById(R.id.nameTitleText);
        pfpImage = findViewById(R.id.pfpImage);
        inputText = findViewById(R.id.inputText);
        recyclerView = findViewById(R.id.recyclerView);

        Intent i = getIntent();
        chatID = i.getIntExtra("chatID", 0);
        String nameTitle = i.getStringExtra("chatmate");

        userData = DataGenerator.loadUserData();
        nameTitleText.setText(nameTitle);
        for (UserModel user : userData) {
            if (user.getName().equals(nameTitle)) {
                chatmate = user;
                pfpImage.setImageResource(user.getPfp());
                break;
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<MessageModel> chatData = DataGenerator.loadMessageData();
        myChatData = new ArrayList<>();

        // Filtered down to a specific chatID
        for (MessageModel message : chatData) {
            if (message.getChatID() == chatID) {
                myChatData.add(message);
            }
        }

        // Sorted by date
        Collections.sort(myChatData, new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel msg2, MessageModel msg1) {
                return msg2.getDate().compareTo(msg1.getDate());
            }
        });

        adapter = new MyHomeChatMessageAdapter(myChatData, chatID, this);
        recyclerView.setAdapter(adapter);
    }

    public void backBtnPressed(View view) {
        finish();
    }

    public void sendBtnPressed(View view) {
        String messageText = inputText.getText().toString();

        if (!messageText.isEmpty()) {
            MessageModel message = new MessageModel(chatID, currentUser, chatmate, messageText, new Date());
            myChatData.add(message);
            adapter.updateData(myChatData);
            adapter.notifyItemInserted(myChatData.size() - 1);
            recyclerView.post(() -> recyclerView.scrollToPosition(myChatData.size() - 1));
            inputText.setText("");
        }
    }
}