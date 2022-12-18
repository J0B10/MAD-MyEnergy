package io.github.j0b10.mad.myenergy.model.evcharger.authentication;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public enum AuthenticationGrantType {
    @SerializedName("refresh_token") REFRESH_TOKEN,
    @SerializedName("password") PASSWORD;

    @NonNull
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
