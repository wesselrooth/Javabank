package com.example.registration_demo.entity;

import com.example.registration_demo.repository.BankRekeningRepository;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name="bankrekening")
public class Bankrekening {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "Vul de naam in van de rekening")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private double saldo;

    @Override
    public String toString() {
        return this.name;
    }

    @OneToMany(mappedBy = "rekening" ,cascade= CascadeType.ALL)
    private Set<Transactie> transacties;

    @OneToMany(mappedBy = "receiver_rekening", cascade= CascadeType.ALL)
    private Set<Transactie> ontvangen_transacties;
    /*
    * Getter / Setters
    */
    public Long getId(){
        return this.id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    /*
    Wat heb ik nodig
    Rekenig Repo
    Rekening van user,
    Bedrag waarmee gerekend moet worden

    */

    public void subtractSaldo(double bedrag){
        double saldo = this.getSaldo();

        saldo = saldo - bedrag;

        this.setSaldo(saldo);
    }
    public void addSaldo(double bedrag){
        System.out.println("--> add Saldo");
        double saldo = this.getSaldo();

        saldo = saldo + bedrag;
        this.setSaldo(saldo);
        System.out.println(this.saldo);
    }
}
