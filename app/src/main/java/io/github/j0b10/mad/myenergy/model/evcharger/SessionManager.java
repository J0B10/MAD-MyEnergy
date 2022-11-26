package io.github.j0b10.mad.myenergy.model.evcharger;

import android.content.Context;

import java.util.concurrent.CompletableFuture;

import io.github.j0b10.mad.myenergy.model.evcharger.authentication.AccountPreferences;

public class SessionManager {

    private static final String ACCOUNT_PREFERENCE_NAME = "myenergy.account";

    private static SessionManager instance;

    private final AccountPreferences accountPreferences;

    private boolean login;

    SessionManager(Context applicationContext) {
        this.accountPreferences = new AccountPreferences(applicationContext, ACCOUNT_PREFERENCE_NAME);
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void login(String ip, String username, String password, boolean storePassword) {
        //TODO
        accountPreferences.putCredentials(ip, username, password, storePassword);
        login = true;
    }

    public boolean isLoggedIn() {
        return login;
    }

    public AccountPreferences getAccountPreferences() {
        return accountPreferences;
    }
}
