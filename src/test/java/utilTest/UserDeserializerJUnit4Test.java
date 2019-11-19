package utilTest;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kornienko.model.User;
import kornienko.util.GoogleTokenResponseDeserializer;
import kornienko.util.UserDeserializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserDeserializerJUnit4Test extends Assert {

    private Gson gson;
    private User testUser;

    @Before
    public void setUpGson(){
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GoogleTokenResponse.class, new GoogleTokenResponseDeserializer())
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    @Before
    public void setUpTestUser(){
        testUser = new User();
        testUser.setRole("USER");
        testUser.setGoogleAuth(false);
        testUser.setGoogleTokenResponse(null);
        testUser.setName("SOMEUSER");
        testUser.setPassHash("fasdasdgsgagar2233234ersaf34fq34q43re");
        testUser.setEmail("SOMEUSER@gmail.com");
    }

    @Test
    public void testUserDeserializer(){
        String jsonBuff = gson.toJson(testUser);
        User user = gson.fromJson(jsonBuff, User.class);
        assertEquals(testUser, user);
    }
}
