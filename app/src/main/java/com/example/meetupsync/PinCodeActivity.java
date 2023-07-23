package com.example.meetupsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PinCodeActivity extends AppCompatActivity {

    private EditText pinCodeEditText;
    private Button submitButton;

    private SharedPreferences sharedPreferences;
    private boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);

        pinCodeEditText = findViewById(R.id.pinCodeEditText);
        submitButton = findViewById(R.id.submitButton);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            submitButton.setText("СОХРАНИТЬ ПАРОЛЬ");
        } else {
            submitButton.setText("ВОЙТИ");
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinCode = pinCodeEditText.getText().toString();

                if (isFirstRun) {
                    // Первый запуск, сохранение пароля
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("pinCode", pinCode);
                    editor.putBoolean("isFirstRun", false);
                    editor.apply();

                    // Переход к следующей странице
                    Intent intent = new Intent(PinCodeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Проверка введенного пин-кода
                    String savedPinCode = sharedPreferences.getString("pinCode", "");
                    if (pinCode.equals(savedPinCode)) {
                        // Пин-код верный, переход к следующей странице
                        Intent intent = new Intent(PinCodeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Пин-код неверный, выполнение соответствующих действий
                        Toast.makeText(PinCodeActivity.this, R.string.invalid_pin_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        pinCodeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String pinCode = pinCodeEditText.getText().toString();

                    if (isFirstRun) {
                        // Первый запуск, сохранение пароля
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pinCode", pinCode);
                        editor.putBoolean("isFirstRun", false);
                        editor.apply();

                        // Переход к следующей странице
                        Intent intent = new Intent(PinCodeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else {
                        // Проверка введенного пин-кода
                        String savedPinCode = sharedPreferences.getString("pinCode", "");
                        if (pinCode.equals(savedPinCode)) {
                            // Пин-код верный, переход к следующей странице
                            Intent intent = new Intent(PinCodeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else {
                            // Пин-код неверный, выполнение соответствующих действий
                            Toast.makeText(PinCodeActivity.this, R.string.invalid_pin_message, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }
}
