package edu.birzeit.fitnesstrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class UserProfileActivity extends AppCompatActivity {

    private String username; // You can obtain this from registration info or shared preferences
    private String weight, height, gender, age, sleepHours;
    private float bmi;

    private TextView txtUsername, txtWeight, txtHeight, txtGender, txtAge, txtBMI;
    private Button btnEdit, btnBackMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeViews();

        Intent intent = getIntent();
        if (intent != null) {
            String weight = intent.getStringExtra("weight");
            String height = intent.getStringExtra("height");
            int genderId = intent.getIntExtra("gender", -1);
            String age = intent.getStringExtra("age");

            txtWeight.setText(weight);
            txtHeight.setText(height);

            if (genderId != -1) {
                RadioButton radioButton = findViewById(genderId);
                String gender = radioButton.getText().toString();
                txtGender.setText(gender);
            }

            txtAge.setText(age);
        }
    }

    private void initializeViews() {
        txtWeight = findViewById(R.id.txtWeight);
        txtHeight = findViewById(R.id.txtHeight);
        txtGender = findViewById(R.id.txtGender);
        txtAge = findViewById(R.id.txtAge);
    }

    private void loadUserProfile() {
        txtUsername.setText("Username: " + username);
        txtWeight.setText("Weight: " + weight);
        txtHeight.setText("Height: " + height);
        txtGender.setText("Gender: " + gender);
        txtAge.setText("Age: " + age);
    }

    private void calculateBMI() {
        float weightInKg = Float.parseFloat(weight);
        float heightInMeters = Float.parseFloat(height) / 100;

        bmi = weightInKg / (heightInMeters * heightInMeters);

        txtBMI.setText("BMI: " + String.format("%.2f", bmi));
    }

    private void navigateToEditProfile() {
        Intent editProfileIntent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
        startActivity(editProfileIntent);
        finish();
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


}