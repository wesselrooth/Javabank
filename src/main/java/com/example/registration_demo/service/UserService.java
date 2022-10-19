package com.example.registration_demo.service;
import com.example.registration_demo.entity.User;
import com.example.registration_demo.dto.UserDto;
import java.util.List;


public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();


}
