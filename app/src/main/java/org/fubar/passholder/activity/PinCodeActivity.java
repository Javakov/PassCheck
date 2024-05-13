package org.fubar.passholder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import org.fubar.passholder.MainActivity;
import org.fubar.passholder.R;
import org.fubar.passholder.database.EncryptionHelper;

import java.util.concurrent.Executor;

public class PinCodeActivity extends AppCompatActivity {
    private EditText pinCodeEditText;
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
        Button submitButton = findViewById(R.id.submitButton);
        mMainLayout = findViewById(R.id.main_layout);
        ImageView fingerImageView = findViewById(R.id.fingerImageView);

        fingerImageView.setOnClickListener(v -> showBiometricPrompt());

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            submitButton.setText("СОХРАНИТЬ");
            fingerImageView.setVisibility(View.GONE);
        } else {
            showBiometricPrompt();
        }

        // Установка курсора в поле для ввода пароля
        pinCodeEditText.requestFocus();

        submitButton.setOnClickListener(v -> {
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
        });

        pinCodeEditText.setOnEditorActionListener((v, actionId, event) -> {
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
                        pinCodeEditText.setText(null);
                    }
                }
                return true;
            }
            return false;
        });
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(PinCodeActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PinCodeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Используйте отпечаток пальца")
                .setNegativeButtonText("Использовать пин-код")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
