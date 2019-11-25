package serviceTest;

import kornienko.config.JUnit4Config;
import kornienko.service.AuthService;
import kornienko.service.CustomUserDetailService;
import kornienko.service.UserElasticsearchService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JUnit4Config.class)
public class CustomUserDetailServiceJUnit4Test extends Assert {
    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    UserElasticsearchService userElasticsearchService;

    @Autowired
    AuthService authService;

    @Before
    public void beforeTest() throws InterruptedException {
        authService.signUp("testUser@gmail.com", "testUser", "testUser");
    }

    @After
    public void afterTest() {
        userElasticsearchService.deleteByEmail("testUser@gmail.com");
    }

    @Test
    public void testLoadUserByUsername() {
        UserDetails test = customUserDetailService.loadUserByUsername("testUser@gmail.com");
        System.out.println(test);
    }

    @Test(expected = NullPointerException.class)
    public void testLoadUserByUsernameFail() {
        UserDetails test = customUserDetailService.loadUserByUsername("tstUser@gmail.com");
        System.out.println(test);
    }
}
