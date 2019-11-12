package kornienko.model;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lombok.Data;

@Data
public class User {
    private String name;
    private String email;
    private String passHash;
    private UserRole role;
    private boolean googleAuth;
    private GoogleTokenResponse googleTokenResponse;

    public User() {}

    public User(String name, String email, String passHash, UserRole role, boolean googleAuth, GoogleTokenResponse googleTokenResponse) {
        this.name = name;
        this.email = email;
        this.passHash = passHash;
        this.role = role;
        this.googleAuth = googleAuth;
        this.googleTokenResponse = googleTokenResponse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public boolean getGoogleAuth(){
        return googleAuth;
    }

    public void setGoogleAuth(boolean googleAuth) {
        this.googleAuth = googleAuth;
    }

    public GoogleTokenResponse getGoogleTokenResponse() {
        return googleTokenResponse;
    }

    public void setGoogleTokenResponse(GoogleTokenResponse googleTokenResponse) {
        this.googleTokenResponse = googleTokenResponse;
    }

    public String getRole(){
        return role.name();
    }

    public void setRole(String role){
        this.role = UserRole.valueOf(role.toUpperCase());
    }
}
