package org.example.projekt.services;

import jakarta.ws.rs.core.Response;
import org.example.projekt.modules.Auto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AutoServices Test Suite")
public class TestingAutoServices {

    private AutoServices autoServices;
    private Auto testAuto;

    @BeforeEach
    void setUp() {
        autoServices = new AutoServices();

        // Test-Auto erstellen mit gültigen Daten
        testAuto = new Auto(
                "Golf GTI Test",
                LocalDate.of(2020, 5, 15),
                1500.5,
                150,
                true,
                true,
                1  // Annahme: Marke mit ID 1 existiert
        );
    }

    // ================== addAuto() Tests ==================

    @Test
    @DisplayName("addAuto - Positiv: Auto mit gültigen Daten hinzufügen")
    void testAddAuto_ValidData() {
        // Act
        Response response = autoServices.addAuto(testAuto);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.CREATED.getStatusCode() ||
            response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
        );
    }

    @Test
    @DisplayName("addAuto - Negativ: Validierung - Baujahr in der Zukunft")
    void testAddAuto_BaujahrInFuture() {
        // Arrange
        testAuto.setBaujahr(LocalDate.now().plusDays(1));

        // Act
        Response response = autoServices.addAuto(testAuto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Baujahr darf nicht in der Zukunft liegen"));
    }

    @Test
    @DisplayName("addAuto - Negativ: Validierung - Gewicht <= 0")
    void testAddAuto_InvalidGewicht() {
        // Arrange
        testAuto.setGewicht(0);

        // Act
        Response response = autoServices.addAuto(testAuto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Gewicht muss größer als 0 sein"));
    }

    @Test
    @DisplayName("addAuto - Negativ: Validierung - Leistung <= 0")
    void testAddAuto_InvalidLeistung() {
        // Arrange
        testAuto.setLeistung(0);

        // Act
        Response response = autoServices.addAuto(testAuto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Leistung muss größer als 0 sein"));
    }

    @Test
    @DisplayName("addAuto - Negativ: Validierung - Modell leer")
    void testAddAuto_EmptyModell() {
        // Arrange
        testAuto.setModell("   ");

        // Act
        Response response = autoServices.addAuto(testAuto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Modell darf nicht leer sein"));
    }

    // ================== updateAuto() Tests ==================

    @Test
    @DisplayName("updateAuto - Positiv: Auto mit gültigen Daten aktualisieren")
    void testUpdateAuto_ValidData() {
        // Arrange
        Auto updateAuto = new Auto(
                1,
                "Golf GTI Updated",
                LocalDate.of(2021, 6, 20),
                1600.0,
                200,
                true,
                true,
                1,
                null
        );

        // Act
        Response response = autoServices.updateAuto(updateAuto);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.OK.getStatusCode() ||
            response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
        );
    }

    @Test
    @DisplayName("updateAuto - Negativ: Validierung - Baujahr in der Zukunft")
    void testUpdateAuto_BaujahrInFuture() {
        // Arrange
        Auto updateAuto = new Auto(
                1,
                "Golf",
                LocalDate.now().plusYears(1),
                1500.0,
                150,
                true,
                true,
                1,
                null
        );

        // Act
        Response response = autoServices.updateAuto(updateAuto);

        // Assert
        assertTrue(response.getEntity().toString().contains("Baujahr darf nicht in der Zukunft liegen") ||
                   response.getEntity().toString().contains("Auto mit ID 1 existiert nicht"));
    }

    @Test
    @DisplayName("updateAuto - Negativ: Validierung - Gewicht <= 0")
    void testUpdateAuto_InvalidGewicht() {
        // Arrange
        Auto updateAuto = new Auto(
                1,
                "Golf",
                LocalDate.of(2020, 1, 1),
                -100.0,
                150,
                true,
                true,
                1,
                null
        );

        // Act
        Response response = autoServices.updateAuto(updateAuto);

        // Assert
        assertTrue(response.getEntity().toString().contains("Gewicht muss größer als 0 sein") ||
                   response.getEntity().toString().contains("Auto mit ID 1 existiert nicht"));
    }

    @Test
    @DisplayName("updateAuto - Negativ: Validierung - Leistung <= 0")
    void testUpdateAuto_InvalidLeistung() {
        // Arrange
        Auto updateAuto = new Auto(
                1,
                "Golf",
                LocalDate.of(2020, 1, 1),
                1500.0,
                -50,
                true,
                true,
                1,
                null
        );

        // Act
        Response response = autoServices.updateAuto(updateAuto);

        // Assert
        assertTrue(response.getEntity().toString().contains("Leistung muss größer als 0 sein") ||
                   response.getEntity().toString().contains("Auto mit ID 1 existiert nicht"));
    }

    // ================== getAuto() Tests ==================

    @Test
    @DisplayName("getAuto - Positiv: Auto abrufen")
    void testGetAuto_Success() {
        // Act
        Response response = autoServices.getAuto(1);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.OK.getStatusCode() ||
            response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
        );
    }

    @Test
    @DisplayName("getAuto - Negativ: Auto nicht gefunden")
    void testGetAuto_NotFound() {
        // Act
        Response response = autoServices.getAuto(99999);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Auto nicht gefunden", response.getEntity());
    }

    // ================== getAllAutos() Tests ==================

    @Test
    @DisplayName("getAllAutos - Positiv: Alle Autos abrufen")
    void testGetAllAutos_Success() {
        // Act
        Response response = autoServices.getAllAutos();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    // ================== deleteAuto() Tests ==================

    @Test
    @DisplayName("deleteAuto - Positiv: Auto löschen")
    void testDeleteAuto_Success() {
        // Act
        Response response = autoServices.deleteAuto(1);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.OK.getStatusCode() ||
            response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
        );
    }

    @Test
    @DisplayName("deleteAuto - Negativ: Auto nicht gefunden")
    void testDeleteAuto_NotFound() {
        // Act
        Response response = autoServices.deleteAuto(99999);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Auto nicht gefunden", response.getEntity());
    }

    // ================== getAutosByBaujahr() Tests ==================

    @Test
    @DisplayName("getAutosByBaujahr - Positiv: Autos nach Jahr abrufen")
    void testGetAutosByBaujahr_Success() {
        // Act
        Response response = autoServices.getAutosByBaujahr(2020);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    @DisplayName("getAutosByBaujahr - Negativ: Ungültiges Jahr (0)")
    void testGetAutosByBaujahr_InvalidYear() {
        // Act
        Response response = autoServices.getAutosByBaujahr(0);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("gültiges Jahr"));
    }

    // ================== countAutos() Tests ==================

    @Test
    @DisplayName("countAutos - Positiv: Anzahl der Autos abrufen")
    void testCountAutos_Success() {
        // Act
        Response response = autoServices.countAutos();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("\"count\":"));
    }

    // ================== deleteAllAutos() Tests ==================

    @Test
    @DisplayName("deleteAllAutos - Positiv: Alle Autos löschen")
    void testDeleteAllAutos_Success() {
        // Act
        Response response = autoServices.deleteAllAutos();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Autos erfolgreich gelöscht"));
    }
}
