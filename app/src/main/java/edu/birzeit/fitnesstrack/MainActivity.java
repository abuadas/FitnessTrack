package edu.birzeit.fitnesstrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
    private Sensor stepCounterSensor;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int SENSOR_TYPE_STEP_COUNTER = 19;
    private long stepCount = 0;
    private static final double AVERAGE_STRIDE_LENGTH = 0.762;
    private static final double CALORIES_BURNED_PER_STEP = 0.04;
    private static final String STEP_COUNT_KEY = "step_count";
    private RequestQueue requestQueue;
    private static final String API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        checkSensorAvailability();
        setDetailedInfoButtonListener();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initializeViews() {
        txtSteps = findViewById(R.id.txtSteps);
        txtCalories = findViewById(R.id.txtCalories);
        txtDistance = findViewById(R.id.txtDistance);
        btnDetailedInfo = findViewById(R.id.btnDetailedInfo);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(SENSOR_TYPE_STEP_COUNTER);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == SENSOR_TYPE_STEP_COUNTER) {
            long currentSteps = (long) event.values[0];
            if (stepCount == 0) {
                stepCount = currentSteps;
            }
            long stepsSinceLastUpdate = currentSteps - stepCount;
            stepCount = currentSteps;

            txtSteps.setText("Steps: ".concat(String.valueOf(currentSteps)));
            double caloriesBurned = stepsSinceLastUpdate * CALORIES_BURNED_PER_STEP;
            txtCalories.setText("Calories burned: ".concat(String.valueOf(caloriesBurned)));
            double distance = stepsSinceLastUpdate * AVERAGE_STRIDE_LENGTH;
            txtDistance.setText("Distance: ".concat(String.valueOf(distance)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        outState.putLong(STEP_COUNT_KEY, stepCount);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stepCount = savedInstanceState.getLong(STEP_COUNT_KEY);
        txtSteps.setText("Steps: ".concat(String.valueOf(stepCount)));
        double caloriesBurned = stepCount * CALORIES_BURNED_PER_STEP;
        txtCalories.setText("Calories burned: ".concat(String.valueOf(caloriesBurned)));
        double distance = stepCount * AVERAGE_STRIDE_LENGTH;
        txtDistance.setText("Distance: ".concat(String.valueOf(distance)));
    }

    private void checkSensorAvailability() {
        if (stepCounterSensor == null) {
            Toast.makeText(this, "Step Counter Sensor is not available", Toast.LENGTH_SHORT).show();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_CODE);
        } else {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void setDetailedInfoButtonListener() {
        btnDetailedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdates();
            }
        });
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
        double lat2 = location.getLatitude();
        double lon2 = location.getLongitude();
        locationManager.removeUpdates(this);
        fetchDataFromAPI(lat2, lon2);
    }

    private void fetchDataFromAPI(double lat2, double lon2) {
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
                return result.getString("formatted_address");
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
        double lat1 = Double.parseDouble(coordinates[0]);
        double lon1 = Double.parseDouble(coordinates[1]);
        double lat2 = 0;
        double lon2 = 0;

        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
