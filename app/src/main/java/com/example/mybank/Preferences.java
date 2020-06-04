package com.example.mybank;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class Preferences extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
