package com.lithan.abcjobs.security;

import com.lithan.abcjobs.constraint.ERole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("At Authen configure");
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    String ROLE_ADMIN = "ROLE_" + ERole.ADMIN.toString();
    String ROLE_USER = "ROLE_" + ERole.USER.toString();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
//                .failureUrl("/login_error")
                .permitAll()
                .and()
                .csrf()
                .and()
                .authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, "/register").permitAll()
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/u/**").permitAll()
                .antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ROLE_ADMIN)
                .antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ROLE_ADMIN)
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .and()
                .formLogin()
                .defaultSuccessUrl("/", false) // Default success URL for ROLE_USER
                .and()
                .formLogin()
                .defaultSuccessUrl("/admin", false); // Default success URL for ROLE_ADMIN
    }
}
