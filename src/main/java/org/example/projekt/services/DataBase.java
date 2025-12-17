package org.example.projekt.services;

import org.example.projekt.modules.Auto;
import org.example.projekt.modules.Marke;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static final String DB_URL = "jdbc:mysql://localhost:8889/Autos?user=root&password=root";

    // ==================== MARKE-Methoden ====================

    // Marke nach ID lesen
    public Marke readMarkeById(int id) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("SELECT ID_Marken, Name FROM marken WHERE ID_Marken = ?");
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        Marke marke = null;
        if (rs.next()) {
            marke = new Marke();
            marke.setIdMarke(rs.getInt("ID_Marken"));
            marke.setName(rs.getString("Name"));
        }
        con.close();
        return marke;
    }

    // Alle Marken lesen
    public List<Marke> getAllMarken() throws SQLException {
        List<Marke> marken = new ArrayList<>();
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("SELECT ID_Marken, Name FROM marken");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Marke marke = new Marke();
            marke.setIdMarke(rs.getInt("ID_Marken"));
            marke.setName(rs.getString("Name"));
            marken.add(marke);
        }
        con.close();
        return marken;
    }

    // Marke hinzufügen
    public int addMarke(Marke marke) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO marken (Name) VALUES (?)");
        pstmt.setString(1, marke.getName());
        int rowsAffected = pstmt.executeUpdate();
        con.close();
        return rowsAffected;
    }

    // Marke löschen
    public int deleteMarke(int id) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("DELETE FROM marken WHERE ID_Marken = ?");
        pstmt.setInt(1, id);
        int rowsAffected = pstmt.executeUpdate();
        con.close();
        return rowsAffected;
    }

    // ==================== AUTO-Methoden ====================

    // Auto nach ID lesen (inklusive Marke)
    public Auto readAutoById(int id) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement(
            "SELECT ID_Autos, Modell, Baujahr, Gewicht, Leistung, Verbrenner, Produktion, FS_Marken FROM autos WHERE ID_Autos = ?"
        );
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        Auto auto = null;
        if (rs.next()) {
            auto = new Auto();
            auto.setIdAutos(rs.getInt("ID_Autos"));
            auto.setModell(rs.getString("Modell"));
            auto.setBaujahr(rs.getDate("Baujahr").toLocalDate());
            auto.setGewicht(rs.getDouble("Gewicht"));
            auto.setLeistung(rs.getInt("Leistung"));
            auto.setVerbrenner(rs.getBoolean("Verbrenner"));
            auto.setProduktion(rs.getBoolean("Produktion"));
            auto.setFsMarken(rs.getInt("FS_Marken"));

            // Marke laden
            Marke marke = readMarkeById(rs.getInt("FS_Marken"));
            auto.setMarke(marke);
        }
        con.close();
        return auto;
    }

    // Alle Autos lesen (inklusive Marken)
    public List<Auto> getAllAutos() throws SQLException {
        List<Auto> autos = new ArrayList<>();
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement(
            "SELECT ID_Autos, Modell, Baujahr, Gewicht, Leistung, Verbrenner, Produktion, FS_Marken FROM autos"
        );
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Auto auto = new Auto();
            auto.setIdAutos(rs.getInt("ID_Autos"));
            auto.setModell(rs.getString("Modell"));
            auto.setBaujahr(rs.getDate("Baujahr").toLocalDate());
            auto.setGewicht(rs.getDouble("Gewicht"));
            auto.setLeistung(rs.getInt("Leistung"));
            auto.setVerbrenner(rs.getBoolean("Verbrenner"));
            auto.setProduktion(rs.getBoolean("Produktion"));
            auto.setFsMarken(rs.getInt("FS_Marken"));

            // Marke laden
            Marke marke = readMarkeById(rs.getInt("FS_Marken"));
            auto.setMarke(marke);

            autos.add(auto);
        }
        con.close();
        return autos;
    }

    // Auto hinzufügen
    public int addAuto(Auto auto) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement(
            "INSERT INTO autos (Modell, Baujahr, Gewicht, Leistung, Verbrenner, Produktion, FS_Marken) VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        pstmt.setString(1, auto.getModell());
        pstmt.setDate(2, Date.valueOf(auto.getBaujahr()));
        pstmt.setDouble(3, auto.getGewicht());
        pstmt.setInt(4, auto.getLeistung());
        pstmt.setBoolean(5, auto.isVerbrenner());
        pstmt.setBoolean(6, auto.isProduktion());
        pstmt.setInt(7, auto.getFsMarken());
        int rowsAffected = pstmt.executeUpdate();
        con.close();
        return rowsAffected;
    }

    // Auto löschen
    public int deleteAuto(int id) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("DELETE FROM autos WHERE ID_Autos = ?");
        pstmt.setInt(1, id);
        int rowsAffected = pstmt.executeUpdate();
        con.close();
        return rowsAffected;
    }

    // Autos nach Baujahr (Jahr) lesen
    public List<Auto> getAutosByJahr(int jahr) throws SQLException {
        List<Auto> autos = new ArrayList<>();
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement(
            "SELECT ID_Autos, Modell, Baujahr, Gewicht, Leistung, Verbrenner, Produktion, FS_Marken FROM autos WHERE YEAR(Baujahr) = ?"
        );
        pstmt.setInt(1, jahr);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Auto auto = new Auto();
            auto.setIdAutos(rs.getInt("ID_Autos"));
            auto.setModell(rs.getString("Modell"));
            auto.setBaujahr(rs.getDate("Baujahr").toLocalDate());
            auto.setGewicht(rs.getDouble("Gewicht"));
            auto.setLeistung(rs.getInt("Leistung"));
            auto.setVerbrenner(rs.getBoolean("Verbrenner"));
            auto.setProduktion(rs.getBoolean("Produktion"));
            auto.setFsMarken(rs.getInt("FS_Marken"));

            // Marke laden
            Marke marke = readMarkeById(rs.getInt("FS_Marken"));
            auto.setMarke(marke);

            autos.add(auto);
        }
        con.close();
        return autos;
    }

    // Anzahl aller Autos zählen
    public int countAutos() throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) as total FROM autos");
        ResultSet rs = pstmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt("total");
        }
        con.close();
        return count;
    }

    // Auto aktualisieren
    public int updateAuto(Auto auto) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement(
            "UPDATE autos SET Modell = ?, Baujahr = ?, Gewicht = ?, Leistung = ?, Verbrenner = ?, Produktion = ?, FS_Marken = ? WHERE ID_Autos = ?"
        );
        pstmt.setString(1, auto.getModell());
        pstmt.setDate(2, Date.valueOf(auto.getBaujahr()));
        pstmt.setDouble(3, auto.getGewicht());
        pstmt.setInt(4, auto.getLeistung());
        pstmt.setBoolean(5, auto.isVerbrenner());
        pstmt.setBoolean(6, auto.isProduktion());
        pstmt.setInt(7, auto.getFsMarken());
        pstmt.setInt(8, auto.getIdAutos());
        int rowsAffected = pstmt.executeUpdate();
        con.close();
        return rowsAffected;
    }

    // Alle Autos löschen
    public int deleteAllAutos() throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("DELETE FROM autos");
        int rowsAffected = pstmt.executeUpdate();
        con.close();
        return rowsAffected;
    }

    // ==================== VALIDIERUNGS-Methoden ====================

    // Prüfen ob eine Marke existiert
    public boolean markeExists(int markeId) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) as count FROM marken WHERE ID_Marken = ?");
        pstmt.setInt(1, markeId);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt("count") > 0;
        }
        con.close();
        return exists;
    }

    // Prüfen ob eine Marke von Autos referenziert wird
    public int countAutosWithMarke(int markeId) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) as count FROM autos WHERE FS_Marken = ?");
        pstmt.setInt(1, markeId);
        ResultSet rs = pstmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt("count");
        }
        con.close();
        return count;
    }

    // Prüfen ob ein Auto existiert
    public boolean autoExists(int autoId) throws SQLException {
        Connection con = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) as count FROM autos WHERE ID_Autos = ?");
        pstmt.setInt(1, autoId);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt("count") > 0;
        }
        con.close();
        return exists;
    }
}
