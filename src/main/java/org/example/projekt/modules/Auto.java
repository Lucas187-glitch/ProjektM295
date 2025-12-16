package org.example.projekt.modules;

import java.time.LocalDate;

public class Auto {
    // Attribute
    private int idAutos;
    private String modell;
    private LocalDate baujahr;        // DATE -> LocalDate
    private double gewicht;           // DECIMAL(6,2) -> double
    private int leistung;
    private boolean verbrenner;       // TINYINT -> boolean
    private boolean produktion;       // TINYINT -> boolean

    // Beziehung zur Marke (1:n - ein Auto hat eine Marke)
    private int fsMarken;             // Foreign Key als int
    private Marke marke;              // Das zugehörige Marke-Objekt

    // Leerer Constructor
    public Auto() {
    }

    // Vollständiger Constructor
    public Auto(int idAutos, String modell, LocalDate baujahr, double gewicht,
                int leistung, boolean verbrenner, boolean produktion,
                int fsMarken, Marke marke) {
        this.idAutos = idAutos;
        this.modell = modell;
        this.baujahr = baujahr;
        this.gewicht = gewicht;
        this.leistung = leistung;
        this.verbrenner = verbrenner;
        this.produktion = produktion;
        this.fsMarken = fsMarken;
        this.marke = marke;
    }

    // Constructor ohne ID (für INSERT)
    public Auto(String modell, LocalDate baujahr, double gewicht,
                int leistung, boolean verbrenner, boolean produktion,
                int fsMarken) {
        this.modell = modell;
        this.baujahr = baujahr;
        this.gewicht = gewicht;
        this.leistung = leistung;
        this.verbrenner = verbrenner;
        this.produktion = produktion;
        this.fsMarken = fsMarken;
    }

    // Alle Getter und Setter
    public int getIdAutos() {
        return idAutos;
    }

    public void setIdAutos(int idAutos) {
        this.idAutos = idAutos;
    }

    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    public LocalDate getBaujahr() {
        return baujahr;
    }

    public void setBaujahr(LocalDate baujahr) {
        this.baujahr = baujahr;
    }

    public double getGewicht() {
        return gewicht;
    }

    public void setGewicht(double gewicht) {
        this.gewicht = gewicht;
    }

    public int getLeistung() {
        return leistung;
    }

    public void setLeistung(int leistung) {
        this.leistung = leistung;
    }

    public boolean isVerbrenner() {
        return verbrenner;
    }

    public void setVerbrenner(boolean verbrenner) {
        this.verbrenner = verbrenner;
    }

    public boolean isProduktion() {
        return produktion;
    }

    public void setProduktion(boolean produktion) {
        this.produktion = produktion;
    }

    public int getFsMarken() {
        return fsMarken;
    }

    public void setFsMarken(int fsMarken) {
        this.fsMarken = fsMarken;
    }

    public Marke getMarke() {
        return marke;
    }

    public void setMarke(Marke marke) {
        this.marke = marke;
    }

    @Override
    public String toString() {
        return "Auto{" +
                "idAutos=" + idAutos +
                ", modell='" + modell + '\'' +
                ", baujahr=" + baujahr +
                ", gewicht=" + gewicht +
                ", leistung=" + leistung +
                ", verbrenner=" + verbrenner +
                ", produktion=" + produktion +
                ", fsMarken=" + fsMarken +
                ", marke=" + marke +
                '}';
    }
}
