package com.example.meetupsync;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    private EditText commentEditText;
    private Button saveButton;
    private Button randomButton;
    private Button labelButton;
    private EditText labelNameEditText;
    private Button colorPickerButton;
    private Button addLabelButton;
    private int labelColor = Color.RED;
    private String label;
    private static final int REQUEST_CODE_ADD_PASSWORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        serviceEditText = findViewById(R.id.serviceEditText);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        commentEditText = findViewById(R.id.commentEditText);
        saveButton = findViewById(R.id.saveButton);
        randomButton = findViewById(R.id.randomButton);
        labelButton = findViewById(R.id.labelButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String service = serviceEditText.getText().toString();
                String login = loginEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String comment = commentEditText.getText().toString();

                if (service.isEmpty() || login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AddPasswordActivity.this, "Введите название сервиса, логин и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("service", service);
                    intent.putExtra("login", login);
                    intent.putExtra("password", password);
                    intent.putExtra("comment", comment);
                    intent.putExtra("label", label);
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

        labelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddLabelDialog();
            }
        });
    }

    private void showAddLabelDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_label, null);
        dialogBuilder.setView(dialogView);

        labelNameEditText = dialogView.findViewById(R.id.labelNameEditText);
        colorPickerButton = dialogView.findViewById(R.id.colorPickerButton);
        addLabelButton = dialogView.findViewById(R.id.addLabelButton);

        AlertDialog alertDialog = dialogBuilder.create();

        addLabelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                label = labelNameEditText.getText().toString();

                alertDialog.dismiss();

            }
        });

        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });
        alertDialog.show();
    }

    private void showColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите цвет");

        // Создаем слушатель для выбора цвета
        DialogInterface.OnClickListener colorPickerListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int color) {
                // Обработка выбранного цвета
                labelColor = color;
                // Можно также обновить внешний вид кнопки выбора цвета
                colorPickerButton.setBackgroundColor(labelColor);
            }
        };

        // Устанавливаем слушатель для диалогового окна выбора цвета
        builder.setPositiveButton("Выбрать", colorPickerListener);
        builder.setNegativeButton("Отмена", null);

        // Создаем диалоговое окно выбора цвета
        AlertDialog colorPickerDialog = builder.create();

        // Отображаем диалоговое окно
        colorPickerDialog.show();
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
