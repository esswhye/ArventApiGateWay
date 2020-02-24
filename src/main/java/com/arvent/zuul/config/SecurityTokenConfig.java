package com.arvent.zuul.config;

import com.arvent.zuul.JwtConfig.JwtConfig;
import com.arvent.zuul.filter.JwtTokenAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

/**
 * It is recommended to add an AdminController that is not visible for the user
 */
@EnableWebSecurity
@Slf4j
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfig jwtConfig;

    @Bean
    public JwtConfig jwtConfig()
    {
        return new JwtConfig();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // handle an authorized attempts
                .exceptionHandling().authenticationEntryPoint((req,rsp,e)-> {
                    log.info("Error 404 Unauthorized User trying to access " + req.getRequestURL().toString());
                    rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                })
                .and()
                // Add a filter to validate the tokens with every request
                .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
                // Authorization requests config
                .authorizeRequests()
                //Allow all who are accessing to this services
                .antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
                //Allow all to access Customer (POST,GET,UPDATE)
                .antMatchers(HttpMethod.GET, "/customer-service/**").permitAll()
                .antMatchers(HttpMethod.POST, "/customer-service/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/customer-service/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/customer-service/**").hasRole("ADMIN")
                //Allow Only Access to VENDORS or ADMIN for POST PUT DELETE (Product)
                .antMatchers(HttpMethod.GET, "/product-service/**").permitAll()
                .antMatchers(HttpMethod.POST, "/product-service/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/product-service/**").hasAnyRole("ADMIN","VENDOR")
                .antMatchers(HttpMethod.DELETE, "/product-service/**").hasAnyRole("ADMIN","VENDOR")
                //Allow only access to ADMIN and USERS
                .antMatchers(HttpMethod.GET, "/customer-service/**").permitAll()
                .antMatchers(HttpMethod.POST, "/customer-service/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/customer-service/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/customer-service/**").hasAnyRole("ADMIN","VENDOR")
                .antMatchers("/common-service").hasRole("ADMIN")
                //.antMatchers("/actuator/**").hasAnyRole("ADMIN");
                .antMatchers("/actuator/health").permitAll()
                // Any other must be authenticated
                .anyRequest().authenticated();




    }
}
