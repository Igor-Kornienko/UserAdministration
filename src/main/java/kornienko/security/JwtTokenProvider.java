package kornienko.security;

import io.jsonwebtoken.*;
import kornienko.handler.JwtExceptionHandler;
import kornienko.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${security.jwtSecret}")
    private String jwtSecret;

    @Value("${security.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    JwtExceptionHandler jwtExceptionHandler;

    public UsernamePasswordAuthenticationToken authenticate(String jwt) throws AuthenticationException {
        UserDetails userDetails = getUserDetails(jwt);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public UserDetails getUserDetails (String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return customUserDetailService.loadUserByUsername(claims.getSubject());
    }

    public boolean validateToken (String jwt) {
        try {
            Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                    .parseClaimsJws(jwt);
            return true;
        } catch (SignatureException e) {
            jwtExceptionHandler.handleSignatureException(e);
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }

    public String getUserEmailFromJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
