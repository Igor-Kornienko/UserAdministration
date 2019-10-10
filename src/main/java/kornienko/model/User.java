package kornienko.model;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private String email;
    private String passHash;
    private String role;
    private boolean googleAuth;
    private GoogleTokenResponse googleTokenResponse;

    public boolean getGoogleAuth(){
        return googleAuth;
    }
}
