package io.github.j0b10.mad.myenergy.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.model.evcharger.SessionManager;
import io.github.j0b10.mad.myenergy.ui.login.LoginActivity;

public class PreferencesFragment extends PreferenceFragmentCompat {

    public static final String
            KEY_LOGIN = "login",
            KEY_DEMO = "demo",
            KEY_FETCH_RATE = "fetch_rate",
            KEY_CAR_WLTP = "car_wltp",
            KEY_CAR_BATTERY = "car_battery",
            KEY_CHARGE_PLAN_AMOUNT = "cp_amount",
            KEY_CHARGE_PLAN_TIME = "cp_time";


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        boolean loggedIn = SessionManager.getInstance(requireContext()).isLoggedIn();
        findPreference(KEY_DEMO).setEnabled(loggedIn);

        findPreference(KEY_LOGIN).setOnPreferenceClickListener(preference -> {
            SessionManager sessionManager = SessionManager.getInstance(requireContext());
            sessionManager.getAccountPreferences().removeAccount();
            sessionManager.logout();
            startActivity(new Intent(requireContext(), LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            return true;
        });

        findPreference(KEY_FETCH_RATE).setSummaryProvider(new ValueSummaryProvider(R.string.sum_fetch_rate, "s"));
        findPreference(KEY_CAR_WLTP).setSummaryProvider(new ValueSummaryProvider(R.string.sum_car_wltp, "kWh/100km"));
        findPreference(KEY_CAR_BATTERY).setSummaryProvider(new ValueSummaryProvider(R.string.sum_car_battery, "kWh"));
        findPreference(KEY_CHARGE_PLAN_AMOUNT).setSummaryProvider(new ValueSummaryProvider(R.string.sum_cp_amount, "kWh"));
        findPreference(KEY_CHARGE_PLAN_TIME).setSummaryProvider(new ValueSummaryProvider(R.string.sum_cp_time, "min"));
    }
}