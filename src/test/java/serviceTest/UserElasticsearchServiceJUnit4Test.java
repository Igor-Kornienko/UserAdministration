package serviceTest;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.plus.PlusScopes;
import kornienko.config.JUnit4Config;
import kornienko.model.User;
import kornienko.service.UserElasticsearchService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JUnit4Config.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserElasticsearchServiceJUnit4Test extends Assert {
    @Autowired
    UserElasticsearchService userElasticsearchService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private User user;

    @Before
    public void before(){
        userElasticsearchService.deleteByEmail("testUser@gmail.com");

        user = new User();
        user.setName("testUser");
        user.setEmail("testUser@gmail.com");
        user.setPassHash(passwordEncoder.encode("testUser"));
        user.setRole("USER");
    }

    @Test
    public void testCRUDUser() throws InterruptedException {
        userElasticsearchService.saveUser(user);
        Thread.sleep(1000);

        assertNotEquals("", userElasticsearchService.userExist("testUser@gmail.com"));
        assertEquals(user, userElasticsearchService.findUserByEmail("testUser@gmail.com"));

        GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
        tokenResponse.setAccessToken("asdasdasdasdasdasdasdasda");
        tokenResponse.setRefreshToken("sdfasdfsadf");
        tokenResponse.setExpiresInSeconds(1600L);
        tokenResponse.setIdToken("asdasdasd");
        tokenResponse.setScope(PlusScopes.USERINFO_EMAIL);
        tokenResponse.setTokenType("Bearer");
        user.setGoogleTokenResponse(tokenResponse);
        user.setGoogleAuth(true);

        userElasticsearchService.updateUser(userElasticsearchService.userExist("testUser@gmail.com"), tokenResponse);
        Thread.sleep(1000);

        assertNotEquals("", userElasticsearchService.userExist("testUser@gmail.com"));
        assertEquals(user, userElasticsearchService.findUserByEmail("testUser@gmail.com"));

        userElasticsearchService.deleteByEmail("testUser@gmail.com");
        Thread.sleep(1000);

        assertNull(userElasticsearchService.userExist("testUser@gmail.com"));
        assertNull(userElasticsearchService.findUserByEmail("testUser@gmail.com"));
    }

    @Test
    public void testCheckBaseAccsData() throws InterruptedException {
        userElasticsearchService.checkBaseAccs();
        userElasticsearchService.saveUser(user);
        Thread.sleep(2000);

        Map<String, User> data = userElasticsearchService.searchAllData();
        System.out.println(data.size());
        assertTrue(data.size() > 2); // min = testUser, admin, guest

        assertNotEquals("", userElasticsearchService.userExist("guest"));
        assertNotEquals("", userElasticsearchService.userExist("admin"));
    }
}
