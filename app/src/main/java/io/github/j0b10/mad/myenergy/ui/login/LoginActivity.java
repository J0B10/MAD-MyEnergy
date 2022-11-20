package io.github.j0b10.mad.myenergy.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.R.attr;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;

import java.util.Optional;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    public static final String PARAM_EV_CHARGER_IP = "ip",
            PARAM_USERNAME = "user",
            PARAM_PASSWORD = "pass",
            PARAM_SAVE_PASSWORD = "save-pass",
            PARAM_BACK_ALLOWED = "back";

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

        Intent intent = getIntent();
        Optional<String> ip = Optional.ofNullable(intent.getStringExtra(PARAM_EV_CHARGER_IP));
        Optional<String> username = Optional.ofNullable(intent.getStringExtra(PARAM_USERNAME));
        backAllowed = intent.getBooleanExtra(PARAM_BACK_ALLOWED, false);

        binding.loginBtn.setOnClickListener(this::onLoginClicked);
        ipv4Filter = new IPv4AddressFilter();
        binding.loginIpInput.setFilters(new InputFilter[]{ipv4Filter});

        ip.ifPresent(binding.loginIpInput::setText);
        username.ifPresent(binding.loginUsrInput::setText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        ipv4Filter = null;
    }

    @Override
    public void onBackPressed() {
        if (backAllowed) {
            super.onBackPressed();
        } else {
            finishAffinity();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Editable ip = binding.loginIpInput.getText();
        outState.putString(PARAM_EV_CHARGER_IP, ip != null ? ip.toString() : null);
        Editable username = binding.loginUsrInput.getText();
        outState.putString(PARAM_USERNAME, username != null ? username.toString() : null);
        Editable password = binding.loginPasswdInput.getText();
        outState.putString(PARAM_PASSWORD, password != null ? password.toString() : null);
        outState.putBoolean(PARAM_SAVE_PASSWORD, binding.loginSavePasswdCheck.isChecked());
        outState.putBoolean(PARAM_BACK_ALLOWED, backAllowed);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Optional<String> ip = Optional.ofNullable(savedInstanceState.getString(PARAM_EV_CHARGER_IP));
        Optional<String> username = Optional.ofNullable(savedInstanceState.getString(PARAM_USERNAME));
        Optional<String> password = Optional.ofNullable(savedInstanceState.getString(PARAM_PASSWORD));
        boolean storePassword = savedInstanceState.getBoolean(PARAM_SAVE_PASSWORD, false);
        backAllowed = savedInstanceState.getBoolean(PARAM_BACK_ALLOWED, false);
        ip.ifPresent(binding.loginIpInput::setText);
        username.ifPresent(binding.loginUsrInput::setText);
        password.ifPresent(binding.loginPasswdInput::setText);
        binding.loginSavePasswdCheck.setChecked(storePassword);
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
            Intent result = new Intent()
                    .putExtra(PARAM_EV_CHARGER_IP, ipAddress)
                    .putExtra(PARAM_USERNAME, username)
                    .putExtra(PARAM_PASSWORD, password)
                    .putExtra(PARAM_SAVE_PASSWORD, savePassword);
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }
}
