package io.github.j0b10.mad.myenergy.model.evcharger.authentication;

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
    private String password;

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

    public void putCredentials(final String ip, final String username, final String password, final boolean storePassword) throws ConcurrentModificationException {
        SharedPreferences.Editor editor = preferencesStore.edit()
                .putString(KEY_IP_ADDRESS, ip)
                .putString(KEY_USERNAME, username);
        if (storePassword) editor.putString(KEY_PASSWORD, password);
        if (!editor.commit()) {
            throw new ConcurrentModificationException("Unable to store credentials: Multiple concurrent modifications.");
        }
        this.password = password;
    }

    public void putToken(final String token) throws ConcurrentModificationException {
        SharedPreferences.Editor editor = preferencesStore.edit()
                .putString(KEY_AUTH_TOKEN, token);
        if (!editor.commit()) {
            throw new ConcurrentModificationException("Unable to store credentials: Multiple concurrent modifications.");
        }

    }

    public boolean isLoginStored() {
        boolean ipStored = preferencesStore.getString(KEY_IP_ADDRESS, null) != null;
        boolean userStored = preferencesStore.getString(KEY_USERNAME, null) != null;
        return ipStored && userStored;
    }

    public boolean isPasswordStored() {
        return preferencesStore.getString(KEY_PASSWORD, null) != null;
    }

    public boolean isPasswordAvailable() {
        return password != null || preferencesStore.getString(KEY_PASSWORD, null) != null;
    }

    public boolean isTokenStored() {
        return preferencesStore.getString(KEY_AUTH_TOKEN, null) != null;
    }

    public String getIpAddress() {
        return Optional.ofNullable(preferencesStore.getString(KEY_IP_ADDRESS, null))
                .orElseThrow(() -> new IllegalStateException("no ip address stored"));
    }

    public String getUsername() {
        return Optional.ofNullable(preferencesStore.getString(KEY_USERNAME, null))
                .orElseThrow(() -> new IllegalStateException("no username stored"));
    }

    public String getPassword() {
        if (password != null) return password;
        else if (isPasswordStored()) return preferencesStore.getString(KEY_PASSWORD, null);
        else throw new IllegalStateException("no password stored");
    }

    public String getAuthToken() {
        return Optional.ofNullable(preferencesStore.getString(KEY_AUTH_TOKEN, null))
                .orElseThrow(() -> new IllegalStateException("no token stored"));
    }

    public void removeAccount() {
        SharedPreferences.Editor editor = preferencesStore.edit()
                .remove(KEY_IP_ADDRESS)
                .remove(KEY_USERNAME)
                .remove(KEY_PASSWORD)
                .remove(KEY_AUTH_TOKEN);
        if (!editor.commit()) {
            throw new ConcurrentModificationException("unable to remove account: Multiple concurrent modifications.");
        }
        this.password = null;
    }
}
