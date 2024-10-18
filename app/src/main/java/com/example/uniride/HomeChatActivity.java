package com.example.uniride;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeChatActivity extends BottomNavigationActivity {

    private RecyclerView recyclerView;
    private MyHomeChatAdapter adapter;
    private ArrayList<MessageModel> chatData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_chat);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatData = DataGenerator.loadMessageData();

        adapter = new MyHomeChatAdapter(chatData, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.passenger;
    }
}