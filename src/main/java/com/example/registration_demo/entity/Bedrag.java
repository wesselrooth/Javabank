package com.example.registration_demo.entity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class Bedrag {


    @DecimalMin(value = "0.00", message = "Het bedrag is te laag" )
    private BigDecimal pinbedrag;

    @Override
    public String toString(){
        return "Bedrag: " + this.pinbedrag;
    }

    public BigDecimal getPinbedrag() {
        return pinbedrag;
    }
    public void setPinbedrag(BigDecimal pinbedrag) {
        this.pinbedrag = pinbedrag;
    }
}
