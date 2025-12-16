package org.example.projekt.modules;


public class Marke {
    // Attribute (entsprechen deinen Datenbank-Spalten)
    private int idMarke;
    private String name;

    // Leerer Constructor (brauchst du für JDBC)
    public Marke() {
    }

    // Constructor mit Parametern
    public Marke(int idMarke, String name) {
        this.idMarke = idMarke;
        this.name = name;
    }

    // Constructor ohne ID (für INSERT, weil DB die ID generiert)
    public Marke(String name) {
        this.name = name;
    }

    // Getter und Setter
    public int getIdMarke() {
        return idMarke;
    }

    public void setIdMarke(int idMarke) {
        this.idMarke = idMarke;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString() für einfaches Debugging
    @Override
    public String toString() {
        return "Marke{" +
                "idMarke=" + idMarke +
                ", name='" + name + '\'' +
                '}';
    }
}
