package serviceTest;

import kornienko.config.JUnit4Config;
import kornienko.handler.NonValidAccessTokenException;
import kornienko.service.AuthService;
import kornienko.service.UserElasticsearchService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JUnit4Config.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthServiceJUnit4Test extends Assert {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserElasticsearchService userElasticsearchService;

    @Before
    public void beforeTestSignInUp(){
        userElasticsearchService.deleteByEmail("testUser@gmail.com");
    }

    @After
    public void afterTestSignInUp(){
        userElasticsearchService.deleteByEmail("testUser@gmail.com");
    }

    @Test
    public void test1GoogleAuthUrl() {
        String url = authService.googleAuthUrl();
        assertEquals(StringUtils.startsWithIgnoreCase(url, "https://accounts.google.com/o/oauth2/"), true);
    }

    /*@Test
    public void testCodeExchange() throws InterruptedException, NonValidAccessTokenException, IOException {
        String accessToken = "4/tQFP-xmxbHQJvNdoJtHeeTu1bTdnEnnExk5ydHRrmJrQcEnS6hXlQkDg-k2qXr3yOQVH2F9C-wXuWdTDWQ7cXP0";
        String jwt = authService.codeExchange(accessToken);
        assertEquals(StringUtils.startsWithIgnoreCase(accessToken, "Bearer "), true);
    }

    @Test(expected = NonValidAccessTokenException.class)
    public void testCodeExchangeNonValid() throws InterruptedException, NonValidAccessTokenException, IOException {
        String accessToken = "4/tQFP-xmxbHQJvNdoJtqXr3yOQVH2F9C-wXuWdTDWQ7cXP0";
        String jwt = authService.codeExchange(accessToken);
    }*/

    @Test(expected = NonValidAccessTokenException.class)
    public void test2CodeExchangeNullValue() throws InterruptedException, NonValidAccessTokenException, IOException {
        String accessToken = "";
        authService.codeExchange(accessToken);
    }

    @Test
    public void test3SignUp() throws InterruptedException {
        assertEquals(authService.signUp("testUser@gmail.com", "testUser", "testUser"),
                ResponseEntity.ok("user created"));
        assertEquals(authService.signUp("testUser@gmail.com", "testUser", "testUser"),
                ResponseEntity.badRequest().body("This email already in use"));
    }

    @Test
    public void test4SignIn() {
        String jwt = authService.signIn("testUser@gmail.com", "testUser");
        System.out.println(jwt);
        assertEquals(StringUtils.startsWithIgnoreCase(jwt, "Bearer "), true);
    }

    @Test(expected = Exception.class)
    public void test5SignInFail() {
        authService.signIn("testUser@gmail.com", "testUsr");
    }
}
