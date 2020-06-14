package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getSupportFragmentManager().beginTransaction().replace(R.id.settingsFrame, new PreferencesFragment()).commit();
    }
}
