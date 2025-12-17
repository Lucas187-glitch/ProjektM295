package org.example.projekt.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Rollen und Authentifizierung Tests")
public class TestingRoles {

    // Basis-URL für die API (anpassen falls nötig)
    private static final String BASE_URL = "http://localhost:8080/Projekt_war_exploded/resources";
    private static HttpClient httpClient;

    @BeforeAll
    static void setup() {
        httpClient = HttpClient.newHttpClient();
    }

    // Helper-Methode für Basic Auth Header
    private String getBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    // ================== @PermitAll Tests ==================

    @Test
    @DisplayName("@PermitAll - Ping ohne Authentifizierung erlaubt")
    void testPing_WithoutAuth_Success() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos/ping"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Auto API is running"));
    }

    @Test
    @DisplayName("@PermitAll - Marke Ping ohne Authentifizierung erlaubt")
    void testMarkePing_WithoutAuth_Success() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/marken/ping"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Marke API is running"));
    }

    // ================== Falsche Zugangsdaten Tests ==================

    @Test
    @DisplayName("Falsche Zugangsdaten - Falsches Passwort wird abgelehnt")
    void testGetAllAutos_WrongPassword_Unauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos/all"))
                .header("Authorization", getBasicAuthHeader("admin", "FALSCHES_PASSWORT"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());  // UNAUTHORIZED
    }

    @Test
    @DisplayName("Falsche Zugangsdaten - Falscher Benutzername wird abgelehnt")
    void testGetAllAutos_WrongUsername_Unauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos/all"))
                .header("Authorization", getBasicAuthHeader("FALSCHER_USER", "1234"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());  // UNAUTHORIZED
    }

    @Test
    @DisplayName("Falsche Zugangsdaten - Keine Credentials werden abgelehnt")
    void testGetAllAutos_NoAuth_Unauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos/all"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());  // UNAUTHORIZED
    }

    @Test
    @DisplayName("Falsche Zugangsdaten - Leere Credentials werden abgelehnt")
    void testGetAllAutos_EmptyCredentials_Unauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos/all"))
                .header("Authorization", getBasicAuthHeader("", ""))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());  // UNAUTHORIZED
    }

    // ================== Falsche Rollen Tests ==================

    @Test
    @DisplayName("Falsche Rolle - USER kann nicht POST durchführen")
    void testAddAuto_AsUser_Forbidden() throws Exception {
        String autoJson = """
            {
                "modell": "Test Auto",
                "baujahr": "2020-01-01",
                "gewicht": 1500.0,
                "leistung": 150,
                "verbrenner": true,
                "produktion": true,
                "fsMarken": 1
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos"))
                .header("Authorization", getBasicAuthHeader("user", "187"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(autoJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, response.statusCode());  // FORBIDDEN
    }

    @Test
    @DisplayName("Falsche Rolle - USER kann nicht DELETE durchführen")
    void testDeleteAuto_AsUser_Forbidden() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos?id=1"))
                .header("Authorization", getBasicAuthHeader("user", "187"))
                .header("Content-Type", "text/plain")
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, response.statusCode());  // FORBIDDEN
    }

    @Test
    @DisplayName("Falsche Rolle - USER kann nicht PUT durchführen")
    void testUpdateAuto_AsUser_Forbidden() throws Exception {
        String autoJson = """
            {
                "idAutos": 1,
                "modell": "Updated Auto",
                "baujahr": "2021-01-01",
                "gewicht": 1600.0,
                "leistung": 200,
                "verbrenner": true,
                "produktion": true,
                "fsMarken": 1
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos"))
                .header("Authorization", getBasicAuthHeader("user", "187"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(autoJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, response.statusCode());  // FORBIDDEN
    }

    @Test
    @DisplayName("Falsche Rolle - USER kann nicht Marke erstellen")
    void testAddMarke_AsUser_Forbidden() throws Exception {
        String markeJson = """
            {
                "name": "Test Marke"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/marken"))
                .header("Authorization", getBasicAuthHeader("user", "187"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(markeJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, response.statusCode());  // FORBIDDEN
    }

    @Test
    @DisplayName("Falsche Rolle - USER kann nicht Marke löschen")
    void testDeleteMarke_AsUser_Forbidden() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/marken?id=1"))
                .header("Authorization", getBasicAuthHeader("user", "187"))
                .header("Content-Type", "text/plain")
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, response.statusCode());  // FORBIDDEN
    }

    // ================== Positive Tests (korrekte Rollen) ==================

    @Test
    @DisplayName("Korrekte Rolle - ADMIN kann GET durchführen")
    void testGetAllAutos_AsAdmin_Success() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos/all"))
                .header("Authorization", getBasicAuthHeader("admin", "1234"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Korrekte Rolle - USER kann GET durchführen")
    void testGetAllAutos_AsUser_Success() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos/all"))
                .header("Authorization", getBasicAuthHeader("user", "187"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Korrekte Rolle - ADMIN kann POST durchführen")
    void testAddAuto_AsAdmin_Success() throws Exception {
        String autoJson = """
            {
                "modell": "Admin Test Auto",
                "baujahr": "2020-01-01",
                "gewicht": 1500.0,
                "leistung": 150,
                "verbrenner": true,
                "produktion": true,
                "fsMarken": 1
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autos"))
                .header("Authorization", getBasicAuthHeader("admin", "1234"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(autoJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // CREATED oder BAD_REQUEST (falls Marke nicht existiert)
        assertTrue(response.statusCode() == 201 || response.statusCode() == 400);
    }
}
