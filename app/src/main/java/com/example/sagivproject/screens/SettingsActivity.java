package com.example.sagivproject.screens;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.sagivproject.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(requireContext());

            // מאזין לשינויים בהגדרות
            prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {

                // מצב כהה / בהיר
                if (key.equals("dark_mode")) {
                    boolean isDark = sharedPreferences.getBoolean("dark_mode", false);
                    AppCompatDelegate.setDefaultNightMode(
                            isDark
                                    ? AppCompatDelegate.MODE_NIGHT_YES
                                    : AppCompatDelegate.MODE_NIGHT_NO
                    );
                }

                // שינוי שפה
                if (key.equals("language")) {
                    String lang = sharedPreferences.getString("language", "he");
                    setLocale(lang);
                }
            });
        }

        private void setLocale(String lang) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);

            requireActivity().getResources().updateConfiguration(
                    config,
                    requireActivity().getResources().getDisplayMetrics()
            );

            // רענון האקטיביטי
            requireActivity().recreate();
        }
    }
}