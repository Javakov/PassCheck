package com.example.meetupsync;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PasswordDetailsActivity extends AppCompatActivity {

    private TextView serviceTextView;
    private TextView loginTextView;
    private TextView passwordTextView;
    private TextView commentTextView;
    private Button copyButton;
    private Button deleteButton;
    private DatabaseHelper dbhelp;
    private static final int REQUEST_CODE_DELETE_PASSWORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_details);

        dbhelp = new DatabaseHelper(PasswordDetailsActivity.this);

        serviceTextView = findViewById(R.id.serviceTextView);
        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        commentTextView = findViewById(R.id.commentTextView);
        copyButton = findViewById(R.id.copyButton);
        deleteButton = findViewById(R.id.deleteButton);

        int id = getIntent().getIntExtra("id", 0);
        Log.d("teg", "id: " + id);
        String service = getIntent().getStringExtra("service");
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");
        String comment = getIntent().getStringExtra("comment");

        serviceTextView.setText("https://" + service);
        loginTextView.setText(login);
        passwordTextView.setText(password);
        commentTextView.setText(comment);

        serviceTextView.setTypeface(null, Typeface.ITALIC);
        serviceTextView.setPaintFlags(serviceTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        serviceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> domen = new ArrayList<String>();
                domen.add(".com");
                domen.add(".net");
                domen.add(".org");
                domen.add(".ru");
                domen.add(".de");
                domen.add(".info");

                String serviceUrl = "https://" + service;

                Executor executor = Executors.newSingleThreadExecutor();
                 // Создаем финальную копию url для каждой итерации

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (String domain : domen) {
                                    final String url = serviceUrl + domain;
                                    URL urlObj = new URL(url);
                                    HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                                    connection.setRequestMethod("GET");
                                    int responseCode = connection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        switch (domain) {
                                            case ".com":
                                            case ".net":
                                            case ".org":
                                            case ".ru":
                                            case ".de":
                                            case ".info":
                                                Uri uri = Uri.parse(urlObj.toString());
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                                break;
                                        }
                                    }
                                    else {
                                        String poiskUrl = "https://yandex.ru/search/?text=" + service;
                                        Uri uri = Uri.parse(poiskUrl);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }

                                }

                            }
                            catch (UnknownHostException e){
                                String poiskUrl = "https://yandex.ru/search/?text=" + service;
                                Uri uri = Uri.parse(poiskUrl);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            }
        });






        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String passwordToCopy = passwordTextView.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("password", password);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PasswordDetailsActivity.this, "Пароль скопирован", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Устанавливаем результат RESULT_OK и передаем ID удаляемого пароля
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", id);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
