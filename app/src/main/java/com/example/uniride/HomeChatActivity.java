package com.example.uniride;

import android.util.Log;
import android.os.Bundle;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class HomeChatActivity extends BottomNavigationActivity {

    private RecyclerView recyclerView;
    private MyHomeChatAdapter adapter;
    private List<MessageModel> chatList;
    private List<MessageModel> messageList;
    private int userID;
    private HashMap<Integer, MessageModel> chatMap;
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
        messageList = new ArrayList<>();
        chatMap = new HashMap<>();
        adapter = new MyHomeChatAdapter(this, chatList, messageList, userID);
        recyclerView.setAdapter(adapter);

        //HomeChatGenerateActivity generate = new HomeChatGenerateActivity();
        //generate.sendMessage(60002, "Hi, can you see this?", 30003);

        // Load chats
        loadChats();
    }

    private void loadChats() {
        chatList.clear();
        messageList.clear();

        // Get userId
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
            .document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    int userID = ((Long) documentSnapshot.get("userID")).intValue();
                    this.userID = userID;
                    Log.d("HomeChatActivity", "userID: " + userID);

                    // Get all messages
                    db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {

                            Log.d("HomeChatActivity", "Number of messages : " + querySnapshot.size());
                            for (QueryDocumentSnapshot document : querySnapshot) {

                                Object senderIDObject = document.get("senderID");
                                Object recipientIDObject = document.get("recipientID");
                                int senderID = 0;
                                int recipientID = 0;

                                if (senderIDObject instanceof String) {
                                    senderID = Integer.parseInt((String) senderIDObject);
                                } else if (senderIDObject instanceof Long) {
                                    senderID = ((Long) senderIDObject).intValue();
                                }

                                if (recipientIDObject instanceof String) {
                                    recipientID = Integer.parseInt((String) recipientIDObject);
                                } else if (recipientIDObject instanceof Long) {
                                    recipientID = ((Long) recipientIDObject).intValue();
                                }

                                // Generate messageList (messages from/for the user)
                                if (senderID == userID || recipientID == userID) {
                                    MessageModel message = MessageModel.fromMap(document.getData());
                                    messageList.add(message);
                                    Log.d("HomeChatActivity", "Message Added #" + messageList.size());

                                    // Create chatList (latest messages for each pair)
                                    int chatKey = (senderID == userID) ? recipientID : senderID;

                                    // Add only the latest message for each chatKey
                                    if (!chatMap.containsKey(chatKey)) {
                                        chatMap.put(chatKey, message);
                                    }
                                }
                            }

                            // Convert HashMap to a List of latest messages for the chat list
                            chatList = new ArrayList<>(chatMap.values());
                            Log.d("HomeChatActivity", "Number of unique chats: " + chatList.size());
                            Log.d("HomeChatActivity", "ChatList: " + chatList);

                            int[] populatedCount = {0};
                            // Update adapter
                            for (MessageModel message : messageList) {
                                message.populateObjects(db, populatedMessage -> {
                                    populatedCount[0]++;  // Increment the counter when each message is populated

                                    // Check if all population tasks are complete
                                    if (populatedCount[0] == messageList.size()) {
                                        adapter.notifyDataSetChanged();  // Notify the adapter after all messages are populated
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(HomeChatActivity.this,
                                    "Error loading chats: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("HomeBookingActivity", "Error loading chats: " + e.getMessage());
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(HomeChatActivity.this,
                    "Error fetching user data: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
                Log.d("HomeBookingActivity", "Error fetching user data: " + e.getMessage());
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadChats();
        }
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.passenger;
    }
}