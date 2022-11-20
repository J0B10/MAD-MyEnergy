package io.github.j0b10.mad.myenergy;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

import io.github.j0b10.mad.myenergy.model.authentication.AccountPreferences;
import io.github.j0b10.mad.myenergy.ui.login.LoginActivity;

/**
 * My Energy main application class .
 */
public class MyEnergyApp extends Application {

    private static final String ACCOUNT_PREFERENCE_NAME = "myenergy.account";

    private AccountPreferences account;


    public void requireLogin(ComponentActivity activity) {
        if (account == null) {
            account = new AccountPreferences(getApplicationContext(), ACCOUNT_PREFERENCE_NAME);
        }
        if (!account.isLoginStored()) {
            activity.registerForActivityResult(new StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data  = result.getData();
                    String ipAddress = data.getStringExtra(LoginActivity.PARAM_EV_CHARGER_IP);
                    String user = data.getStringExtra(LoginActivity.PARAM_USERNAME);
                    String password = data.getStringExtra(LoginActivity.PARAM_PASSWORD);
                    account.putCredentials(ipAddress, user, password);
                }
            }).launch(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}
