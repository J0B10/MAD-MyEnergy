package io.github.j0b10.mad.myenergy.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.R.attr;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;

import io.github.j0b10.mad.myenergy.MainActivity;
import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.ActivityLoginBinding;
import io.github.j0b10.mad.myenergy.model.evcharger.SessionManager;
import io.github.j0b10.mad.myenergy.model.evcharger.authentication.AccountPreferences;
import io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment;

public class LoginActivity extends AppCompatActivity {

    public static final String PARAM_BACK_ALLOWED = "back";

    private SessionManager sessionManager;
    private ActivityLoginBinding binding;
    private IPv4AddressFilter ipv4Filter;
    private boolean backAllowed;

    private static void showErrorMsg(View context, @StringRes int resId, Object... attributes) {
        Snackbar.make(context, context.getResources().getString(resId, attributes), Snackbar.LENGTH_SHORT)
                .setBackgroundTint(MaterialColors.getColor(context, attr.colorError))
                .setTextColor(MaterialColors.getColor(context, attr.colorOnError))
                .show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        AccountPreferences accountPreferences = sessionManager.getAccountPreferences();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        boolean demoMode = preferences.getBoolean(PreferencesFragment.KEY_DEMO, false);
        if (!demoMode) {
            boolean loggedIn = SessionManager.getInstance(this).requireLoginSync(this);
            if (loggedIn) {
                onLogin();
                return;
            }
        }

        binding.loginBtn.setOnClickListener(this::onLoginClicked);
        ipv4Filter = new IPv4AddressFilter();
        binding.loginIpInput.setFilters(new InputFilter[]{ipv4Filter});

        backAllowed = getIntent().getBooleanExtra(PARAM_BACK_ALLOWED, false);
        if (accountPreferences.isLoginStored()) {
            binding.loginIpInput.setText(accountPreferences.getIpAddress());
            binding.loginUsrInput.setText(accountPreferences.getUsername());
        }
        if (accountPreferences.isPasswordStored()) {
            binding.loginPasswdInput.setText(accountPreferences.getPassword());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        ipv4Filter = null;
        sessionManager = null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(PARAM_BACK_ALLOWED, backAllowed);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        backAllowed = savedInstanceState.getBoolean(PARAM_BACK_ALLOWED, false);
    }

    private void onLoginClicked(View button) {
        Editable ipAddress = binding.loginIpInput.getText();
        Editable username = binding.loginUsrInput.getText();
        Editable password = binding.loginPasswdInput.getText();
        boolean savePassword = binding.loginSavePasswdCheck.isChecked();
        if (ipAddress == null || ipAddress.toString().isEmpty()) {
            showErrorMsg(button, R.string.error_no_ip);
        } else if (!ipv4Filter.isIPv4Address(ipAddress, true)) {
            showErrorMsg(button, R.string.error_invalid_ip, ipAddress);
        } else if (username == null || username.toString().isEmpty()) {
            showErrorMsg(button, R.string.error_no_user);
        } else if (password == null || password.toString().isEmpty()) {
            showErrorMsg(button, R.string.error_no_passwd);
        } else {
            binding.loginProgress.setVisibility(View.VISIBLE);
            sessionManager.login(
                    ipAddress.toString(),
                    username.toString(),
                    password.toString(),
                    savePassword,
                    this::onLogin,
                    error -> {
                        binding.loginProgress.setVisibility(View.GONE);
                        Log.w("login", error);
                        showErrorMsg(binding.getRoot(), R.string.login_error, error.getLocalizedMessage());
                    }
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.login_debug_sel) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            pref.edit().putBoolean(PreferencesFragment.KEY_DEMO, true).apply();
            onLogin();
            return true;
        }
        return false;
    }

    private void onLogin() {
        Intent startMain = new Intent(this, MainActivity.class);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startMain);
        Log.i("login", "Logged in!");
    }
}
