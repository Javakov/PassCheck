package com.example.meetupsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.ConsumerIrManager;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class PinCodeActivity extends AppCompatActivity {

    private EditText pinCodeEditText;
    private Button submitButton;

    private SharedPreferences sharedPreferences;
    private boolean isFirstRun;
    ConstraintLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EncryptionHelper.initialize(getApplicationContext());
        setContentView(R.layout.activity_pin_code);


        // Открытие клавиатуры сразу при открытии приложения
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        pinCodeEditText = findViewById(R.id.pinCodeEditText);
        submitButton = findViewById(R.id.submitButton);
        mMainLayout = findViewById(R.id.main_layout);
        ImageView fingerImageView = findViewById(R.id.fingerImageView);


        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "Устройство поддерживает вход по отпечатку пальца.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "Устройство не поддерживает вход по отпечатку пальца.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Проверка отпечатка пальца не работает");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "Отпечаток пальца не установлен");
                break;
        }
        fingerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiometricPrompt();
            }
        });

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            submitButton.setText("СОХРАНИТЬ");
            fingerImageView.setVisibility(View.GONE);
        } else {
            submitButton.setText("ВОЙТИ");
            showBiometricPrompt();
        }

        // Установка курсора в поле для ввода пароля
        pinCodeEditText.requestFocus();

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
                    Toast.makeText(getApplicationContext(), "Пин-код сохранён", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    // Проверка введенного пин-кода
                    String savedPinCode = sharedPreferences.getString("pinCode", "");
                    if (pinCode.equals(savedPinCode)) {
                        // Пин-код верный, переход к следующей странице
                        Intent intent = new Intent(PinCodeActivity.this, MainActivity.class);
                        Toast.makeText(getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    } else {
                        // Пин-код неверный, выполнение соответствующих действий
                        Toast.makeText(PinCodeActivity.this, R.string.invalid_pin_message, Toast.LENGTH_SHORT).show();
                        pinCodeEditText.setText(null);
                    }
                }
            }
        });

        pinCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
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
                            pinCodeEditText.setText(null);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(PinCodeActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PinCodeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Обработка ошибки аутентификации
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Обработка неудачной аутентификации
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Используйте отпечаток пальца")
                .setNegativeButtonText("Использовать пин-код")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
