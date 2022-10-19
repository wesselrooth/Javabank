package com.example.registration_demo.repository;

import com.example.registration_demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByname(String name);

}
