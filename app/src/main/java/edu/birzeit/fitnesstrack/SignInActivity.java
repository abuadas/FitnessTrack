package edu.birzeit.fitnesstrack;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private EditText edtTxtSignInEmail, edtTxtSignInPass;
    private CheckBox checkBoxRememberSignIn;
    private Button btnSignIn;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtTxtSignInEmail = findViewById(R.id.edtTxtSignInEmail);
        edtTxtSignInPass = findViewById(R.id.edtTxtSignInPass);
        checkBoxRememberSignIn = findViewById(R.id.checkBoxRememberSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);

        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);

        String savedEmail = prefs.getString("email", "");
        String savedPassword = prefs.getString("password", "");
        edtTxtSignInEmail.setText(savedEmail);
        edtTxtSignInPass.setText(savedPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        String email = edtTxtSignInEmail.getText().toString().trim();
        String password = edtTxtSignInPass.getText().toString().trim();
        if (checkBoxRememberSignIn.isChecked()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        }

        Toast.makeText(SignInActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onCreateAccountClick(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}