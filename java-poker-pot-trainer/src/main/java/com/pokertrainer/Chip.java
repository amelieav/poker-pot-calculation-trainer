package com.pokertrainer;

public class Chip {
    private double value; // value of the chip (e.g., 0.05 for 5p)
    private String color;

    public Chip(double value, String color) {
        this.value = value;
        this.color = color;
    }

    public double getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
