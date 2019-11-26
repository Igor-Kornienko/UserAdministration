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

    @Test(expected = NonValidAccessTokenException.class)
    public void test2CodeExchangeNullValue() throws InterruptedException, NonValidAccessTokenException, IOException {
        String accessToken = "";
        authService.codeExchange(accessToken);
    }

    @Test
    public void test4SignIn() throws InterruptedException {
        userElasticsearchService.deleteByEmail("testUser@gmail.com");
        Thread.sleep(1000);
        System.out.println(userElasticsearchService.userExist("testUser@gmail.com"));

        assertEquals(authService.signUp("testUser@gmail.com", "testUser", "testUser"),
                ResponseEntity.ok("user created"));
        Thread.sleep(1000);
        assertEquals(authService.signUp("testUser@gmail.com", "testUser", "testUser"),
                ResponseEntity.badRequest().body("This email already in use"));

        String jwt = authService.signIn("testUser@gmail.com", "testUser");
        System.out.println(jwt);
        assertTrue(StringUtils.startsWithIgnoreCase(jwt, "Bearer "));
    }

    @Test(expected = Exception.class)
    public void test5SignInFail() {
        authService.signIn("testUser@gmail.com", "testUsr");
    }
}
