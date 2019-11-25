package kornienko.controller;

import kornienko.handler.NonValidAccessTokenException;
import kornienko.payload.LoginRequest;
import kornienko.payload.SignUpRequest;
import kornienko.service.AuthService;
import kornienko.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    GoogleDriveService googleDriveService;

    @RequestMapping(value = "/googleAuth", method = RequestMethod.GET)
    public RedirectView googleAuthRedirect(){
        return new RedirectView(authService.googleAuthUrl());
    }

    @RequestMapping(value = "/getGoogleAuthUrl", method = RequestMethod.GET)
    public ResponseEntity<?> googleAuthUrl(){
        return ResponseEntity.ok(authService.googleAuthUrl());
    }

    @RequestMapping(value = "/codeExchange", method = RequestMethod.GET)
    public ModelAndView codeExchange(@RequestParam("code") String accessToken) throws IOException, InterruptedException, NonValidAccessTokenException {
        ModelAndView model = new ModelAndView("googleDriveFiles");
        String jwt = authService.codeExchange(accessToken);
        model.addObject("jwt", jwt);
        System.out.println(jwt);
        model.addObject("files", googleDriveService.getTop10Files(jwt));
        System.out.println(2);
        System.out.println(model);
        return model;
    }

    @RequestMapping(value="/signIn", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.signIn(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @RequestMapping(value="/signUp", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) throws InterruptedException {
        return authService.signUp(signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getName());
    }

    @RequestMapping(value= "/users", method = RequestMethod.GET)
    public ResponseEntity<?> users(){
        return authService.users();
    }
}
