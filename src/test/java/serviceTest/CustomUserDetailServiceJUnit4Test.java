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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JUnit4Config.class)
public class CustomUserDetailServiceJUnit4Test extends Assert {
    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    AuthService authService;

    @Test
    public void testLoadUserByUsername() throws InterruptedException {
        authService.signUp("testUser@gmail.com", "testUser", "testUser");
        Thread.sleep(1000);

        UserDetails test = customUserDetailService.loadUserByUsername("testUser@gmail.com");
        System.out.println(test);
    }

    @Test(expected = BadCredentialsException.class)
    public void testLoadUserByUsernameBadCredentials() throws InterruptedException {
        authService.signUp("testUser@gmail.com", "testUser", "testUser");
        Thread.sleep(1000);

        UserDetails test = customUserDetailService.loadUserByUsername("tstUser@gmail.com");
        System.out.println(test);
    }
}
