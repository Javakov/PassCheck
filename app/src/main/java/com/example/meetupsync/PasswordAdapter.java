package com.example.meetupsync;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {

    private List<Password> passwordList;

    public PasswordAdapter(List<Password> passwordList) {
        this.passwordList = passwordList;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_password, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
        Password password = passwordList.get(position);

        holder.serviceTextView.setText(password.getService());
        holder.serviceTextView.setTextSize(20); // Установка размера шрифта
        holder.serviceTextView.setTypeface(holder.serviceTextView.getTypeface(), Typeface.BOLD);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password password = passwordList.get(position);
                Intent intent = new Intent(v.getContext(), PasswordDetailsActivity.class);
                intent.putExtra("service", password.getService());
                intent.putExtra("password", password.getPassword());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    public static class PasswordViewHolder extends RecyclerView.ViewHolder {

        public TextView serviceTextView;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceTextView = itemView.findViewById(R.id.serviceTextView);
        }
    }
}
