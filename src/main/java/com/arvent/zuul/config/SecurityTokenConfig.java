package com.arvent.zuul.config;

import com.arvent.zuul.JwtConfig.JwtConfig;
import com.arvent.zuul.filter.JwtTokenAuthenticationFilter;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

/**
 * It is recommended to add an AdminController that is not visible for the user
 */
//https://medium.com/omarelgabrys-blog/microservices-with-spring-boot-authentication-with-jwt-part-3-fafc9d7187e8

@EnableWebSecurity
@Slf4j
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfig jwtConfig;

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                cors().and().csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // handle an authorized attempts
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> {
            log.info("Error 404 Unauthorized User trying to access " + req.getRequestURL().toString());
            rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        })
                .and()
                // Add a filter to validate the tokens with every request
                .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
                // Authorization requests config
                .authorizeRequests()
                //Cors https://stackoverflow.com/questions/54255950/configure-spring-for-cors
                //.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                //Allow all who are accessing to this services
                .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
                //Allow all to access Customer (POST,GET,UPDATE)
                .antMatchers(HttpMethod.GET, "/customer-service/**", "/product-service/**", "/order-service/**").permitAll()
                .antMatchers(HttpMethod.POST, "/customer-service/**", "/product-service/**", "/order-service/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/customer-service/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/customer-service/**").hasRole("ADMIN")
                //Allow Only Access to VENDORS or ADMIN for POST PUT DELETE (Product)
                //.antMatchers(HttpMethod.GET, "/product-service/**").permitAll()
                //.antMatchers(HttpMethod.POST, "/product-service/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/product-service/**").hasAnyRole("ADMIN", "VENDOR")
                .antMatchers(HttpMethod.DELETE, "/product-service/**").hasAnyRole("ADMIN", "VENDOR")
                //Allow only access to ADMIN and USERS
                //.antMatchers(HttpMethod.GET, "/order-service/**").permitAll()
                //.antMatchers(HttpMethod.POST, "/order-service/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/order-service/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/order-service/**").hasAnyRole("ADMIN", "VENDOR")
                .antMatchers(HttpMethod.GET, "/actuator/health/**", "/actuator/prometheus", "/favicon.ico").permitAll()
                //Shutdown using CURL (must change)
                .antMatchers(HttpMethod.POST, "/actuator/shutdown/**").hasRole("ADMIN")
                //.antMatchers(HttpMethod.GET, "/actuator/prometheus").permitAll()
                // Any other must be authenticated
                .anyRequest().authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type", "username", "password"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
