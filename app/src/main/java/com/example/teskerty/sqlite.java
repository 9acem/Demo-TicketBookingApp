package com.example.teskerty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class sqlite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 5;

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EMAIL = "email";


    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    // Table Tickets
    private static final String TABLE_TICKETS = "tickets";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STADIUM_NAME = "stadiumName";
    private static final String COLUMN_MATCH_TIME = "matchTime";
    private static final String COLUMN_LEFT_LOGO = "leftLogo";
    private static final String COLUMN_RIGHT_LOGO = "rightLogo";

    public sqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT)");
        Log.d("Database", "Users table created");

        // Create Tickets table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TICKETS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STADIUM_NAME + " TEXT, " +
                COLUMN_MATCH_TIME + " TEXT, " +
                COLUMN_LEFT_LOGO + " TEXT, " +
                COLUMN_RIGHT_LOGO + " TEXT)");
        Log.d("Database", "Tickets table created");

        // Insert Default Admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_NAME, "Admin");
        adminValues.put(COLUMN_LAST_NAME, "Default");
        adminValues.put(COLUMN_EMAIL, "admin@gmail.com");
        adminValues.put(COLUMN_PASSWORD, "admin123");
        adminValues.put(COLUMN_ROLE, "admin_role");
        db.insert(TABLE_USERS, null, adminValues);
        Log.d("Database", "Default admin added");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the 'job_posts' table if it exists
        db.execSQL("DROP TABLE IF EXISTS job_posts");

        // Steps to drop 'mobile' column from 'users' table
        db.execSQL("CREATE TEMPORARY TABLE users_backup AS SELECT id, name, last_name, email, password, role FROM users");
        db.execSQL("DROP TABLE users");
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT)");
        db.execSQL("INSERT INTO users (id, name, last_name, email, password, role) SELECT id, name, last_name, email, password, role FROM users_backup");
        db.execSQL("DROP TABLE users_backup");

        Log.d("Database", "Dropped 'job_posts' table and removed 'mobile' column from users table");

        onCreate(db); // Re-create other tables
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("sqlite", "Downgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // User Methods
    public boolean insertUser(String name, String lastName, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ROLE + " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{email, password});

        if (cursor != null && cursor.moveToFirst()) {
            String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            cursor.close();
            return role;
        }
        return null;
    }

    public ArrayList<User> getAllUsersList() {
        ArrayList<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                userList.add(new User(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
                ));

            }
            cursor.close();
        }
        return userList;
    }
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("last_name", user.getLastName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        db.update("users", values, "email = ?", new String[]{user.getEmail()});
        db.close();
    }

    public void deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
    }

    // Ticket Methods
    public void addTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STADIUM_NAME, ticket.getStadiumName());
        values.put(COLUMN_MATCH_TIME, ticket.getMatchTime());
        values.put(COLUMN_LEFT_LOGO, ticket.getLeftLogo());
        values.put(COLUMN_RIGHT_LOGO, ticket.getRightLogo());
        db.insert(TABLE_TICKETS, null, values);
        db.close();
    }


    public ArrayList<Ticket> getAllTickets() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TICKETS, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                tickets.add(new Ticket(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STADIUM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MATCH_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LEFT_LOGO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RIGHT_LOGO))
                ));
            }
            cursor.close();
        }
        db.close();
        return tickets;
    }

    public void updateTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("stadiumName", ticket.getStadiumName());
        values.put("matchTime", ticket.getMatchTime());
        values.put("leftLogo", ticket.getLeftLogo());
        values.put("rightLogo", ticket.getRightLogo());

        db.update("tickets", values, "id = ?", new String[]{String.valueOf(ticket.getId())});
        db.close();
    }


    public void deleteTicket(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TICKETS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            );
            cursor.close();
            return user;
        }
        return null;
    }
}
