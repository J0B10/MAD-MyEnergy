package io.github.j0b10.mad.myenergy.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import androidx.preference.Preference;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.model.evcharger.SessionManager;
import io.github.j0b10.mad.myenergy.ui.login.LoginActivity;
import io.github.j0b10.mad.myenergy.ui.settings.pv.InvertersListActivity;

public class PreferencesFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener {

    public static final String
            KEY_LOGIN = "login",
            KEY_DEMO = "demo",
            KEY_INVERTERS = "inverters",
            KEY_FETCH_RATE = "fetch_rate",
            KEY_CAR_WLTP = "car_wltp",
            KEY_CAR_BATTERY = "car_battery",
            KEY_CHARGE_PLAN_AMOUNT = "cp_amount",
            KEY_CHARGE_PLAN_TIME = "cp_time",
            KEY_QUICK_CHARGE_SPEED_ENABLED = "quick_charge_speed_enabled",
            KEY_QUICK_CHARGE_MAX_CURRENT = "quick_charge_max";

    private Preference pref_qc_max_curr;


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

        boolean loggedIn = SessionManager.getInstance(requireContext()).isLoggedIn();
        findPreference(KEY_DEMO).setEnabled(loggedIn);
        /*findPreference(KEY_INVERTERS).setEnabled(loggedIn);
        if (loggedIn)*/
        findPreference(KEY_INVERTERS).setOnPreferenceClickListener(pref -> {
            startActivity(new Intent(requireContext(), InvertersListActivity.class));
            return true;
        });

        findPreference(KEY_LOGIN).setOnPreferenceClickListener(pref -> {
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
        findPreference(KEY_QUICK_CHARGE_MAX_CURRENT).setSummaryProvider(new ValueSummaryProvider(R.string.sum_quick_charge_max, "A"));
        findPreference(KEY_QUICK_CHARGE_MAX_CURRENT).setEnabled(preferences.getBoolean(KEY_QUICK_CHARGE_SPEED_ENABLED, false));
        pref_qc_max_curr = findPreference(KEY_QUICK_CHARGE_MAX_CURRENT);

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        pref_qc_max_curr.setEnabled(sharedPreferences.getBoolean(KEY_QUICK_CHARGE_SPEED_ENABLED, false));
    }
}