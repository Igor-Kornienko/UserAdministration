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
import kornienko.model.User;
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
    AuthenticationManager authenticationManager;

    @Autowired
    UserElasticsearchService userElasticsearchService;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final static String CLIENT_SECRETS_FILE_PATH = "client_secret.json";

    private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final List<String> SCOPES = new ArrayList<>(Arrays.asList(DriveScopes.DRIVE, PlusScopes.USERINFO_EMAIL, PlusScopes.PLUS_LOGIN));

    private NetHttpTransport HTTP_TRANSPORT;
    private GoogleClientSecrets clientSecrets;
    private GoogleAuthorizationCodeFlow flow;

    public AuthService() throws GeneralSecurityException, IOException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream(new java.io.File(CLIENT_SECRETS_FILE_PATH))));

        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .build();
    }

    public ResponseEntity<?> googleAuthUrl(){
        String url =  flow
                .newAuthorizationUrl()
                .setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0))
                .setAccessType("offline")
                .build();
        return ResponseEntity.ok(url);
    }

    public ResponseEntity<?> codeExchange(String accessToken) throws IOException, InterruptedException {
        if (!accessToken.equals("")) {
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(accessToken).setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0)).execute();
            String id = userElasticsearchService.userExist(tokenResponse.parseIdToken().getPayload().getEmail());
            System.out.println(id);

            if (id == null) {
                User user = new User();
                user.setEmail(tokenResponse.parseIdToken().getPayload().getEmail());
                user.setPassHash(passwordEncoder.encode(tokenResponse.parseIdToken().getPayload().getEmail()));
                user.setGoogleAuth(true);
                user.setGoogleTokenResponse(tokenResponse);
                user.setName((String) tokenResponse.parseIdToken().getPayload().get("name"));
                user.setRole("ROLE_USER");
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
            return ResponseEntity.ok("Bearer " + tokenProvider.generateToken(authentication));
        }
        return ResponseEntity.badRequest().body("Not valid access token");
    }

    public ResponseEntity<?> signIn(String name, String password){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        name,
                        password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok("Bearer " + tokenProvider.generateToken(authentication));
    }

    public ResponseEntity<?> signUp(String email, String password, String name){

        String id = userElasticsearchService.userExist(email);
        System.out.println(id);
        if (!(id == null)) {
            return ResponseEntity.badRequest().body("This email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setGoogleAuth(false);
        user.setRole("ROLE_USER");
        userElasticsearchService.saveUser(user);

        return ResponseEntity.ok("user created");
    }

    public ResponseEntity<?> users(){
        return ResponseEntity.ok(userElasticsearchService.searchAllTestData());
    }
}