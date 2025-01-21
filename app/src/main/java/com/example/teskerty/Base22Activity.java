package com.example.teskerty;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public abstract class Base22Activity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupD(int layoutResID) {
        setContentView(layoutResID);

        drawerLayout = findViewById(R.id.drawer);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.bar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view_user);
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigation(item);
            return true;
        });
    }

    private void handleNavigation(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_dash_tic) {
            startActivity(new Intent(this, UserDashboard.class));
        } else {startActivity(new Intent(this, UserProfil.class));}


        drawerLayout.closeDrawers();
    }
}

