package com.example.meetupsync;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout passwordsLayout;
    private Button addPasswordButton;
    private Button searchButton;
    private EditText searchEditText;
    private DatabaseHelper dbhelp;
    private Button deleteButton;

    private static final int REQUEST_CODE_ADD_PASSWORD = 1;
    private static final int REQUEST_CODE_DELETE_PASSWORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordsLayout = findViewById(R.id.passwordsLayout);
        addPasswordButton = findViewById(R.id.addPasswordButton);
        searchButton = findViewById(R.id.searchButton);
        searchEditText = findViewById(R.id.searchEditText);

        dbhelp = new DatabaseHelper(this);
        showPasswords();

        addPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPasswordActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_PASSWORD);
            }
        });

        // обработка нажатия кнопки поиска
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString();

                // поиск карточек по тексту вопроса
                List<Password> passwordList = dbhelp.getPasswordsByService(searchText);
                Collections.reverse(passwordList);

                // вывод результатов поиска
                passwordsLayout.removeAllViews();
                for (Password password : passwordList) {
                    showPassword(password, new ArrayList<>());
                }
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
            showPasswords();
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

        if (requestCode == REQUEST_CODE_ADD_PASSWORD && resultCode == RESULT_OK) {
            if (data != null) {
                String service = data.getStringExtra("service");
                String login = data.getStringExtra("login");
                String password = data.getStringExtra("password");


                // Создание объекта Password с полученными данными
                Password newPassword = new Password(service, login, password);
                // Добавление нового пароля в базу данных
                dbhelp.addPassword(newPassword);
                // Обновление отображения списка паролей
                showPasswords();
            }
        }
        else if (requestCode == REQUEST_CODE_DELETE_PASSWORD && resultCode == RESULT_OK) {
            if (data != null) {
                int id = data.getIntExtra("id", 0);
                dbhelp.deletePasswordById(id);
                // Обновление отображения списка паролей
                showPasswords();
            }
        }
    }



//
//
//            if (data != null){
//                int id = data.getIntExtra("id", 0);
//                dbhelp.deletePasswordById(id);
//                showPasswords();
//            }


    // метод для отображения всех карточек на экране
    private void showPasswords() {
        List<Password> passwordList = dbhelp.getAllPasswords();

        passwordsLayout.removeAllViews();
        for (int i = passwordList.size() - 1; i >= 0; i--) {
            Password password = passwordList.get(i);
            password.setId(i + 1); // Устанавливаем уникальный идентификатор
            showPassword(password, new ArrayList<>());
        }
    }

    // метод для отображения одной карточки на экране
    private void showPassword(Password password, List<View> selectedCards) {
        LayoutInflater inflater = getLayoutInflater();
        View cardView = inflater.inflate(R.layout.list_item_password, null);

        TextView idTextView = cardView.findViewById(R.id.idTextView);
        TextView serviceTextView = cardView.findViewById(R.id.serviceTextView);
        TextView loginTextView = cardView.findViewById(R.id.loginTextView);
        TextView passwordTextView = cardView.findViewById(R.id.passwordTextView);

        // Устанавливаем значение поля idTextView
        idTextView.setText(String.valueOf(password.getId()));
        serviceTextView.setText(password.getService());
        loginTextView.setText(password.getLogin());
        passwordTextView.setText(password.getPassword());

        // Добавляем обработчик нажатия на карточку
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PasswordDetailsActivity.class);
                intent.putExtra("id", password.getId());
                intent.putExtra("service", password.getService());
                intent.putExtra("login", password.getLogin());
                intent.putExtra("password", password.getPassword());
                startActivityForResult(intent, REQUEST_CODE_DELETE_PASSWORD);
            }
        });

        passwordsLayout.addView(cardView);
    }
}
