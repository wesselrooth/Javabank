package com.example.registration_demo.security;

import com.example.registration_demo.entity.Role;
import com.example.registration_demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

//public class MyUserDetails implements UserDetails {
//
//    private User user;
//
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        System.out.println("--> GET ROLES");
//        List<Role> roles = user.getRoles();
//        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//
//        for (Role role : roles) {
//            authorities.add(new SimpleGrantedAuthority(role.getName()));
//        }
//
//        return authorities;
//    }
//    @Override
//    public String getUsername() {
//        return user.getUsername();
//    }
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return user.isEnabled();
//    }
//}
