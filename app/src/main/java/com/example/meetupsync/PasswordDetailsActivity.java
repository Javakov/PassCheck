package com.example.meetupsync;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PasswordDetailsActivity extends AppCompatActivity {

    private TextView serviceTextView;
    private TextView loginTextView;
    private TextView passwordTextView;
    private Button copyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_details);

        serviceTextView = findViewById(R.id.serviceTextView);
        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        copyButton = findViewById(R.id.copyButton);

        String service = getIntent().getStringExtra("service");
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");

        serviceTextView.setText(service);
        loginTextView.setText(login);
        passwordTextView.setText(password);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordToCopy = passwordTextView.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("password", passwordToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PasswordDetailsActivity.this, "Пароль скопирован", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


