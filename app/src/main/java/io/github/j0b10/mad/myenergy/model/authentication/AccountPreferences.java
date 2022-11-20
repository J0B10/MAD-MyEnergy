package io.github.j0b10.mad.myenergy.model.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ConcurrentModificationException;
import java.util.Optional;

import at.favre.lib.armadillo.Armadillo;

public final class AccountPreferences {

    private static final String KEY_IP_ADDRESS = "ip",
            KEY_USERNAME = "user",
            KEY_PASSWORD = "pass",
            KEY_AUTH_TOKEN = "token";

    private final SharedPreferences preferencesStore;

    public AccountPreferences(SharedPreferences preferencesStore) {
        this.preferencesStore = preferencesStore;
    }

    public AccountPreferences(Context context, String preferenceName) {
        this(
                Armadillo.create(context, preferenceName)
                        .encryptionFingerprint(context)
                        .build()
        );
    }

    public void putCredentials(final String ip, final String username, final String password, final String authToken) throws ConcurrentModificationException {
        SharedPreferences.Editor editor = preferencesStore.edit();
        editor.putString(KEY_IP_ADDRESS, ip);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        if (!editor.commit()) {
            throw new ConcurrentModificationException("Unable to store credentials: Multiple concurrent modifications.");
        }
    }

    public boolean isLoginStored() {
        boolean ipStored = preferencesStore.getString(KEY_IP_ADDRESS, null) != null;
        boolean userStored = preferencesStore.getString(KEY_USERNAME, null) != null;
        return ipStored && userStored;
    }

    public String getUsername() {
        return Optional.ofNullable(preferencesStore.getString(KEY_USERNAME, null))
                .orElseThrow(() -> new IllegalStateException("no username stored"));
    }

    public String getPassword() {
        return Optional.ofNullable(preferencesStore.getString(KEY_PASSWORD, null))
                .orElseThrow(() -> new IllegalStateException("no password stored"));
    }

    public String getIpAddress() {
        return Optional.ofNullable(preferencesStore.getString(KEY_IP_ADDRESS, null))
                .orElseThrow(() -> new IllegalStateException("no ip address stored"));
    }
}
