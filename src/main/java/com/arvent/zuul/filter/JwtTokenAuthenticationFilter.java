package com.arvent.zuul.filter;

import com.arvent.zuul.JwtConfig.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private JwtConfig config;

    public JwtTokenAuthenticationFilter(JwtConfig config) {
        this.config = config;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = httpServletRequest.getHeader(config.getHeader());

        // 2. validate the header and check the prefix
        if(header == null|| !header.startsWith(config.getPrefix()))
        {
            // If not valid, go to the next filter.
            filterChain.doFilter(httpServletRequest,httpServletResponse);

            return;
        }

        // If there is no token provided and hence the user won't be authenticated.
        // It's Ok. Maybe the user accessing a public path or asking for a token.

        // All secured paths that needs a token are already defined and secured in config class
        // If user tried to access without access token, then he won't be authenticated and an exception will be thrown.

        // 3. Get the token
        String token =  header.replace(config.getPrefix(), "");

        // exceptions might be thrown in creating the claims if for example the token is expired
        try {

            Claims claims = Jwts.parser()
                    .setSigningKey(config.getSecret().getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            if(username != null){

                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get("authorities");

                // 5. Create auth object
                // UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated/being authenticated user
                // It needs a list of authorities, which has type of GrantedAuthorities interface, where SimpleGrantedAuthority is an implementation of that interface
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username,null,authorities.stream().map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));

                // 6. Authenticate the user
                // Now, user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        }catch (Exception e)
        {
            SecurityContextHolder.clearContext();
        }
        //go to the next filter in the filter chain
        filterChain.doFilter(httpServletRequest,httpServletResponse);

    }
}
