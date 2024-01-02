package edu.birzeit.fitnesstrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class UserProfileActivity extends AppCompatActivity {

    private String username;
    private String weight, height, gender, age;
    private float bmi;

    private TextView txtUsername, txtWeight, txtHeight, txtGender, txtAge, txtBMI;
    private Button btnEdit, btnBackMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeViews();
        Intent intent = getIntent();
        processIntentData(intent);
        calculateBMI();
        txtBMI.setText("BMI: " + String.format("%.2f", bmi));
        navigateToEditProfile();

    }

    private void initializeViews() {
        txtWeight = findViewById(R.id.txtWeight);
        txtHeight = findViewById(R.id.txtHeight);
        txtGender = findViewById(R.id.txtGender);
        txtAge = findViewById(R.id.txtAge);

    }

    private void processIntentData(Intent intent) {
        if (intent != null) {
            weight = intent.getStringExtra("weight");
            height = intent.getStringExtra("height");
            int genderId = intent.getIntExtra("gender", -1);
            age = intent.getStringExtra("age");

            txtWeight.setText("Weight: " + weight);
            txtHeight.setText("Height: " + height);

            if (genderId != -1) {
                RadioButton radioButton = findViewById(genderId);
                gender = radioButton.getText().toString();
                txtGender.setText("Gender: " + gender);
            }

            txtAge.setText("Age: " + age);
        }
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