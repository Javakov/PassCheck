package com.example.meetupsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView passwordsRecyclerView;
    private Button addPasswordButton;
    private PasswordAdapter passwordAdapter;
    private List<Password> passwordList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordsRecyclerView = findViewById(R.id.passwordsRecyclerView);
        addPasswordButton = findViewById(R.id.addPasswordButton);

        databaseHelper = new DatabaseHelper(this);
        passwordList = databaseHelper.getAllPasswords();
        passwordAdapter = new PasswordAdapter(passwordList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        passwordsRecyclerView.setLayoutManager(layoutManager);
        passwordsRecyclerView.setAdapter(passwordAdapter);

        addPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPasswordActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            int id = 0;
            String service = data.getStringExtra("service");
            String password = data.getStringExtra("password");

            if (service != null && password != null) {
                Password newPassword = new Password(id, service, password);
                passwordList.add(newPassword);
                passwordAdapter.notifyDataSetChanged();

                databaseHelper.addPassword(newPassword);
            }
        }
    }
}
