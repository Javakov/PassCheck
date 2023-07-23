package com.example.meetupsync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "passwords_db";
    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SERVICE = "service";
    private static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PASSWORDS_TABLE = "CREATE TABLE " + TABLE_PASSWORDS +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_SERVICE + " TEXT," +
                COLUMN_PASSWORD + " TEXT" +
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
        values.put(COLUMN_PASSWORD, password.getPassword());

        db.insert(TABLE_PASSWORDS, null, values);
        db.close();
    }

    public List<Password> getAllPasswords() {
        List<Password> passwordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PASSWORDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String service = cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE));
                String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));

                Password passwordObj = new Password(id, service, password);
                passwordList.add(passwordObj);

                cursor.moveToNext();
            }
        }


        cursor.close();
        db.close();

        return passwordList;
    }
}

