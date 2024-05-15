package org.fubar.passholder;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.fubar.passholder.activity.AddPasswordActivity;
import org.fubar.passholder.activity.PasswordDetailsActivity;
import org.fubar.passholder.activity.PinCodeActivity;
import org.fubar.passholder.database.DatabaseHelper;
import org.fubar.passholder.dto.Password;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private LinearLayout passwordsLayout;
    private SearchView searchView;
    private DatabaseHelper dbHelp;
    private String selectedLabel;
    private static final int REQUEST_CODE_ADD_PASSWORD = 1;
    private static final int REQUEST_CODE_DELETE_PASSWORD = 2;
    public static final long DISCONNECT_TIMEOUT = 300000;
    private CountDownTimer disconnectTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        passwordsLayout = findViewById(R.id.passwordsLayout);
        Button addPasswordButton = findViewById(R.id.addPasswordButton);
        Button checkLabelButton = findViewById(R.id.checkLabelButton);

        dbHelp = new DatabaseHelper(this);
        showPasswords();

        addPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPasswordActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PASSWORD);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        checkLabelButton.setOnClickListener(view -> {
            showLabelsDialog();
            showPasswords();
        });
    }

    @Override
    public void onBackPressed() {
        // Проверяем, открыто ли поле поиска
        super.onBackPressed();
        if (!searchView.isIconified()) {
            // Сбрасываем текст поиска
            searchView.setQuery("", false);
            searchView.setIconified(true);
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
                dbHelp.addPassword(newPassword);
                // Обновление отображения списка паролей
                showPasswords();
            }
        } else if (requestCode == REQUEST_CODE_DELETE_PASSWORD && resultCode == RESULT_OK) {
            if (data != null) {
                int id = data.getIntExtra("id", 0);
                dbHelp.deletePasswordById(id);
                // Обновление отображения списка паролей
                showPasswords();
            }
        }
    }

    private void showLabelsDialog() {
        Set<String> labelSet = new HashSet<>();
        List<Password> labels = dbHelp.getAllLabels();

        if (labels != null) { // Добавляем проверку на null
            for (Password password : labels) {
                String label = password.getLabel();
                if (label != null) {
                    labelSet.add(label);
                }
            }
        }

        final String[] labelArray = labelSet.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Метки")
                .setItems(labelArray, (dialogInterface, i) -> {
                    selectedLabel = labelArray[i];
                    showPasswords();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // метод для выполнения поиска
    private void performSearch(String query) {
        List<Password> cardList = dbHelp.getPasswordsByService(query);
        Collections.reverse(cardList);

        passwordsLayout.removeAllViews();
        for (Password password : cardList) {
            showPassword(password);
        }
    }

    // метод для отображения всех карточек на экране
    private void showPasswords() {
        dbHelp.getAllPasswords();
        List<Password> passwordList;

        if (selectedLabel != null) {
            passwordList = dbHelp.getPasswordsByLabel(selectedLabel);
        } else {
            passwordList = dbHelp.getAllPasswords();
        }

        passwordsLayout.removeAllViews();
        for (int i = passwordList.size() - 1; i >= 0; i--) {
            Password password = passwordList.get(i);
            password.setId(i + 1); // Устанавливаем уникальный идентификатор
            showPassword(password);
        }
    }

    // метод для отображения одной карточки на экране
    private void showPassword(Password password) {
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
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PasswordDetailsActivity.class);
            intent.putExtra("id", password.getId());
            intent.putExtra("service", password.getService());
            intent.putExtra("login", password.getLogin());
            intent.putExtra("password", password.getPassword());
            intent.putExtra("comment", password.getComment());
            intent.putExtra("label", password.getLabel());
            startActivityForResult(intent, REQUEST_CODE_DELETE_PASSWORD);
        });
        passwordsLayout.addView(cardView);
    }

    void logout() {
        Intent intent = new Intent(this, PinCodeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void startLogoutTimer() {
        if (!isTimerRunning) {
            disconnectTimer = new CountDownTimer(DISCONNECT_TIMEOUT, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("DisconnectTimer Main", "Disconnect timer running: " + millisUntilFinished / 1000 + " seconds remaining");
                }

                @Override
                public void onFinish() {
                    logout();
                }
            }.start();
            isTimerRunning = true;
        }
    }

    public void resetLogoutTimer() {
        if (isTimerRunning) {
            disconnectTimer.cancel();
            isTimerRunning = false;
        }
        startLogoutTimer();
    }

    @Override
    public void onUserInteraction() {
        resetLogoutTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetLogoutTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTimerRunning) {
            disconnectTimer.cancel();
            isTimerRunning = false;
            resetLogoutTimer();
        }
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
