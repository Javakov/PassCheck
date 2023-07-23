package com.example.meetupsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddPasswordActivity extends AppCompatActivity {

    private EditText serviceEditText;
    private EditText passwordEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        serviceEditText = findViewById(R.id.serviceEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String service = serviceEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (service.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AddPasswordActivity.this, "Введите название сервиса и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("service", service);
                    intent.putExtra("password", password);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
