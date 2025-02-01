package com.pokertrainer;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private List<Chip> chips;

    public Table() {
        chips = new ArrayList<>();
    }

    // add a chip to the table
    public void addChip(Chip chip) {
        chips.add(chip);
    }

    // calculate the total value of the pot
    public double calculatePot() {
        double total = 0;
        for (Chip chip : chips) {
            total += chip.getValue();
        }
        return total;
    }

    // clear all chips from the table
    public void clearTable() {
        chips.clear();
    }

    public List<Chip> getChips() {
        return chips;
    }
}
