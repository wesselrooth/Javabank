package com.example.registration_demo.repository;

//import com.example.registration_demo.entity.BankRekening;
import com.example.registration_demo.entity.Bankrekening;
import com.example.registration_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yaml.snakeyaml.events.Event;

public interface BankRekeningRepository extends JpaRepository<Bankrekening, Long> {
//    BankRekening findByid(Long id);
    Bankrekening findBankRekeningByUser(User user);
    Bankrekening findBankRekeningById(Long id);
}



