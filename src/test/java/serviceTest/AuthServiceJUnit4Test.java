package serviceTest;

import kornienko.config.AppConfig;
import kornienko.handler.NonValidAccessTokenException;
import kornienko.service.AuthService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AppConfig.class)
public class AuthServiceJUnit4Test extends Assert {
    @Autowired
    private AuthService authService;

    @Test
    public void testGoogleAuthUrl() {
        String url = authService.googleAuthUrl();
        assertEquals(StringUtils.startsWithIgnoreCase(url, "https://accounts.google.com/o/oauth2/"), true);
    }

    /*@Test
    public void testCodeExchange() throws InterruptedException, NonValidAccessTokenException, IOException {
        String accessToken = "4/tQFP-xmxbHQJvNdoJtHeeTu1bTdnEnnExk5ydHRrmJrQcEnS6hXlQkDg-k2qXr3yOQVH2F9C-wXuWdTDWQ7cXP0";
        String jwt = authService.codeExchange(accessToken);
        assertEquals(StringUtils.startsWithIgnoreCase(accessToken, "Bearer "), true);
    }*/

    @Test(expected = NonValidAccessTokenException.class)
    public void testCodeExchangeNullValue() throws InterruptedException, NonValidAccessTokenException, IOException {
        String accessToken = "";
        String jwt = authService.codeExchange(accessToken);
    }

    /*@Test(expected = NonValidAccessTokenException.class)
    public void testCodeExchangeNonValid() throws InterruptedException, NonValidAccessTokenException, IOException {
        String accessToken = "4/tQFP-xmxbHQJvNdoJtqXr3yOQVH2F9C-wXuWdTDWQ7cXP0";
        String jwt = authService.codeExchange(accessToken);
    }*/

    @Test
    public void testSignInUp() throws InterruptedException {
        assertEquals(authService .signUp("testUser@gmail.com", "testUser", "testUser"),
                ResponseEntity.ok("user created"));
        assertEquals(authService.signUp("testUser@gmail.com", "testUser", "testUser"),
                ResponseEntity.badRequest().body("This email already in use"));

        String jwt = authService.signIn("testUser", "testUser");
        assertEquals(StringUtils.startsWithIgnoreCase(jwt, "Bearer "), true);

        jwt = authService.signIn("testUser", "testUsr");
        assertEquals(StringUtils.startsWithIgnoreCase(jwt, "Bearer "), false);
    }
}
