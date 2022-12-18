package io.github.j0b10.mad.myenergy.model.evcharger;

import io.github.j0b10.mad.myenergy.model.evcharger.authentication.AuthenticationGrantType;
import io.github.j0b10.mad.myenergy.model.evcharger.authentication.Token;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface EVChargerAuth {

    @FormUrlEncoded
    @POST("/api/v1/token")
    Call<Token> login(@Field("grant_type") AuthenticationGrantType grantType,
                      @Field("username") String username,
                      @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/v1/token")
    Call<Token> login(@Field("grant_type") AuthenticationGrantType grantType,
                      @Field("refresh_token") String refreshToken);
}
