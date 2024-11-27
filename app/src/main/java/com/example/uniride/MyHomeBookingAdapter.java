package com.example.uniride;

import android.util.Log;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyHomeBookingAdapter extends RecyclerView.Adapter<MyHomeBookingAdapter.ViewHolder> {

    List<BookingModel> bookingList;
    String bookingType; // scheduled/requests/accepted
    HomeBookingActivity activity;

    String departureDate;
    String departureTime;
    String pickup;
    String dropoff;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    int userID = 0;
    int otherUserID = 0;
    int chatID = 0;
    int rideID = 0;

    public MyHomeBookingAdapter(List<BookingModel> bookingList, String bookingType, HomeBookingActivity activity) {
        this.bookingList = bookingList;
        this.bookingType = bookingType;

        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView pfpImage;
        TextView passengerText;
        TextView bookingIDText;
        TextView dateText;
        TextView statusText;
        Button acceptButton;
        Button rejectButton;
        Button cancelBookingButton;
        Button onTheWayButton;
        Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pfpImage = itemView.findViewById(R.id.pfpImage);
            passengerText = itemView.findViewById(R.id.passengerText);
            bookingIDText = itemView.findViewById(R.id.bookingIDText);
            dateText = itemView.findViewById(R.id.dateText);
            statusText = itemView.findViewById(R.id.statusText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            cancelBookingButton = itemView.findViewById(R.id.cancelBookingButton);
            onTheWayButton = itemView.findViewById(R.id.onTheWayButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
    @NonNull
    @Override
    public MyHomeBookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (bookingType.equals("accepted")) {
            view = layoutInflater.inflate(R.layout.home_booking_accepted_item, parent, false);
        } else if (bookingType.equals("requests")){
            view = layoutInflater.inflate(R.layout.home_booking_requests_item, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.home_booking_scheduled_item, parent, false);
        }
        MyHomeBookingAdapter.ViewHolder viewHolder = new MyHomeBookingAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHomeBookingAdapter.ViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);

        if (booking.getPassenger() != null) {
            // Normal binding
            holder.pfpImage.setImageResource(booking.getPassenger().getPfp());
            holder.passengerText.setText(booking.getPassenger().getName());
            holder.bookingIDText.setText(String.valueOf(booking.getBookingID()));

            if (!bookingType.equals("scheduled")) {
                holder.dateText.setText(formatDate(booking.getDate()));
            }
            if (!bookingType.equals("requests")) {
                holder.statusText.setText(booking.isPaymentComplete() ? "Finished" : "Pending");
            }

            setListeners(holder, position);
        } else {
            // Populate objects and refresh the item when data is available
            booking.populateObjects(FirebaseFirestore.getInstance(), populatedBooking -> {
                bookingList.set(position, populatedBooking);
                notifyItemChanged(position); // Refresh only this item
            });
        }
    }


    @Override
    public int getItemCount() { return bookingList.size(); }

    private void setListeners(@NonNull MyHomeBookingAdapter.ViewHolder holder, int position) {
        if (bookingType.equals("requests")) {
            holder.acceptButton.setOnClickListener(v -> {
                showConfirmationDialog("accept", position);
            });
            holder.rejectButton.setOnClickListener(v -> {
                showConfirmationDialog("reject", position);
            });
        } else if (bookingType.equals("accepted")) {
            holder.cancelBookingButton.setOnClickListener(v -> {
                showConfirmationDialog("cancelBooking", position);
            });
        } else if (bookingType.equals("scheduled")) {
            holder.onTheWayButton.setOnClickListener(v -> {
                showConfirmationDialog("onTheWay", position);
            });
            holder.cancelButton.setOnClickListener(v -> {
                showConfirmationDialog("cancel", position);
            });
        }
    }

    private void showConfirmationDialog(String code, int position) {
        Drawable icon = activity.getResources().getDrawable(android.R.drawable.ic_dialog_alert, null);
        icon.setTint(ContextCompat.getColor(activity, R.color.primary));
        AlertDialog dialog = null;

        BookingModel booking = bookingList.get(position); // Retrieve the booking object at this position
        otherUserID = booking.getPassengerID();
        int bookingID = booking.getBookingID();

        if (code.equals("accept")) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle("Accept Booking Request")
                .setMessage("Are you sure you want to ACCEPT the booking request?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                            .whereEqualTo("bookingID", bookingID)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {

                                // Generate App Message
                                for (QueryDocumentSnapshot bookingDoc : querySnapshot) {
                                    BookingModel booking = BookingModel.fromMap(bookingDoc.getData());
                                    departureDate = booking.getDate();
                                    rideID = booking.getRideID();
                                    generateAppMessage(code, bookingID);
                                }

                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                    String documentID = documentSnapshot.getId();

                                    // Update to is Accepted
                                    db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                                        .document(documentID)
                                        .update("isAccepted", true)
                                        .addOnSuccessListener(aVoid -> {
                                            for (int i = 0; i < bookingList.size(); i++) {
                                                if (bookingList.get(i).getBookingID() == bookingID) {
                                                    bookingList.remove(i);
                                                    break;
                                                }
                                            }
                                            notifyDataSetChanged();
                                            activity.updateUI();
                                            Toast.makeText(activity, "Booking accepted!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(activity, "Failed to accept booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                } else {
                                    // No document found with the given bookingID
                                    Toast.makeText(activity, "Booking not found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(icon)
                .show();
        } else if (code.equals("reject")) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle("Reject Booking Request")
                .setMessage("Are you sure you want to REJECT the booking request?")
                .setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                                .whereEqualTo("bookingID", bookingID)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {

                                    // Generate App Message
                                    for (QueryDocumentSnapshot bookingDoc : querySnapshot) {
                                        BookingModel booking = BookingModel.fromMap(bookingDoc.getData());
                                        departureDate = booking.getDate();
                                        rideID = booking.getRideID();
                                        generateAppMessage(code, bookingID);
                                    }

                                    // Delete booking
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();

                                        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                                                .document(documentID)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    for (int i = 0; i < bookingList.size(); i++) {
                                                        if (bookingList.get(i).getBookingID() == bookingID) {
                                                            bookingList.remove(i);
                                                            break;
                                                        }
                                                    }
                                                    notifyDataSetChanged();
                                                    activity.updateUI();
                                                    Toast.makeText(activity, "Booking rejected.", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(activity, "Failed to reject booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        // No document found with the given bookingID
                                        Toast.makeText(activity, "Booking not found", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(icon)
                .show();
        } else if (code.equals("onTheWay")) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle("On The Way!")
                .setMessage("Are you sure you want to START the ride?")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO
                        generateAppMessage(code, bookingID);
                        Intent i = new Intent(activity, RideTracking.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(icon)
                .show();
        } else if (code.equals("cancelBooking") || code.equals("cancel")) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to CANCEL the booking?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                                .whereEqualTo("bookingID", bookingID)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {

                                    // Generate App Message
                                    for (QueryDocumentSnapshot bookingDoc : querySnapshot) {
                                        BookingModel booking = BookingModel.fromMap(bookingDoc.getData());
                                        departureDate = booking.getDate();
                                        rideID = booking.getRideID();
                                        generateAppMessage(code, bookingID);
                                    }

                                    // Delete booking
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();

                                        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                                                .document(documentID)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    for (int i = 0; i < bookingList.size(); i++) {
                                                        if (bookingList.get(i).getBookingID() == bookingID) {
                                                            bookingList.remove(i);
                                                            break;
                                                        }
                                                    }
                                                    notifyDataSetChanged();
                                                    activity.updateUI();
                                                    Toast.makeText(activity, "Booking cancelled.", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(activity, "Failed to cancel booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        // No document found with the given bookingID
                                        Toast.makeText(activity, "Booking not found", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(icon)
                .show();
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.unselected_color));

// Change the text color of the PositiveButton (optional)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.unselected_color));
    }

    public void generateAppMessage(String code, int bookingID) {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        userID = ((Long) userSnapshot.get("userID")).intValue();

                        db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                                .whereEqualTo("rideID", rideID)
                                .get()
                                .addOnSuccessListener(ridesSnapshot -> {
                                    for (QueryDocumentSnapshot rideDoc : ridesSnapshot) {
                                        RideModel ride = RideModel.fromMap(rideDoc.getData());

                                        // Populate the ride objects first
                                        ride.populateObjects(db, populatedRide -> {
                                            departureTime = populatedRide.getDepartureTime();
                                            pickup = populatedRide.getFrom().getName();
                                            dropoff = populatedRide.getTo().getName();
                                            Log.d("MyHomeBookingAdapter", "departureTime: " + departureTime);
                                            Log.d("MyHomeBookingAdapter", "pickup: " + pickup);
                                            Log.d("MyHomeBookingAdapter", "dropoff: " + dropoff);

                                            // Now get messages after ride data is populated
                                            db.collection(MyFirestoreReferences.MESSAGES_COLLECTION)
                                                    .get()
                                                    .addOnSuccessListener(messagesSnapshot -> {
                                                        int lastChatID = 0;
                                                        for (QueryDocumentSnapshot messageDoc : messagesSnapshot) {
                                                            MessageModel message = MessageModel.fromMap(messageDoc.getData());

                                                            int senderID = message.getSenderID();
                                                            int recipientID = message.getRecipientID();
                                                            lastChatID = Math.max(lastChatID, message.getChatID());

                                                            if ((senderID == userID && recipientID == otherUserID) ||
                                                                    (senderID == otherUserID && recipientID == userID)) {
                                                                chatID = message.getChatID();
                                                                break;
                                                            }
                                                        }

                                                        if (chatID == 0) {
                                                            chatID = lastChatID + 1;
                                                        }

                                                        ChatGenerator generate = new ChatGenerator();
                                                        String message = "";

                                                        if (code.equals("accept")) {
                                                            message = "> BOOKING REQUEST ACCEPTED <\n" +
                                                                    "\uD83D\uDE97 Date: " + departureDate + "\n" +
                                                                    "\uD83D\uDE97 Time: " + departureTime + "\n" +
                                                                    "\uD83D\uDE97 Pickup: " + pickup + "\n" +
                                                                    "\uD83D\uDE97 Dropoff: " + dropoff + "\n\n" +
                                                                    "I accepted your booking request. See you soon!\n\n" +
                                                                    "(This message was generated by the app. 🤖)";
                                                        } else if (code.equals("reject")) {
                                                            message = "> BOOKING REQUEST REJECTED <\n" +
                                                                    "\uD83D\uDE97 Date: " + departureDate + "\n" +
                                                                    "\uD83D\uDE97 Time: " + departureTime + "\n" +
                                                                    "\uD83D\uDE97 Pickup: " + pickup + "\n" +
                                                                    "\uD83D\uDE97 Dropoff: " + dropoff + "\n\n" +
                                                                    "Sorry, I won't be able to accept your booking.\n\n" +
                                                                    "(This message was generated by the app. 🤖)";
                                                        } else if (code.equals("cancelBooking") || code.equals("cancel")) {
                                                            message = "> SCHEDULED BOOKING WAS CANCELLED <\n" +
                                                                    "\uD83D\uDE97 Date: " + departureDate + "\n" +
                                                                    "\uD83D\uDE97 Time: " + departureTime + "\n" +
                                                                    "\uD83D\uDE97 Pickup: " + pickup + "\n" +
                                                                    "\uD83D\uDE97 Dropoff: " + dropoff + "\n\n" +
                                                                    "Sorry, I had to cancel the booking.\n\n" +
                                                                    "(This message was generated by the app. 🤖)";
                                                        } else if (code.equals("onTheWay")) {
                                                            message = "> DRIVER IS ON THE WAY <\n" +
                                                                    "\uD83D\uDE97 Date: " + departureDate + "\n" +
                                                                    "\uD83D\uDE97 Time: " + departureTime + "\n" +
                                                                    "\uD83D\uDE97 Pickup: " + pickup + "\n" +
                                                                    "\uD83D\uDE97 Dropoff: " + dropoff + "\n\n" +
                                                                    "I am currently heading to the pickup location. See you soon!\n\n" +
                                                                    "(This message was generated by the app. 🤖)";
                                                        }

                                                        generate.sendMessage(chatID, message, userID, otherUserID);
                                                    });
                                        });
                                    }
                                });
                    }
                });
    }

    public static String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-M-d");
            Date date = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy");
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
