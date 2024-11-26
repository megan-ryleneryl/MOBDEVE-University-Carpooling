package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class HomeChatMessageActivity extends AppCompatActivity {

    private int chatID;
    private RecyclerView recyclerView;
    private MyHomeChatMessageAdapter adapter;
    private List<MessageModel> messageList;
    private EditText inputText;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chat_message);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        chatID = i.getIntExtra("chatID", 0);

        inputText = findViewById(R.id.inputText);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MyHomeChatMessageAdapter(this, messageList);
        recyclerView.setAdapter(adapter);

        loadMessages();
    }

    private void loadMessages() {
        db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
                .whereEqualTo("chatID", chatID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    messageList.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        MessageModel message = MessageModel.fromMap(document.getData());
                        messageList.add(message);
                    }

                    Collections.sort(messageList, (m1, m2) -> m1.getDate().compareTo(m2.getDate()));
                    adapter.notifyDataSetChanged();
                });
    }

    public void sendBtnPressed(View view) {
        String messageText = inputText.getText().toString().trim();

        if (!messageText.isEmpty()) {
            // Get the senderID
            db.collection(MyFirestoreReferences.USERS_COLLECTION)
                    .document(mAuth.getUid())  // Get the current user's UID
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Fetch the userID
                            int senderID = ((Long) documentSnapshot.get("userID")).intValue(); // Assuming userID is stored as Long in Firestore

                            // Create and send the message
                            MessageModel newMessage = new MessageModel(chatID, senderID, 0, messageText, new Date());

                            // Add the new message to the Firestore collection
                            db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
                                    .add(newMessage.toMap())
                                    .addOnSuccessListener(documentReference -> {
                                        inputText.setText("");
                                        loadMessages();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the failure
                                        Log.e("sendBtnPressed", "Error sending message", e);
                                    });
                        } else {
                            Log.e("sendBtnPressed", "User data not found in Firestore");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle error fetching user data
                        Log.e("sendBtnPressed", "Error fetching user data", e);
                    });
        }
    }

    // Fetch the last chatID (find the highest chatID)
    private void getLastChatID(OnLastChatIDFetchedListener listener) {
        db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
                .orderBy("chatID", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sort by chatID in descending order
                .limit(1) // Only get the first (latest) chat document
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int lastChatID = 0;
                    // Use a for loop to access the first document
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        lastChatID = document.getLong("chatID").intValue(); // Extract chatID
                    }
                    listener.onLastChatIDFetched(lastChatID);
                })
                .addOnFailureListener(e -> {
                    Log.e("getLastChatID", "Error fetching last chatID", e);
                    listener.onLastChatIDFetched(0); // Default to 0 if there's an error
                });
    }

    // Listener interface to handle chatID fetch completion
    public interface OnLastChatIDFetchedListener {
        void onLastChatIDFetched(int lastChatID);
    }
}
