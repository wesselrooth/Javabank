package com.example.registration_demo.security;

import com.example.registration_demo.entity.Role;
import com.example.registration_demo.entity.User;
import com.example.registration_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(usernameOrEmail);
        if(user != null){
            System.out.println("--> JAHOOR de user is gevonden !!");
//            return new org.springframework.security.core.userdetails.User(
//                    user.getEmail(),
//                    user.getPassword(),
//                    user.getRoles().stream().map((role) -> new SimpleGrantedAuthority(role.getName()))
//                            .collect(Collectors.toList()));
            return new BankUserDetails(user);
//
        }else {
            System.out.println("--> User niet gevonden");
            throw new UsernameNotFoundException("Invalid email or password");

        }
    }

    public String getEmail(Principal principal){
        User user = userRepository.findByEmail(principal.getName());
        return user.getEmail();
    }

}