package com.lithan.abcjobs.security;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.UserNotActivatedException;
import com.lithan.abcjobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl() {
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Incorrect username or password!");
        }
        org.springframework.security.core.userdetails.User.UserBuilder userBuilder = org.springframework.security.core.userdetails.User.builder();

        if (!user.getIsActive()) {
            throw new UserNotActivatedException("Please activate your account first!");
        }
        String roleName = user.getRole();
        System.out.println("User role: " + roleName);
        return userBuilder.username(user.getUsername())
                .password(user.getPassword())
                .roles(roleName)
                .build();
    }
}
