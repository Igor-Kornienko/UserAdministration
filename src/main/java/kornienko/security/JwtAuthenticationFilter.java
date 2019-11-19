package kornienko.security;

import kornienko.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private String tokenPrefix;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter (String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        try {
            String jwt = resolveToken(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken auth = jwtTokenProvider.authenticate(jwt);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            System.out.println("Couldn`t set user authentication in security context " + e);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request){
        String token = request.getParameter(HttpHeaders.AUTHORIZATION);
        System.out.println(token);
        if (Objects.isNull(token)) {
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
            System.out.println(token);
        }

        if (token != null && token.startsWith(this.tokenPrefix)){
            return token.substring(this.tokenPrefix.length() + 1);
        }

        return null;
    }
}
