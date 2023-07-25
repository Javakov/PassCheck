package com.example.meetupsync;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView passwordsRecyclerView;
    private Button addPasswordButton;
    private PasswordAdapter passwordAdapter;
    private List<Password> passwordList;
    private DatabaseHelper databaseHelper;
    private Button searchButton;
    private EditText searchEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordsRecyclerView = findViewById(R.id.passwordsRecyclerView);
        addPasswordButton = findViewById(R.id.addPasswordButton);
        searchButton = findViewById(R.id.searchButton);
        searchEditText = findViewById(R.id.searchEditText);

        databaseHelper = new DatabaseHelper(this);
        passwordList = databaseHelper.getAllPasswords();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        passwordsRecyclerView.setLayoutManager(layoutManager);
        passwordAdapter = new PasswordAdapter(passwordList);
        passwordsRecyclerView.setAdapter(passwordAdapter);

        addPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPasswordActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // обработка нажатия кнопки поиска
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString();

                if (searchText.isEmpty()) {
                    // Если поле поиска пустое, показать все карточки
                    passwordList = databaseHelper.getAllPasswords();
                } else {
                    // Иначе выполнить поиск карточек по тексту вопроса
                    passwordList = databaseHelper.getPasswordsByService("%" + searchText + "%");
                }

                passwordAdapter = new PasswordAdapter(passwordList);
                passwordsRecyclerView.setAdapter(passwordAdapter);

                // Показываем поле поиска и кнопку "Найти"
                searchEditText.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Проверяем, открыто ли поле поиска
        if (searchEditText.getVisibility() == View.VISIBLE) {

            // Сбрасываем текст поиска
            searchEditText.setText("");

            // Показываем все карточки
            passwordList = databaseHelper.getAllPasswords();

            passwordAdapter = new PasswordAdapter(passwordList);
            passwordsRecyclerView.setAdapter(passwordAdapter);

            passwordList = databaseHelper.getAllPasswords();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            int id = 0;
            String service = data.getStringExtra("service");
            String login = data.getStringExtra("login");
            String password = data.getStringExtra("password");

            if (service != null && login != null && password != null) {
                Password newPassword = new Password(id, service, login, password);
                passwordList.add(0, newPassword);
                passwordAdapter.notifyItemInserted(0);
                passwordsRecyclerView.scrollToPosition(0);

                //passwordsRecyclerView.scrollToPosition(0); // Прокручиваем к новому элементу

                databaseHelper.addPassword(newPassword);
            }


        }
    }
}
