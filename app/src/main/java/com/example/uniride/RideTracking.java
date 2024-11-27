package com.example.uniride;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RideTracking extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String fromLocationName = "";
    private String toLocationName = "";
    private SupportMapFragment mapFragment;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading); // Show a loading screen initially

        // Initialize Volley RequestQueue for API calls
        requestQueue = Volley.newRequestQueue(this);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Check the bookings collection for today's bookings
        fetchBookings();
    }

    private void initializeMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            Log.d("MapDebug", "Getting map async");
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapDebug", "Map fragment is null");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MapDebug", "Map is ready");
        mMap = googleMap;

        // Enable map controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Set initial camera position to Metro Manila
        LatLng metroManila = new LatLng(14.5995, 120.9842);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(metroManila, 11));

        // If we already have location names, update the map
        if (!fromLocationName.isEmpty() && !toLocationName.isEmpty()) {
            updateMapWithLocations();
        }
    }

    private void fetchBookings() {
        // Get today's date in the format matching your Firestore date format
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Get the current authenticated user's UID
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the user's userID from the users collection
        db.collection("users")
                .document(currentUserUID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Long userID = task.getResult().getLong("userID");

                        if (userID != null) {
                            // Query bookings where passengerID matches the user's userID
                            queryBookings(userID, todayDate);
                        } else {
                            Toast.makeText(this, "User ID not found in users collection.", Toast.LENGTH_SHORT).show();
                            // No matching bookings found, show a different screen
                            navigateToNoBookingsScreen();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show();
                        // No matching bookings found, show a different screen
                        navigateToNoBookingsScreen();
                    }
                });
    }

    private void queryBookings(Long userID, String todayDate) {
        // Query the bookings collection for today's bookings
        db.collection("bookings")
                .whereEqualTo("isAccepted", true)
                .whereEqualTo("date", todayDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean isRideFound = false;

                        for (DocumentSnapshot document : task.getResult()) {
                            Long passengerID = document.getLong("passengerID");
                            if (userID.equals(passengerID)) {
                                isRideFound = true;

                                // Get the rideID from the booking document
                                Long rideID = document.getLong("rideID");
                                String date = document.getString("date");
                                fetchRideDetails(rideID, date, userID);

                                setContentView(R.layout.activity_ride_tracking); // Load the main screen
                                // Initialize map
                                initializeMap();
                                setupChatButton();
                            }
                        }

                        if (!isRideFound) {
                            // No ride found for the current user
                            Toast.makeText(this, "No rides found for your account today.", Toast.LENGTH_SHORT).show();
                            // No matching bookings found, show a different screen
                            navigateToNoBookingsScreen();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch bookings.", Toast.LENGTH_SHORT).show();
                        // No matching bookings found, show a different screen
                        navigateToNoBookingsScreen();
                    }
                });
    }

    // A map of hardcoded location names to coordinates (in LatLng format)
    private static final Map<String, LatLng> locationCoordinates = new HashMap<String, LatLng>() {{
        put("ADMU", new LatLng(14.6093, 121.1095));
        put("Antipolo", new LatLng(14.5880, 121.1775));
        put("Bacoor", new LatLng(14.4333, 120.9833));
        put("Binondo", new LatLng(14.5995, 120.9762));
        put("Biñan", new LatLng(14.3133, 121.0783));
        put("Cainta", new LatLng(14.5765, 121.1280));
        put("Caloocan", new LatLng(14.6190, 120.9670));
        put("Cavite City", new LatLng(14.4792, 120.9147));
        put("DLSU", new LatLng(14.564847136369453, 120.9931683841155));
        put("Dasmariñas", new LatLng(14.3143, 120.9382));
        put("Imus", new LatLng(14.4196, 120.9309));
        put("Las Piñas", new LatLng(14.4386, 120.9895));
        put("Makati", new LatLng(14.5547, 121.0244));
        put("Malabon", new LatLng(14.6549, 120.9609));
        put("Mandaluyong", new LatLng(14.5739, 121.0220));
        put("Manila", new LatLng(14.5995, 120.9842));
        put("Marikina", new LatLng(14.6500, 121.0833));
        put("Montalban", new LatLng(14.7246, 121.1350));
        put("Navotas", new LatLng(14.6500, 120.9457));
        put("Parañaque", new LatLng(14.5000, 121.0153));
        put("Pasig", new LatLng(14.5760, 121.0855));
        put("Pateros", new LatLng(14.5307, 121.0802));
        put("Quezon City", new LatLng(14.6760, 121.0437));
        put("San Juan", new LatLng(14.6000, 121.0300));
        put("San Pedro", new LatLng(14.3226, 121.0566));
        put("Santa Rosa", new LatLng(14.3129, 121.0856));
        put("Taguig", new LatLng(14.5200, 121.0500));
        put("Taytay", new LatLng(14.5703, 121.1192));
        put("UP", new LatLng(14.6532, 121.0629));
        put("UST", new LatLng(14.5993, 120.9841));
        put("Valenzuela", new LatLng(14.6789, 120.9739));
    }};


    private void fetchRideDetails(Long rideID, String date, Long userID) {
        // Query the rides collection to find a document where the "rideID" field matches the passed rideID
        db.collection("rides")
                .whereEqualTo("rideID", rideID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            Log.d("RideDetails", "Fetching ride details for rideID: " + rideID);

                            // Extract fields from the document
                            Long driverID = document.getLong("driverID");
                            Long fromLocationID = document.getLong("fromLocationID");
                            Long toLocationID = document.getLong("toLocationID");
                            Double price = document.getDouble("price");
                            String vehicle = "Car";  // Query the vehicle type if available in another collection
                            String departureTime = document.getString("departureTime");
                            String arrivalTime = document.getString("arrivalTime");

                            // Fetch the driver's name and passenger's name, and location names
                            fetchUserName(driverID, "driver", (driverName) -> {
                                fetchUserName(userID, "passenger", (passengerName) -> {
                                    fetchLocationName(fromLocationID, "from", (fetchedFromLocationName) -> {
                                        fromLocationName = fetchedFromLocationName;

                                        fetchLocationName(toLocationID, "to", (fetchedToLocationName) -> {
                                            toLocationName = fetchedToLocationName;

                                            // After all data is fetched, update the UI
                                            updateUI(passengerName, driverName, price, vehicle, departureTime, arrivalTime, date);
                                        });
                                    });
                                });
                            });
                        } else {
                            Toast.makeText(this, "No ride found with the given rideID.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch ride details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateMapWithLocations() {
        if (mMap == null) {
            Log.e("MapDebug", "Map is null in updateMapWithLocations");
            return;
        }

        LatLng fromCoordinates = locationCoordinates.get(fromLocationName);
        LatLng toCoordinates = locationCoordinates.get(toLocationName);

        if (fromCoordinates != null && toCoordinates != null) {
            Log.d("MapDebug", "Getting directions from " + fromLocationName + " to " + toLocationName);
            plotRouteOnMap(fromCoordinates, toCoordinates);
        } else {
            Log.e("MapDebug", "Missing coordinates for locations: " + fromLocationName + " or " + toLocationName);
        }
    }

    private void getDirections(LatLng origin, LatLng destination) {
        String url = String.format(Locale.US,
                "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=%f,%f&destination=%f,%f&key=%s",
                origin.latitude, origin.longitude,
                destination.latitude, destination.longitude,
                getString(R.string.MAPS_API_KEY));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String encodedPath = overviewPolyline.getString("points");

                            // Decode path and draw on map
                            List<LatLng> path = decodePolyline(encodedPath);
                            drawPathOnMap(path, origin, destination);
                        }
                    } catch (JSONException e) {
                        Log.e("DirectionsAPI", "Error parsing JSON", e);
                    }
                },
                error -> Log.e("DirectionsAPI", "Error fetching directions", error)
        );

        requestQueue.add(request);
    }

    private void drawPathOnMap(List<LatLng> path, LatLng origin, LatLng destination) {
        if (mMap == null) return;

        // Clear previous map elements
        mMap.clear();

        // Draw the route
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(path)
                .width(12)
                .color(Color.parseColor("#4CAF50"))  // Material Design Green
                .geodesic(true);
        mMap.addPolyline(polylineOptions);

        // Add markers
        mMap.addMarker(new MarkerOptions()
                .position(origin)
                .title("Pickup: " + fromLocationName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title("Dropoff: " + toLocationName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // Calculate bounds to show entire route
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng point : path) {
            boundsBuilder.include(point);
        }
        LatLngBounds bounds = boundsBuilder.build();

        // Move camera with padding
        int padding = (int) (getResources().getDisplayMetrics().density * 100); // 100dp padding
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> points = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            points.add(new LatLng(lat / 1E5, lng / 1E5));
        }
        return points;
    }

    private void updateUI(String passengerName, String driverName, Double price,
                          String vehicle, String departureTime, String arrivalTime, String date) {

        ((TextView) findViewById(R.id.passengerNameTv)).setText(passengerName);
        ((TextView) findViewById(R.id.driverNameTv)).setText(driverName);
        ((TextView) findViewById(R.id.pickupTv)).setText(fromLocationName);
        ((TextView) findViewById(R.id.dropoffTv)).setText(toLocationName);
        ((TextView) findViewById(R.id.priceTv)).setText(String.format(Locale.getDefault(), "₱%.2f", price));
        ((TextView) findViewById(R.id.vehicleTv)).setText(vehicle);
        ((TextView) findViewById(R.id.dateTv)).setText(date);
        ((TextView) findViewById(R.id.timeTv)).setText(departureTime);
        ((TextView) findViewById(R.id.arrivalTv)).setText(arrivalTime);

        // Get coordinates for locations
        LatLng fromCoordinates = locationCoordinates.get(fromLocationName);
        LatLng toCoordinates = locationCoordinates.get(toLocationName);

        if (fromCoordinates != null && toCoordinates != null) {
            plotRouteOnMap(fromCoordinates, toCoordinates);
            Log.d("MapDebug", "Map updated with route");
        } else {
            Log.e("MapError", "Missing coordinates for " + fromLocationName + " or " + toLocationName);
            Toast.makeText(this, "Unable to plot route: Invalid location coordinates",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchUserName(Long userID, String userType, OnUserNameFetched callback) {
        db.collection("users")
                .whereEqualTo("userID", userID)  // Query for the user document by userID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String userName = document.getString("name");  // Assuming "name" is the field for the user's name
                        Log.d("UserDetails", userType + " name: " + userName);
                        callback.onUserNameFetched(userName);  // Pass the userName to the callback
                    } else {
                        Log.d("UserDetails", "Failed to fetch " + userType + " name");
                        callback.onUserNameFetched("N/A");  // Return a default value if not found
                    }
                });
    }

    interface OnUserNameFetched {
        void onUserNameFetched(String name);
    }

    private void fetchLocationName(Long locationID, String locationType, OnLocationNameFetched callback) {
        db.collection("locations")
                .whereEqualTo("locationID", locationID)  // Query for the location document by locationID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String locationName = document.getString("name");  // Assuming "name" is the field for the location's name
                        Log.d("LocationDetails", locationType + " location name: " + locationName);
                        callback.onLocationNameFetched(locationName);  // Pass the locationName to the callback
                    } else {
                        Log.d("LocationDetails", "Failed to fetch " + locationType + " location name");
                        callback.onLocationNameFetched("Unknown");  // Return a default value if not found
                    }
                });
    }

    interface OnLocationNameFetched {
        void onLocationNameFetched(String locationName);
    }

    private void setupChatButton() {
        Button chatButton = findViewById(R.id.btn_chat);

        chatButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HomeChatActivity.class);
            startActivity(i);
        });
    }

    private void plotRouteOnMap(LatLng fromCoordinates, LatLng toCoordinates) {
        if (mMap == null) return;

        // Clear previous markers and routes
        mMap.clear();

        // Add markers for pickup and dropoff
        mMap.addMarker(new MarkerOptions()
                .position(fromCoordinates)
                .title("Pickup: " + fromLocationName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions()
                .position(toCoordinates)
                .title("Dropoff: " + toLocationName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // Fetch and draw route using Directions API
        getDirections(fromCoordinates, toCoordinates);
    }


    private void navigateToNoBookingsScreen() {
        // Navigate to a different screen or show a message
        Intent intent = new Intent(this, NoBookingsActivity.class); // Create a new Activity for "No Bookings"
        startActivity(intent);
        setupChatButton();
        finish(); // Close the current activity
    }
}
