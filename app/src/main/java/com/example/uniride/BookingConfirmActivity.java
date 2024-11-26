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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

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
            FirebaseFirestore db = FirebaseFirestore.getInstance();
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

                    RideModel ride = bookingToSave.getRide();

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

                    contactBtn.setOnClickListener(v -> {
                        //Toast.makeText(BookingConfirmActivity.this, "Move to chat", Toast.LENGTH_SHORT).show();

                        // TODO: Coordinate on how to implement this
                        // To be implemented on MCO3:
                        //   IF there is no existing chat with the driver
                        //   THEN automatically create a chat and add it to the chat list
                        //   Move to message activity

                        // Hardcoded to Barbie Smith's chat for now:
                        Intent i = new Intent(BookingConfirmActivity.this, HomeChatMessageActivity.class);
                        i.putExtra("chatID", 70001);
                        i.putExtra("chatmate", DataGenerator.user2.getName());
                        startActivity(i);
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