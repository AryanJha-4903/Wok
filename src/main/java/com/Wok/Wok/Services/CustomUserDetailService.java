package com.Wok.Wok.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.Wok.Wok.Model.user;
import com.Wok.Wok.Repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<user> user = repository.findByUsername(username);
        

        if (user.isPresent()) {
            var obj = user.get();
            System.out.println("User found: " + obj.getUsername() + ", ID: " + obj.getId());
            return User.builder()
                .username(obj.getUsername())
                // .id(obj.getId()) // Removed as User.UserBuilder does not support id
                .password(obj.getPassword()) 
                .roles(obj.getRoles().toArray(new String[0])) // Convert List<String> to String[]
                .build();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
