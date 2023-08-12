package com.lithan.abcjobs.security;

import com.lithan.abcjobs.constraint.ERole;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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

    static String ROLE_ADMIN = "ROLE_" + ERole.ADMIN.toString();
    static String ROLE_USER = "ROLE_" + ERole.USER.toString();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureForwardUrl("/login?error")
                .permitAll()
                .defaultSuccessUrl("/loginSuccess")
                .and()
                .csrf()
                .and()
                .authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, "/register").permitAll()
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/u/**").permitAll()
                .antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ROLE_ADMIN)
                .antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ROLE_ADMIN)
                .antMatchers(HttpMethod.POST, "/saveUpdateUserProfile").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/create-thread").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
                .antMatchers(HttpMethod.POST, "/saveThreadComment").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/deleteThread").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/deleteJob").hasAnyAuthority(ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/add-experience").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/edit-experience").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
                .antMatchers(HttpMethod.POST, "/saveExperience").hasAnyAuthority(ROLE_USER, ROLE_ADMIN)
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .and()
                .formLogin();
    }
}
