package kornienko.service;

import kornienko.model.User;
import kornienko.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserElasticsearchService userElasticsearchService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userElasticsearchService.findUserByEmail(email);
        return UserPrincipal.create(user);
    }
}
