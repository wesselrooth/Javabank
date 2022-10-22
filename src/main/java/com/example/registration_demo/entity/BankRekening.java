package com.example.registration_demo.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name="bankrekening")
public class BankRekening {

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

    @OneToMany(mappedBy = "bankrekening")
    private Set<Transactie> transacties;

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
}
