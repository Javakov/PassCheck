package org.fubar.passholder.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.fubar.passholder.R;
import org.fubar.passholder.database.DatabaseHelper;
import org.fubar.passholder.dto.Password;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AddPasswordActivity extends AppCompatActivity {
    private EditText serviceEditText;
    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText commentEditText;
    private EditText labelNameEditText;
    private String label;
    private DatabaseHelper dbHelp;
    private static final int PASSWORD_LENGTH = 16;
    private static final String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelp = new DatabaseHelper(this);
        setContentView(R.layout.activity_add_password);
        serviceEditText = findViewById(R.id.serviceEditText);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        commentEditText = findViewById(R.id.commentEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button randomButton = findViewById(R.id.randomButton);
        Button labelButton = findViewById(R.id.labelButton);

        saveButton.setOnClickListener(v -> {
            String service = serviceEditText.getText().toString();
            String login = loginEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String comment = commentEditText.getText().toString();

            if (service.isEmpty() || login.isEmpty() || password.isEmpty()) {
                Toast.makeText(AddPasswordActivity.this, "Введите название сервиса, логин и пароль", Toast.LENGTH_SHORT).show();
            } else if (label == null) {
                Toast.makeText(AddPasswordActivity.this, "Выберите метку", Toast.LENGTH_SHORT).show();
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
        });

        randomButton.setOnClickListener(view -> {
            String password = generateSuperSecurePassword();
            passwordEditText.setText(password);
        });

        labelButton.setOnClickListener(v -> showAddLabelDialog());
    }

    private void showAddLabelDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_label, null);
        dialogBuilder.setView(dialogView);

        labelNameEditText = dialogView.findViewById(R.id.labelNameEditText);
        Button addLabelButton = dialogView.findViewById(R.id.addLabelButton);
        Spinner labelSpinner = dialogView.findViewById(R.id.labelSpinner);

        AlertDialog alertDialog = dialogBuilder.create();

        addLabelButton.setOnClickListener(v -> {
            label = labelNameEditText.getText().toString();
            alertDialog.dismiss();
        });

        // Получаем список всех меток из базы данных
        ArrayAdapter<String> labelAdapter = getStringArrayAdapter();

        // Устанавливаем адаптер для элемента Spinner
        labelSpinner.setAdapter(labelAdapter);

        // Устанавливаем слушатель для выбора метки из списка
        labelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedLabel = (String) adapterView.getItemAtPosition(position);
                labelNameEditText.setText(selectedLabel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        alertDialog.show();
    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        List<Password> labels = dbHelp.getAllLabels();
        Set<String> labelSet = new HashSet<>();

        for (Password password : labels) {
            labelSet.add(password.getLabel());
        }

        ArrayAdapter<String> labelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(labelSet));
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return labelAdapter;
    }

    private String generateSuperSecurePassword() {
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(PASSWORD_CHARACTERS.length());
            char character = PASSWORD_CHARACTERS.charAt(index);
            password.append(character);
        }
        return password.toString();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppInForeground()) {
            finish();
        }
    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);

        if (!runningTasks.isEmpty()) {
            ComponentName topActivity = runningTasks.get(0).topActivity;
            assert topActivity != null;
            return topActivity.getPackageName().equals(getPackageName());
        }

        return false;
    }
}
