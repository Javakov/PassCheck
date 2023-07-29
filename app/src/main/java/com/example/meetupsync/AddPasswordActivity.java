package com.example.meetupsync;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Random;

public class AddPasswordActivity extends AppCompatActivity {

    private EditText serviceEditText;
    private EditText loginEditText;
    private EditText passwordEditText;
    private Button saveButton;
    private Button randomButton;
    private static final int REQUEST_CODE_ADD_PASSWORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        serviceEditText = findViewById(R.id.serviceEditText);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);
        randomButton = findViewById(R.id.randomButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String service = serviceEditText.getText().toString();
                String login = loginEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (service.isEmpty() || login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AddPasswordActivity.this, "Введите название сервиса, логин и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("service", service);
                    intent.putExtra("login", login);
                    intent.putExtra("password", password);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = generateSuperSecurePassword();
                passwordEditText.setText(password);
            }
        });
    }

    private String generateSuperSecurePassword() {
        int length = 16; // Длина пароля
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char character = characters.charAt(index);
            password.append(character);
        }
        return password.toString();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Если приложение не на переднем плане, завершаем его
        if (!isAppInForeground()) {
            finish();
        }
    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);

        if (!runningTasks.isEmpty()) {
            ComponentName topActivity = runningTasks.get(0).topActivity;
            return topActivity.getPackageName().equals(getPackageName());
        }

        return false;
    }
}
