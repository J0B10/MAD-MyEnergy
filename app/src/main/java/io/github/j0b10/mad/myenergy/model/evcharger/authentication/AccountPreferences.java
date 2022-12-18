package io.github.j0b10.mad.myenergy.model.evcharger.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ConcurrentModificationException;
import java.util.Optional;

import at.favre.lib.armadillo.Armadillo;

public final class AccountPreferences {

    private static final String KEY_IP_ADDRESS = "ip",
            KEY_USERNAME = "user",
            KEY_PASSWORD = "pass",
            KEY_REFRESH_TOKEN = "token";

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

    public void putCredentials(final String ip, final String username, @Nullable final String password) throws ConcurrentModificationException {
        SharedPreferences.Editor editor = preferencesStore.edit()
                .putString(KEY_IP_ADDRESS, ip)
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password);
        if (!editor.commit()) {
            throw new ConcurrentModificationException("Unable to store credentials: Multiple concurrent modifications.");
        }
    }

    public void putRefreshToken(final String token) throws ConcurrentModificationException {
        SharedPreferences.Editor editor = preferencesStore.edit()
                .putString(KEY_REFRESH_TOKEN, token);
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

    public boolean isRefreshTokenStored() {
        return preferencesStore.getString(KEY_REFRESH_TOKEN, null) != null;
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
        return Optional.ofNullable(preferencesStore.getString(KEY_PASSWORD, null))
                .orElseThrow(() -> new IllegalStateException("no password stored"));
    }

    public String getRefreshToken() {
        return Optional.ofNullable(preferencesStore.getString(KEY_REFRESH_TOKEN, null))
                .orElseThrow(() -> new IllegalStateException("no token stored"));
    }

    public void removeAccount() {
        SharedPreferences.Editor editor = preferencesStore.edit()
                .remove(KEY_IP_ADDRESS)
                .remove(KEY_USERNAME)
                .remove(KEY_PASSWORD)
                .remove(KEY_REFRESH_TOKEN);
        if (!editor.commit()) {
            throw new ConcurrentModificationException("unable to remove account: Multiple concurrent modifications.");
        }
    }
}
