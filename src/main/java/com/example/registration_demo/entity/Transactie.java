package com.example.registration_demo.entity;


import javax.persistence.*;
import javax.validation.Valid;
import java.util.Date;

/*
* Wat Ga ik doen
*  Alle rekeningen  zijn
* */
@Entity
public class Transactie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date date;

    private Double bedrag;


    @ManyToOne
    @JoinColumn(name = "bankrekening",nullable = false, insertable = true,updatable = false)
    private Bankrekening rekening;

    @ManyToOne
    @JoinColumn(name = "receiver_rekening",nullable = false, insertable = true,updatable = false)

    private Bankrekening receiver_rekening;
/*
*   Getters/Setters
* */

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Date getTransactieDate() {
        return date;
    }

    public void setTransactieDate(Date transactieDate) {
        this.date = transactieDate;
    }

    public void setReceiver_rekening(Bankrekening receiver_rekening) {
        this.receiver_rekening = receiver_rekening;
    }

    public void setRekening(Bankrekening rekening) {
        this.rekening = rekening;
    }

    public Bankrekening getReceiver_rekening() {
        return receiver_rekening;
    }

    public Bankrekening getRekening() {
        return rekening;
    }

    public void setBedrag(Double bedrag) {
        this.bedrag = bedrag;
    }

    public Double getBedrag() {
        return bedrag;
    }
}
