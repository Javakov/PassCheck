package com.example.meetupsync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "passwords_db";
    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SERVICE = "service";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_COMMENT = "comment";
    private EncryptionHelper encHelp;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PASSWORDS_TABLE = "CREATE TABLE " + TABLE_PASSWORDS +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_SERVICE + " TEXT," +
                COLUMN_LOGIN + " TEXT," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_COMMENT + " TEXT" +
                ")";
        db.execSQL(CREATE_PASSWORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        onCreate(db);
    }

    public void addPassword(Password password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE, password.getService());
        values.put(COLUMN_LOGIN, encHelp.encrypt(password.getLogin()));
        values.put(COLUMN_PASSWORD, encHelp.encrypt(password.getPassword()));
        values.put(COLUMN_COMMENT, password.getComment());


        db.insert(TABLE_PASSWORDS, null, values);
        updateIds();
        db.close();
    }

    public void deletePasswordById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DatabaseHelper", "Deleting password with ID: " + id);

        int rowsDeleted = db.delete(TABLE_PASSWORDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        Log.d("DatabaseHelper", "Rows deleted: " + rowsDeleted);

        updateIds();

        db.close();
    }

    public void updateIds() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PASSWORDS, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int currentIndex = 1;

            do {
                int id = cursor.getInt(idIndex);
                ContentValues values = new ContentValues();
                values.put(COLUMN_ID, currentIndex);
                db.update(TABLE_PASSWORDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
                currentIndex++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }


    public List<Password> getAllPasswords() {
        List<Password> passwordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PASSWORDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Password password = new Password("сервис", "логин", "пароль", "комментарий");
                password.setService(cursor.getString(1));
                password.setLogin(encHelp.decrypt(cursor.getString(2)));
                password.setPassword(encHelp.decrypt(cursor.getString(3)));
                password.setComment(cursor.getString(4));

                passwordList.add(password);
            } while (cursor.moveToNext());
        }
        Log.d("DatabaseHelper", "Password list size: " + passwordList.size());

        cursor.close();
        db.close();
        return passwordList;
    }

    public List<Password> getPasswordsByService(String searchText) {
        List<Password> passwordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PASSWORDS + " WHERE " + COLUMN_SERVICE + " LIKE '%" + searchText + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Password password = new Password("сервис", "логин", "пароль", "комментарий");
                password.setService(cursor.getString(1));
                password.setLogin(cursor.getString(2));
                password.setPassword(cursor.getString(3));
                password.setComment(cursor.getString(4));
                passwordList.add(password);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return passwordList;
    }


}

