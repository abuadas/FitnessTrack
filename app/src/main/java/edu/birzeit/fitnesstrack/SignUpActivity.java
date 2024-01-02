package edu.birzeit.fitnesstrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtTxtName, edtTxtSignUpEmail, edtTxtSignUpPass;
    private CheckBox checkBoxRememberSignUp;
    private Button btnSignUp;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeViews();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void initializeViews() {
        edtTxtName = findViewById(R.id.edtTxtName);
        edtTxtSignUpEmail = findViewById(R.id.edtTxtSignUpEmail);
        edtTxtSignUpPass = findViewById(R.id.edtTxtSignUpPass);
        checkBoxRememberSignUp = findViewById(R.id.checkBoxRememberSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);

        pref = getSharedPreferences("userPreferences", MODE_PRIVATE);
    }

    private void signUp() {
        String name = edtTxtName.getText().toString().trim();
        String email = edtTxtSignUpEmail.getText().toString().trim();
        String password = edtTxtSignUpPass.getText().toString().trim();

        if (checkBoxRememberSignUp.isChecked()) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        }

        Toast.makeText(SignUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignInClick(View view) {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }
}