package com.example.uniride;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeChatMessageActivity extends AppCompatActivity {


    CircleImageView pfpImage;
    TextView nameTitleText;

    private int chatID;
    private int otherUserID;
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
        otherUserID = i.getIntExtra("otherUserID", 0);

        pfpImage = findViewById(R.id.pfpImage);
        nameTitleText = findViewById(R.id.nameTitleText);
        inputText = findViewById(R.id.inputText);
        recyclerView = findViewById(R.id.recyclerView);

        // Loads username and pfpImage of otherUser
        loadOtherUserData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MyHomeChatMessageAdapter(this, messageList, otherUserID);
        recyclerView.setAdapter(adapter);

        loadMessages();
    }

    private void loadMessages() {
        db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
            .orderBy("date", Query.Direction.ASCENDING)
            .whereEqualTo("chatID", chatID)
            .get()
            .addOnSuccessListener(messagesSnapshot -> {
                Log.d("HomeChatMessageActivity", "Messages from " + chatID + " : " + messagesSnapshot.size());
                messageList.clear();

                for (QueryDocumentSnapshot messageDoc : messagesSnapshot) {
                    MessageModel message = MessageModel.fromMap(messageDoc.getData());
                    messageList.add(message);
                }

                //Collections.sort(messageList, (m1, m2) -> m1.getDate().compareTo(m2.getDate()));
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(HomeChatMessageActivity.this,
                        "Error fetching messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("HomeChatMessagectivity", "Error fetching messages: " + e.getMessage());
            });
    }

    private void loadOtherUserData() {
        // Fetch the other user data based on otherUserID
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
            .whereEqualTo("userID", otherUserID) // Adjust the query if needed
            .get()
            .addOnSuccessListener(usersSnapshot -> {
                for (QueryDocumentSnapshot userDoc : usersSnapshot) {
                    UserModel otherUser = UserModel.fromMap(userDoc.getData());
                    nameTitleText.setText(otherUser.getName());
                    pfpImage.setImageResource(otherUser.getPfp());
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(HomeChatMessageActivity.this,
                    "Error fetching otherUserData: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("HomeChatMessageActivity", "Error fetching otherUserData: " + e.getMessage());
            });
    }

    public void backBtnPressed(View view) {
        finish();
    }

    public void sendBtnPressed(View view) {
        String messageText = inputText.getText().toString().trim();

        if (!messageText.isEmpty()) {
            // Get the userID
            db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(mAuth.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int userID = ((Long) documentSnapshot.get("userID")).intValue();
                        MessageModel newMessage = new MessageModel(chatID, userID, otherUserID, messageText, new Date());

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

    @Override
    public void finish() {
        super.finish();
        setResult(RESULT_OK);
    }
}
