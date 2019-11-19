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

public class GoogleTokenResponseDeserializerJUnit4Test extends Assert {

    private Gson gson;
    private GoogleTokenResponse testTokenResponse;

    @Before
    public void setUpGson(){
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GoogleTokenResponse.class, new GoogleTokenResponseDeserializer())
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    @Before
    public void setUpGoogleTokenResponse(){
        testTokenResponse = new GoogleTokenResponse();
        testTokenResponse.setTokenType("Bearer");
        testTokenResponse.setScope("https://www.googleapis.com/auth/userinfo.profile openid https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/drive");
        testTokenResponse.setIdToken("eyJhbGciOiJSUzI1NiIsImtpZsdfgSDFsdghYjMwZTBiNzViOGVjZDRmODE2YmI5ZTE5NzhmNjI4NDk4OTQiLCJ0eXAiOiJKV1QifQ.eyJpfsdg97GDsd9JodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMDM4NTQwMDIwMi03MmUwYXFsajJlNGVmbDJkZjBsNWJxbjUwOTExaXNzcS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjEwMzg1NDAwMjAyLTcyZTBhcWxqMmU0ZWZsMmRmMGw1YnFuNTA5MTFpc3NxLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA0ODczMTg1MzgxNzI2NzI0OTc0IiwiZW1haWwiOiJpLmsuc2tyaWxsYXhAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJpWV9RSWV3LVNOQUEyZHJOamJiOW1RIiwibmFtZSI6ItCY0LPQvtGA0Ywg0JrQvtGA0L3QuNC10L3QutC-IiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS8tS2dlTE4tYWktNnMvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQUNIaTNyYzNxV3R0UERCTDhqVG5WVk5xSzZWRGVhcVAxQS9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoi0JjQs9C-0YDRjCIsImZhbWlseV9uYW1lIjoi0JrQvtGA0L3QuNC10L3QutC-IiwibG9jYWxlIjoicnUiLCJpYXQiOjE1NzM3MjQ2MDAsImV4cCI6MTU3MzcyODIwMH0.b9X5xyLJytZ19fVcgFOUKFNbyfJB6nL6SAgZENhonHo_fLwPPxTZ7J_tYYR_XU7YzrydU2piXXTU4C9BV2de6qYkHIBA1a50ATuiFO6Tf8Pgty6qZdN2eUCS9rjjzpvQOVp3DdUWjSXCi0QkmxcrQ45Y7e0SxOS6Befa0jyUpUdli0VvUUV6PLrFlEjYo3mXCmnyENjLSmMrG2Oi6-YPb2v5RWDNsNa3Dpi8WSHRTsAJbBm9K5_NJNBqFHRrMsE2URkOJbQarIMvSpxLMHRZrHADWSlYuosODDgn6o2CRKBOf38R4zVmo7nYPHrhKtXz9wz5SZ16W7lJuInK19ThDg");
        testTokenResponse.setExpiresInSeconds(3600L);
        testTokenResponse.setAccessToken("ya29.Il-xB7sfBGwecyalTygyjua745NC8ewtr34FlQvj_nSHWs_TKwLK9In-WCp_h-oTnSky_4oHeAXfYp6X89afa8dFSD8R0ipYxOS-PfBzUUflMDEzjfIP_lxKhmf8-FSJvW7eo38g");
        testTokenResponse.setRefreshToken("1//0cR2RKifSzNj-CgYIARAAGAwSNwF-L9asdJOKwsdW7OW5YcMtv4_yNBG-0bKl2bYpdG-YXX421rH7ltquIuNWKJHGKJEhOtDP-jOfA");
    }

    @Test
    public void testGoogleTokenResponseDeserializer(){
        String jsonBuff = gson.toJson(testTokenResponse);
        GoogleTokenResponse tokenResponse = gson.fromJson(jsonBuff, GoogleTokenResponse.class);
        assertEquals(testTokenResponse, tokenResponse);
    }
}
