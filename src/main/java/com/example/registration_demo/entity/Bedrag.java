package com.example.registration_demo.entity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class Bedrag {


    @DecimalMin(value = "0.00", message = "Geen waarde" )
    private BigDecimal input_bedrag;

    @Override
    public String toString(){
        return "Bedrag: " + this.input_bedrag;
    }

    public BigDecimal getPinbedrag() {
        return input_bedrag;
    }
    public void setPinbedrag(BigDecimal pinbedrag) {
        this.input_bedrag = pinbedrag;
    }
}
