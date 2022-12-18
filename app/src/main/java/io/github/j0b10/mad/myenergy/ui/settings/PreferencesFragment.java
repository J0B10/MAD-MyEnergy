package io.github.j0b10.mad.myenergy.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.ui.login.LoginActivity;

public class PreferencesFragment extends PreferenceFragmentCompat {

    public static final String
            KEY_LOGIN = "login",
            KEY_DEMO = "demo",
            KEY_CAR_WLTP = "car_wltp",
            KEY_CAR_BATTERY = "car_battery",
            KEY_CHARGE_PLAN_AMOUNT = "cp_amount",
            KEY_CHARGE_PLAN_TIME = "cp_time";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        //noinspection ConstantConditions
        findPreference(KEY_LOGIN).setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), LoginActivity.class)
                    .putExtra(LoginActivity.PARAM_BACK_ALLOWED, true));
            return true;
        });
    }
}