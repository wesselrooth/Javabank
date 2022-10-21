package com.example.registration_demo.repository;

import com.example.registration_demo.entity.BankRekening;
import com.example.registration_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yaml.snakeyaml.events.Event;

public interface BankRekeningRepository extends JpaRepository<BankRekening, Long> {
//    BankRekening findByid(Long id);
    BankRekening findBankRekeningByUser(User user);
    BankRekening findBankRekeningById(Long id);
}



