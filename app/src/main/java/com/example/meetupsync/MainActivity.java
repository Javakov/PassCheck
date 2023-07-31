package com.example.meetupsync;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private LinearLayout passwordsLayout;
    private Button addPasswordButton;
    private Button searchButton;
    private EditText searchEditText;
    private DatabaseHelper dbhelp;
    private Button checkLabelButton;
    private String selectedLabel;


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
        checkLabelButton = findViewById(R.id.checkLabelButton);

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

        checkLabelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabelsDialog();
                showPasswords();
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
        if (selectedLabel != null) {
            // Если выбрана метка, сбрасываем выбранную метку и показываем все карточки
            selectedLabel = null;
            showPasswords();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_PASSWORD && resultCode == RESULT_OK) {
            if (data != null) {
                String service = data.getStringExtra("service");
                String login = data.getStringExtra("login");
                String password = data.getStringExtra("password");
                String comment = data.getStringExtra("comment");
                String label = data.getStringExtra("label");

                // Создание объекта Password с полученными данными
                Password newPassword = new Password(service, login, password, comment, label);
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

    private void showLabelsDialog() {
        Set<String> labelSet = new HashSet<>();
        List<Password> labels = dbhelp.getAllLabels();

        for (Password password : labels) {
            labelSet.add(password.getLabel());
        }

        final String[] labelArray = labelSet.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Метки")
                .setItems(labelArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedLabel = labelArray[i];
                        showPasswords();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // метод для отображения всех карточек на экране
    private void showPasswords() {
        List<Password> passwordList = dbhelp.getAllPasswords();

        if (selectedLabel != null) {
            passwordList = dbhelp.getPasswordsByLabel(selectedLabel);
        } else {
            passwordList = dbhelp.getAllPasswords();
        }

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
        TextView commentTextView = cardView.findViewById(R.id.commentTextView);
        TextView labelTextView = cardView.findViewById(R.id.labelTextView);

        // Устанавливаем значение поля idTextView
        idTextView.setText(String.valueOf(password.getId()));
        serviceTextView.setText(password.getService());
        loginTextView.setText(password.getLogin());
        passwordTextView.setText(password.getPassword());
        commentTextView.setText(password.getComment());
        labelTextView.setText(password.getLabel());

        // Добавляем обработчик нажатия на карточку
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PasswordDetailsActivity.class);
                intent.putExtra("id", password.getId());
                intent.putExtra("service", password.getService());
                intent.putExtra("login", password.getLogin());
                intent.putExtra("password", password.getPassword());
                intent.putExtra("comment", password.getComment());
                intent.putExtra("label", password.getLabel());
                startActivityForResult(intent, REQUEST_CODE_DELETE_PASSWORD);
            }
        });
        passwordsLayout.addView(cardView);
    }
}
