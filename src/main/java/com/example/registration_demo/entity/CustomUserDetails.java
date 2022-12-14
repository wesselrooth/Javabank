package com.example.registration_demo.entity;

import lombok.Builder;

public class CustomUserDetails extends User{

    private final String name;

    private CustomUserDetails(Builder builder) {
        super(builder.username, builder.password, builder.authorities);
        this.name = builder.name;

    }
}
