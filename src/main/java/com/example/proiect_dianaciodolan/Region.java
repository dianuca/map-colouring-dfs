package com.example.proiect_dianaciodolan;

import java.util.HashSet;
import java.util.Set;

public class Region {
    private final int id; //id - ul regiunii
    private final Set<Integer> pixels; // lista pixelilor din regiune
    private final Set<Integer> neighbors; //lista vecinilor
    private int colorIndex = -1; //culoarea regiunii, initial -1 nu e colorata

    //constructor pentru crearea unei regiuni
    public Region(int id) { //
        this.id = id; //ii dau un id la regiune
        this.pixels = new HashSet<>(); //creez un set gol de pixeli
        this.neighbors = new HashSet<>(); //creez un set gol de vecini
        this.colorIndex = -1;
    }
    public int getId() { //returneaza id ul sectiunii
        return id;
    }
    public Set<Integer> getPixels() { //returnez pixelii din regiune
        return pixels;
    }
    public void addPixel(int index) { //metoda de adaugare al unui pixel
        pixels.add(index);
    }
    public Set<Integer> getNeighbors() { //returnez toti pixelii regiunii
        return neighbors;
    }
    public void addNeighbor(int neighborId) { //metoda de adaugare al unui vecin
        if (neighborId != id) {
            neighbors.add(neighborId);
        }
    }
    public int getCuloareRegiune() { //metoda de returnare a culorii unei regiuni
        return colorIndex;
    }
    public void setCuloareRegiune(int colorIndex) { //metoda de setarea a culorii unei regiuni
        this.colorIndex = colorIndex;
    }
    @Override
    public String toString() {
        return "Regiune{id=" + id +
                ", pixeli=" + pixels.size() +
                ", vecini=" + neighbors +
                ", culoare=" + colorIndex + "}";
    }
}
