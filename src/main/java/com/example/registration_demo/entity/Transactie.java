package com.example.registration_demo.entity;


import javax.persistence.*;
import java.util.Date;

/*
* Wat Ga ik doen
*  Alle rekeningen  zijn
* */
@Entity
public class Transactie {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bankrekening_id",nullable = false)
    private Bankrekening receiver_rekening;

    private Date transactieDate;

    @ManyToOne
    @JoinColumn(name = "bankrekening_id",nullable = false)
    private Bankrekening rekening;
}
