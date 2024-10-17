package com.example.uniride;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookingConfirmActivity extends AppCompatActivity {
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.booking_search_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve Intent data
        BookingModel selectedBooking = (BookingModel) getIntent().getSerializableExtra("selectedBooking");

        if (selectedBooking != null) {
            // TODO: Add new booking to datebase

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

            // TODO: Implement username passing
            passengerNameTv.setText("Luke Aniago");
            pickupTv.setText(selectedBooking.getRide().getFrom().getName());
            dropoffTv.setText(selectedBooking.getRide().getTo().getName());
            driverNameTv.setText(selectedBooking.getRide().getDriver().getName());
            priceTv.setText("P" + selectedBooking.getRide().getPrice());
            vehicleTv.setText(selectedBooking.getRide().getDriver().getCar().getMake() + " " + selectedBooking.getRide().getDriver().getCar().getModel() + " - " + selectedBooking.getRide().getDriver().getCar().getPlateNumber());
            // TODO: Implement date passing
            dateTv.setText("10-10-1010");
            timeTv.setText(selectedBooking.getRide().getDepartureTime());
            arrivalTv.setText(selectedBooking.getRide().getArrivalTime());

            homeBtn.setOnClickListener(v -> {
                Toast.makeText(context, "Returning home", Toast.LENGTH_SHORT).show();
                finish();
            });

            contactBtn.setOnClickListener(v -> {
                Toast.makeText(context, "Move to chat", Toast.LENGTH_SHORT).show();

                // TODO: Coordinate on how to implement this
//                Intent i = new Intent(BookingConfirmActivity.this, HomeChatActivity.class);
//                i.putExtra("selectedBooking", selectedBooking);
//                startActivity(i);
            });
        }
    }
}