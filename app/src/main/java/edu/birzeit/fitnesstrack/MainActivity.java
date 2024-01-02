package edu.birzeit.fitnesstrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private TextView txtSteps, txtCalories, txtDistance;
    private Button btnDetailedInfo;
    private SensorManager sensorManager;
    private Sensor sensor;


    private RequestQueue requestQueue;
    private static final String API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private LocationManager locationManager;

    private static long STEP_COUNTER = 0;
    private static long INITIAL_STEP_COUNT = 0;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int SENSOR_TYPE_STEP_COUNTER = 19;
    private static final double AVERAGE_STRIDE_LENGTH = 0.762;
    private static final double CALORIES_BURNED_PER_STEP = 0.04;
    private static final String STEP_COUNT_KEY = "stepCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        checkSensorAvailability();
        requestLocationUpdates();
        setDetailedInfoButtonListener();
        requestQueue = Volley.newRequestQueue(this);
        INITIAL_STEP_COUNT = STEP_COUNTER;
    }

    private void initializeViews() {
        txtSteps = findViewById(R.id.txtSteps);
        txtCalories = findViewById(R.id.txtCalories);
        txtDistance = findViewById(R.id.txtDistance);
        btnDetailedInfo = findViewById(R.id.btnDetailedInfo);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(SENSOR_TYPE_STEP_COUNTER);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == SENSOR_TYPE_STEP_COUNTER) {
            long newStepCount = (long) event.values[0];
            long stepsSinceStart = newStepCount - INITIAL_STEP_COUNT;
            txtSteps.setText("Steps: ".concat(String.valueOf(stepsSinceStart)));
            txtSteps.setText("Steps: ".concat(String.valueOf(newStepCount)));
            double caloriesBurned = stepsSinceStart * CALORIES_BURNED_PER_STEP;
            txtCalories.setText("Calories burned: ".concat(String.valueOf(caloriesBurned)));
            double distance = stepsSinceStart * AVERAGE_STRIDE_LENGTH;
            txtDistance.setText("Distance: ".concat(String.valueOf(distance)));
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STEP_COUNT_KEY, STEP_COUNTER);
    }

    private void checkSensorAvailability() {
        if (sensor == null || ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0, MainActivity.this);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double distLat = location.getLatitude();
        double distLon = location.getLongitude();
        locationManager.removeUpdates(this);
        fetchDataFromAPI(distLat, distLon);
    }

    private void fetchDataFromAPI(double distLat, double distLon) {
        String address = " ";
        String apiKey = "API_KEY";
        String url = API_URL + "?address=" + address + "&key=" + apiKey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String location = parseLocationFromResponse(response);
                        processLocationData(location);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(stringRequest);
    }

    private String parseLocationFromResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject result = results.getJSONObject(0);
                return result.getString("formattedAddress");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processLocationData(String location) {
        if (location != null) {
            double distance = calculateDistance(location);
            txtDistance.setText("Distance: " + distance + " km");
        } else {
            Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateDistance(String location) {
        String[] coordinates = location.split(",");
        double sourceLat = Double.parseDouble(coordinates[0]);
        double sourceLong = Double.parseDouble(coordinates[1]);
        double distLat = 37.7749;
        double distLon = -122.4194;
        final int earthRadius = 6371;
        double latDistance = Math.toRadians(distLat - sourceLat);
        double lonDistance = Math.toRadians(distLon - sourceLong);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(sourceLat)) * Math.cos(Math.toRadians(distLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double distance = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * distance;
    }

    private void setDetailedInfoButtonListener() {
        btnDetailedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
