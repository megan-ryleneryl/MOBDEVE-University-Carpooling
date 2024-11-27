package com.example.uniride;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Activity to display the confirmation screen for a booking.
 */
public class BookingConfirmActivity extends BottomNavigationActivity {
    TextView passengerNameTv;
    TextView pickupTv;
    TextView dropoffTv;
    TextView driverNameTv;
    TextView priceTv;
    TextView vehicleTv;
    TextView dateTv;
    TextView timeTv;
    TextView arrivalTv;
    Button homeBtn;
    Button contactBtn;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    int userID = 0;
    int otherUserID = 0;
    int chatID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.booking_confirm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve Intent data
        BookingModel bookingToSave = (BookingModel) getIntent().getSerializableExtra("bookingToSave");

        if (bookingToSave != null) {
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            bookingToSave.populateObjects(db, new BookingModel.OnPopulateCompleteListener() {
                @Override
                public void onPopulateComplete(BookingModel booking) {
                    passengerNameTv = findViewById(R.id.passengerNameTv);
                    pickupTv = findViewById(R.id.pickupTv);
                    dropoffTv = findViewById(R.id.dropoffTv);
                    driverNameTv = findViewById(R.id.driverNameTv);
                    priceTv = findViewById(R.id.priceTv);
                    vehicleTv = findViewById(R.id.vehicleTv);
                    dateTv = findViewById(R.id.dateTv);
                    timeTv = findViewById(R.id.timeTv);
                    arrivalTv = findViewById(R.id.arrivalTv);
                    homeBtn = findViewById(R.id.homeBtn);
                    contactBtn = findViewById(R.id.contactBtn);

                    Log.d("CodeDebug", bookingToSave.toString());

                    otherUserID = bookingToSave.getRide().getDriverID();

                    RideModel ride = bookingToSave.getRide();
                    String departureDate = bookingToSave.getDate();
                    String departureTime = ride.getDepartureTime();
                    String pickup = ride.getFrom().getName();
                    String dropoff = ride.getTo().getName();

                    passengerNameTv.setText(bookingToSave.getPassenger().getName());
                    pickupTv.setText(ride.getFrom().getName());
                    dropoffTv.setText(ride.getTo().getName());
                    driverNameTv.setText(ride.getDriver().getName());
                    priceTv.setText("P" + ride.getPrice());
                    vehicleTv.setText(ride.getDriver().getCar().getMake() + " " + ride.getDriver().getCar().getModel() + " - " + ride.getDriver().getCar().getPlateNumber());
                    dateTv.setText(bookingToSave.getDate());
                    timeTv.setText(ride.getDepartureTime());
                    arrivalTv.setText(ride.getArrivalTime());

                    homeBtn.setOnClickListener(v -> {
                        Toast.makeText(BookingConfirmActivity.this, "Return to home", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), BookingHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    });

                    getOnBackPressedDispatcher().addCallback(BookingConfirmActivity.this, new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            Toast.makeText(BookingConfirmActivity.this, "Return to home", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), BookingHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                    contactBtn.setOnClickListener(v -> {
                        // Check if the user has chat history with the driver
                        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                            .document(currentUser.getUid())
                            .get()
                            .addOnSuccessListener(userSnapshot -> {
                                if (userSnapshot.exists()) {
                                    userID = ((Long) userSnapshot.get("userID")).intValue();

                                    // Get all messages
                                    db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
                                        .get()
                                        .addOnSuccessListener(messagesSnapshot -> {

                                            // Check each message if it matches userID and otherUserID
                                            int lastChatID = 0;
                                            for (QueryDocumentSnapshot messageDoc : messagesSnapshot) {
                                                MessageModel message = MessageModel.fromMap(messageDoc.getData());

                                                int senderID = message.getSenderID();
                                                int recipientID = message.getRecipientID();
                                                lastChatID = Math.max(lastChatID, message.getChatID());

                                                // Get the chatID if there is
                                                if ((senderID == userID && recipientID == otherUserID) || (senderID == otherUserID && recipientID == userID)) {
                                                    chatID = message.getChatID();
                                                    Log.d("BookingConfirmActivity", "chatID identified: " + chatID);
                                                    break;
                                                }
                                            }

                                            // Generate chat
                                            if (chatID == 0) {
                                                chatID = lastChatID + 1;
                                            }
                                            ChatGenerator generate = new ChatGenerator();
                                            generate.sendMessage(chatID,
                                                "> BOOKING REQUEST SUBMITTED <\n" +
                                                    "\uD83D\uDE97 Date: " + departureDate + "\n" +
                                                    "\uD83D\uDE97 Time: " + departureTime + "\n" +
                                                    "\uD83D\uDE97 Pickup: " + pickup + "\n" +
                                                    "\uD83D\uDE97 Dropoff: " + dropoff + "\n\n" +
                                                    "I will review your request ASAP!\n\n" +
                                                    "(This message was generated by the app. 🤖)",
                                                    otherUserID,
                                                    userID);
                                            // Move to HomeChatMessage activity
                                            Intent i = new Intent(BookingConfirmActivity.this, HomeChatMessageActivity.class);
                                            Log.d("BookingConfirmActivity", "chatID to be passed " + chatID);
                                            Log.d("BookingConfirmActivity", "otherUserID to be passed " + otherUserID);
                                            i.putExtra("chatID", chatID);
                                            i.putExtra("otherUserID", otherUserID);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                            startActivity(i);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(BookingConfirmActivity.this,
                                                "Error loading messages: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                            Log.d("BookingConfirmActivity", "Error loading messages: " + e.getMessage());
                                        });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(BookingConfirmActivity.this,
                                        "Error loading users: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                Log.d("BookingConfirmActivity", "Error loading users: " + e.getMessage());
                            });
                    });
                }
            });
        }
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.home;
    }
}