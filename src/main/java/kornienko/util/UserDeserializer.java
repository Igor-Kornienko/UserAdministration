package kornienko.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kornienko.model.User;
import kornienko.model.UserRole;

import java.lang.reflect.Type;

public class UserDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
        JsonObject jsonObject = json.getAsJsonObject();
        User user = new User();

        user.setName(jsonObject.has("name") ? jsonObject.get("name").getAsString() : null);
        user.setEmail(jsonObject.has("email") ? jsonObject.get("email").getAsString() : null);
        System.out.println(jsonObject.has("role") ? jsonObject.get("role").getAsString() : UserRole.GUEST.name().toLowerCase());
        user.setRole(jsonObject.has("role") ? jsonObject.get("role").getAsString() : UserRole.GUEST.name().toLowerCase());
        user.setGoogleAuth(jsonObject.has("googleAuth") ? jsonObject.get("googleAuth").getAsBoolean() : false);
        user.setPassHash(jsonObject.has("passHash") ? jsonObject.get("passHash").getAsString() : null);
        if (user.getGoogleAuth()) {
            user.setGoogleTokenResponse(context.deserialize(jsonObject.get("googleTokenResponse"), GoogleTokenResponse.class));
        }

        return user;
    }
}
