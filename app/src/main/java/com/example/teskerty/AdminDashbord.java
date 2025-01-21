package com.example.teskerty;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminDashbord extends BaseActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ListView userListView;
    private sqlite dbHelper;
    private ArrayList<User> userList;
    private UserAdapter userAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu (if necessary)

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashbord);
        setupDrawer(R.layout.activity_admin_dashbord);

        // Initialize components
        userListView = findViewById(R.id.userListView);
        // Load Users
        dbHelper = new sqlite(this);
        userList = new ArrayList<>();
        loadUsers();

    }
    // Load Users into ListView
    private void loadUsers() {
        userList.clear();
        userList = dbHelper.getAllUsersList(); // Implement getAllUsersList in SQLite helper
        userAdapter = new UserAdapter(this, userList, dbHelper);
        userListView.setAdapter(userAdapter);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

