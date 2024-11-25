package com.example.uniride;

import android.os.Bundle;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.ArrayList;

public class HomeChatActivity extends BottomNavigationActivity {

    private RecyclerView recyclerView;
    private MyHomeChatAdapter adapter;
    private List<MessageModel> chatList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chat);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        adapter = new MyHomeChatAdapter(this, chatList);
        recyclerView.setAdapter(adapter);

        //HomeChatGenerateActivity generate = new HomeChatGenerateActivity();
        //generate.sendMessage(60001, "Hi, can you see this?", 30002);

        // Load chats
        loadChats();
    }

    private void loadChats() {
        db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    chatList.clear();
                    List<MessageModel> allMessages = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        MessageModel message = MessageModel.fromMap(document.getData());
                        allMessages.add(message);
                    }

                    // Filter for distinct chats and notify adapter
                    chatList.addAll(MyHomeChatAdapter.getUniqueChats(allMessages));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeChatActivity.this,
                            "Error loading chats: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.passenger;
    }
}