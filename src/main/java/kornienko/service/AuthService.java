package kornienko.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.plus.PlusScopes;
import kornienko.handler.NonValidAccessTokenException;
import kornienko.model.User;
import kornienko.model.UserRole;
import kornienko.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {
    @Autowired
    UserElasticsearchService userElasticsearchService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    private final static String CLIENT_SECRETS_FILE_PATH = "client_secret.json";

    private GoogleClientSecrets clientSecrets;
    private GoogleAuthorizationCodeFlow flow;

    public AuthService() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        List<String> SCOPES = new ArrayList<>(Arrays.asList(DriveScopes.DRIVE, PlusScopes.USERINFO_EMAIL, PlusScopes.PLUS_LOGIN));
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream(new java.io.File(CLIENT_SECRETS_FILE_PATH))));

        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .build();
     }

    public String googleAuthUrl(){
        return flow
                .newAuthorizationUrl()
                .setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0))
                .setAccessType("offline")
                .build();
    }

    public String codeExchange(String accessToken) throws IOException, InterruptedException, NonValidAccessTokenException {
        if (!accessToken.equals("")) {
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(accessToken).setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0)).execute();
            String id = userElasticsearchService.userExist(tokenResponse.parseIdToken().getPayload().getEmail());

            if (id == null) {
                User user = new User();
                user.setEmail(tokenResponse.parseIdToken().getPayload().getEmail());
                user.setPassHash(passwordEncoder.encode(tokenResponse.parseIdToken().getPayload().getEmail()));
                user.setGoogleAuth(true);
                user.setGoogleTokenResponse(tokenResponse);
                user.setName((String) tokenResponse.parseIdToken().getPayload().get("name"));
                user.setRole(UserRole.USER.name());
                userElasticsearchService.saveUser(user);
                System.out.println(user);
                Thread.sleep(1000);
            } else {
                userElasticsearchService.updateUser(id, tokenResponse);
            }

            User user = userElasticsearchService.findUserByEmail(tokenResponse.parseIdToken().getPayload().getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getEmail()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "Bearer " + jwtTokenProvider.generateToken(authentication);
        }
        throw new NonValidAccessTokenException("Not valid access token");
    }

    public String signIn(String name, String password){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        name,
                        password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "Bearer " + jwtTokenProvider.generateToken(authentication);
    }

    public ResponseEntity<?> signUp(String email, String password, String name) throws InterruptedException {

        String id = userElasticsearchService.userExist(email);
        if (!(id == null)) {
            return ResponseEntity.badRequest().body("This email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setGoogleAuth(false);
        user.setRole(UserRole.USER.name());
        userElasticsearchService.saveUser(user);

        return ResponseEntity.ok("user created");
    }

    public ResponseEntity<?> users(){
        return ResponseEntity.ok(userElasticsearchService.searchAllData());
    }
}