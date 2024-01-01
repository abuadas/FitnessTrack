package edu.birzeit.fitnesstrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtTxtWeight, edtTxtHeight, edtTxtAge;
    private RadioGroup radioGroupGender;
    private RadioButton radioMale, radioFemale;
    private Button btnBack, btnUpdateProfile;

    private String username; // You can obtain this from previous activity or shared preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtTxtWeight = findViewById(R.id.edtTxtWeight);
        edtTxtHeight = findViewById(R.id.edtTxtHeight);
        edtTxtAge = findViewById(R.id.edtTxtAge);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        btnBack = findViewById(R.id.btnBack);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        loadUserProfile();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String weight = prefs.getString("weight", "");
        String height = prefs.getString("height", "");
        String gender = prefs.getString("gender", "");
        String age = prefs.getString("age", "");

        edtTxtWeight.setText(weight);
        edtTxtHeight.setText(height);
        edtTxtAge.setText(age);
        if (gender.equals("Male")) {
            radioMale.setChecked(true);
        } else if (gender.equals("Female")) {
            radioFemale.setChecked(true);
        }
    }

    private void updateProfile() {
        String weight = edtTxtWeight.getText().toString();
        String height = edtTxtHeight.getText().toString();
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender.getText().toString();
        String age = edtTxtAge.getText().toString();

        saveUserProfile(weight, height, gender, age);
        navigateToUserProfile();
    }

    private void saveUserProfile(String weight, String height, String gender, String age) {
        SharedPreferences.Editor editor = getSharedPreferences("UserProfile", MODE_PRIVATE).edit();
        editor.putString("weight", weight);
        editor.putString("height", height);
        editor.putString("gender", gender);
        editor.putString("age", age);
        editor.apply();
    }

    private void navigateToUserProfile() {
        Intent userProfileIntent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
        userProfileIntent.putExtra("weight", edtTxtWeight.getText().toString());
        userProfileIntent.putExtra("height", edtTxtHeight.getText().toString());
        userProfileIntent.putExtra("gender", radioGroupGender.getCheckedRadioButtonId());
        userProfileIntent.putExtra("age", edtTxtAge.getText().toString());
        startActivity(userProfileIntent);
        finish();
    }
}