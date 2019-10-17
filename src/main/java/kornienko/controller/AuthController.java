package kornienko.controller;

import kornienko.payload.LoginRequest;
import kornienko.payload.SignUpRequest;
import kornienko.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController()
@RequestMapping("auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @RequestMapping(value = "/getGoogleAuthUrl", method = RequestMethod.GET)
    public ResponseEntity<?> googleAuthRedirect(){
        return authService.googleAuthUrl();
    }

    @RequestMapping(value = "/codeExchange", method = RequestMethod.GET)
    public ResponseEntity<?> codeExchange(@RequestParam("code") String accessToken) throws IOException, InterruptedException {
        return authService.codeExchange(accessToken);
    }

    @RequestMapping(value="/signIn", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest loginRequest){
        return authService.signIn(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @RequestMapping(value="/signUp", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        return authService.signUp(signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getName());
    }

    @RequestMapping(value= "/users", method = RequestMethod.GET)
    public ResponseEntity<?> users(){
        return authService.users();
    }
}
