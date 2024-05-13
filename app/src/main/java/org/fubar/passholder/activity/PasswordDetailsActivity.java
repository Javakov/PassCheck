package org.fubar.passholder.activity;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.fubar.passholder.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordDetailsActivity extends AppCompatActivity {
    private TextView serviceTextView;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_details);


        serviceTextView = findViewById(R.id.serviceTextView);
        TextView loginTextView = findViewById(R.id.loginTextView);
        TextView passwordTextView = findViewById(R.id.passwordTextView);
        TextView commentTextView = findViewById(R.id.commentTextView);
        TextView labelTextView = findViewById(R.id.labelTextView);
        Button copyButton = findViewById(R.id.copyButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        int id = getIntent().getIntExtra("id", 0);
        Log.d("teg", "id: " + id);
        String service = getIntent().getStringExtra("service");
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");
        String comment = getIntent().getStringExtra("comment");
        String label = getIntent().getStringExtra("label");

        serviceTextView.setText("https://" + service);
        loginTextView.setText(login);
        passwordTextView.setText(password);
        commentTextView.setText(comment);
        labelTextView.setText(label);

        serviceTextView.setTypeface(null, Typeface.ITALIC);
        serviceTextView.setPaintFlags(serviceTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        serviceTextView.setOnClickListener(v -> {
            animateClick(serviceTextView);
            String searchUrl = "https://www.google.com/search?q=" + service;

            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.execute(() -> {
                try {
                    String htmlContent = getString(searchUrl);
                    ArrayList<String> links = extractLinks(htmlContent);
                    String firstLinkUrl = null;
                    for (String link : links) {
                        Log.d("Links", "link: " + link);
                        assert service != null;

                        char[] alphabet = new char[26]; // Создаем массив длиной 26 (количество букв в английском алфавите)
                        for (int i = 0; i < 26; i++) {
                            alphabet[i] = (char) ('a' + i); // Заполняем массив буквами английского алфавита
                        }

                        if (link.startsWith("https://" + service.toLowerCase()) || link.startsWith("https://www." + service.toLowerCase())
                                || link.startsWith("http://" + service.toLowerCase()) || link.startsWith("http://www." + service.toLowerCase())) {
                            Log.d("Service", "Service: " + service);
                            firstLinkUrl = link;
                            break;
                        } else {
                            for (char letter : alphabet) {
                                if (link.startsWith("https://" + letter + ".www." + service.toLowerCase()) || link.startsWith("https://" + letter + "." + service.toLowerCase())) {
                                    Log.d("Service", "Service + char: " + service);
                                    firstLinkUrl = link;
                                    break;
                                }
                            }
                        }
                    }

                    if (firstLinkUrl != null) {
                        Uri uri = Uri.parse(firstLinkUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else {
                        String anotherSearchUrl = "https://yandex.ru/search/?text=" + service;
                        Uri uri = Uri.parse(anotherSearchUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    Log.d(TAG, "onCreate: " + e.getMessage());
                }
            });
        });

        copyButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", password);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(PasswordDetailsActivity.this, "Пароль скопирован", Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(v -> {
            // Устанавливаем результат RESULT_OK и передаем ID удаляемого пароля
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", id);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @NonNull
    private static String getString(String searchUrl) throws IOException {
        URL url = new URL(searchUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    private ArrayList<String> extractLinks(String htmlContent) {
        ArrayList<String> links = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);
        Elements linkElements = doc.select("a[href]");
        for (Element linkElement : linkElements) {
            String linkUrl = linkElement.absUrl("href");
            links.add(linkUrl);
        }
        return links;
    }

    private void animateClick(View view) {
        Animation animation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);

        loadingProgressBar.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingProgressBar.setVisibility(View.GONE); // Скрыть ProgressBar после задержки
        }, 5000); // Задержка в миллисекундах
    }
}
