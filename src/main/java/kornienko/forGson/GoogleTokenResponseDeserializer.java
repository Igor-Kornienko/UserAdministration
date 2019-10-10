package kornienko.forGson;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class GoogleTokenResponseDeserializer implements JsonDeserializer<GoogleTokenResponse> {
    @Override
    public GoogleTokenResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        GoogleTokenResponse googleTokenResponse = new GoogleTokenResponse();

        if (jsonObject.has("access_token")) googleTokenResponse.setAccessToken(jsonObject.get("access_token").getAsString());
        if (jsonObject.has("expires_in")) googleTokenResponse.setExpiresInSeconds(jsonObject.get("expires_in").getAsLong());
        if (jsonObject.has("id_token")) googleTokenResponse.setIdToken(jsonObject.get("id_token").getAsString());
        if (jsonObject.has("scope")) googleTokenResponse.setScope(jsonObject.get("scope").getAsString());
        if (jsonObject.has("token_type")) googleTokenResponse.setTokenType(jsonObject.get("token_type").getAsString());

        return googleTokenResponse;
    }
}
