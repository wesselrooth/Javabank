package com.example.registration_demo.repository;

import com.example.registration_demo.entity.Transactie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactieRepository extends JpaRepository<Transactie, Long> {

}
