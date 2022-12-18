package io.github.j0b10.mad.myenergy.model.evcharger.authentication;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Token {
    public final String access_token;
    public final long expires_in;
    public final String refresh_token;
    public final Type token_type;
    public final String uiIdleTime;

    public Token(String access_token, long expires_in, String refresh_token, Type token_type, String uiIdleTime) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.uiIdleTime = uiIdleTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return expires_in == token.expires_in
                && Objects.equals(access_token, token.access_token)
                && Objects.equals(refresh_token, token.refresh_token)
                && token_type == token.token_type && Objects.equals(uiIdleTime, token.uiIdleTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(access_token, expires_in, refresh_token, token_type, uiIdleTime);
    }

    @Override
    public String toString() {
        return "Token{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                ", refresh_token='" + refresh_token + '\'' +
                ", token_type=" + token_type +
                ", uiIdleTime='" + uiIdleTime + '\'' +
                '}';
    }

    public enum Type {
        @SerializedName("bearer") BEARER
    }
}
