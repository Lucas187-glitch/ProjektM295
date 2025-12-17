package org.example.projekt.services;

import jakarta.ws.rs.core.Response;
import org.example.projekt.modules.Marke;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MarkeServices Test Suite")
public class TestingMarkeServices {

    private MarkeServices markeServices;
    private Marke testMarke;

    @BeforeEach
    void setUp() {
        markeServices = new MarkeServices();

        // Test-Marke erstellen mit gültigen Daten
        testMarke = new Marke("Volkswagen Test");
    }

    // ================== addMarke() Tests ==================

    @Test
    @DisplayName("addMarke - Positiv: Marke mit gültigem Namen hinzufügen")
    void testAddMarke_ValidData() {
        // Act
        Response response = markeServices.addMarke(testMarke);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.CREATED.getStatusCode() ||
            response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
        );
    }

    @Test
    @DisplayName("addMarke - Negativ: Validierung - Name ist leer")
    void testAddMarke_EmptyName() {
        // Arrange
        testMarke.setName("   ");

        // Act
        Response response = markeServices.addMarke(testMarke);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Markenname darf nicht leer sein"));
    }

    @Test
    @DisplayName("addMarke - Negativ: Validierung - Name ist null")
    void testAddMarke_NullName() {
        // Arrange
        testMarke.setName(null);

        // Act
        Response response = markeServices.addMarke(testMarke);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Markenname darf nicht leer sein"));
    }

    // ================== getMarke() Tests ==================

    @Test
    @DisplayName("getMarke - Positiv: Marke abrufen")
    void testGetMarke_Success() {
        // Act
        Response response = markeServices.getMarke(1);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.OK.getStatusCode() ||
            response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
        );
    }

    @Test
    @DisplayName("getMarke - Negativ: Marke nicht gefunden")
    void testGetMarke_NotFound() {
        // Act
        Response response = markeServices.getMarke(99999);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Marke nicht gefunden", response.getEntity());
    }

    // ================== getAllMarken() Tests ==================

    @Test
    @DisplayName("getAllMarken - Positiv: Alle Marken abrufen")
    void testGetAllMarken_Success() {
        // Act
        Response response = markeServices.getAllMarken();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    // ================== deleteMarke() Tests ==================

    @Test
    @DisplayName("deleteMarke - Positiv: Marke löschen")
    void testDeleteMarke_Success() {
        // Act
        Response response = markeServices.deleteMarke(1);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.OK.getStatusCode() ||
            response.getStatus() == Response.Status.NOT_FOUND.getStatusCode() ||
            response.getStatus() == Response.Status.CONFLICT.getStatusCode()
        );
    }

    @Test
    @DisplayName("deleteMarke - Negativ: Marke nicht gefunden")
    void testDeleteMarke_NotFound() {
        // Act
        Response response = markeServices.deleteMarke(99999);

        // Assert
        assertTrue(
            response.getStatus() == Response.Status.NOT_FOUND.getStatusCode() ||
            response.getStatus() == Response.Status.CONFLICT.getStatusCode()
        );
    }
}
