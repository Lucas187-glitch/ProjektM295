package org.example.projekt.services;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.projekt.modules.Auto;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@Path("/autos")
public class AutoServices {
    private static final Logger LOGGER = Logger.getLogger(AutoServices.class.getName());
    private DataBase db = new DataBase();

    @GET
    @Path("/ping")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        LOGGER.info("Ping Request für Auto API");
        return Response.ok("Auto API is running").build();
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuto(@QueryParam("id") int id) {
        LOGGER.info("GET Request für Auto ID: " + id);

        try {
            Auto auto = db.readAutoById(id);
            if (auto == null) {
                LOGGER.log(Level.WARNING, "Auto mit ID " + id + " nicht gefunden");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Auto nicht gefunden")
                        .build();
            }
            LOGGER.info("Auto mit ID " + id + " erfolgreich abgerufen");
            return Response.ok(auto).build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Lesen von Auto " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/all")
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAutos() {
        LOGGER.info("GET Request für alle Autos");

        try {
            List<Auto> autos = db.getAllAutos();
            LOGGER.info(autos.size() + " Autos erfolgreich abgerufen");
            return Response.ok(autos).build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Abrufen aller Autos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @RolesAllowed("ADMIN")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAuto(@QueryParam("id") int id) {
        LOGGER.info("DELETE Request für Auto ID: " + id);

        try {
            int rowsDeleted = db.deleteAuto(id);
            if (rowsDeleted > 0) {
                LOGGER.info("Auto " + id + " erfolgreich gelöscht");
                return Response.ok("Auto " + id + " erfolgreich gelöscht!").build();
            } else {
                LOGGER.log(Level.WARNING, "Auto mit ID " + id + " nicht gefunden");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Auto nicht gefunden")
                        .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Löschen von Auto " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addAuto(Auto auto) {
        LOGGER.info("POST Request zum Hinzufügen eines neuen Autos: " + auto.getModell());

        try {
            // Validierung: Prüfen ob Marke existiert
            if (!db.markeExists(auto.getFsMarken())) {
                LOGGER.log(Level.WARNING, "Marke mit ID " + auto.getFsMarken() + " existiert nicht");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Marke mit ID " + auto.getFsMarken() + " existiert nicht")
                        .build();
            }

            // Validierung: Pflichtfelder prüfen
            if (auto.getModell() == null || auto.getModell().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Modell darf nicht leer sein")
                        .build();
            }

            // Validierung 1: Baujahr darf nicht in der Zukunft liegen (DATE)
            if (auto.getBaujahr().isAfter(LocalDate.now())) {
                LOGGER.log(Level.WARNING, "Baujahr liegt in der Zukunft: " + auto.getBaujahr());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Baujahr darf nicht in der Zukunft liegen")
                        .build();
            }

            // Validierung 2: Gewicht muss größer als 0 sein (DECIMAL)
            if (auto.getGewicht() <= 0) {
                LOGGER.log(Level.WARNING, "Ungültiges Gewicht: " + auto.getGewicht());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Gewicht muss größer als 0 sein")
                        .build();
            }

            // Validierung 3: Leistung muss größer als 0 sein (INTEGER)
            if (auto.getLeistung() <= 0) {
                LOGGER.log(Level.WARNING, "Ungültige Leistung: " + auto.getLeistung());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Leistung muss größer als 0 sein (PS)")
                        .build();
            }

            int rowsInserted = db.addAuto(auto);
            if (rowsInserted > 0) {
                LOGGER.info("Neues Auto erfolgreich hinzugefügt: " + auto.getModell());
                return Response.status(Response.Status.CREATED)
                        .entity("Neues Auto " + auto.getModell() + " erfolgreich hinzugefügt")
                        .build();
            } else {
                LOGGER.log(Level.WARNING, "Auto konnte nicht hinzugefügt werden");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Auto konnte nicht hinzugefügt werden")
                        .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Hinzufügen eines Autos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/baujahr")
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAutosByBaujahr(@QueryParam("jahr") int jahr) {
        LOGGER.info("GET Request für Autos mit Baujahr: " + jahr);

        try {
            List<Auto> autos = db.getAutosByJahr(jahr);
            LOGGER.info(autos.size() + " Autos mit Baujahr " + jahr + " gefunden");
            return Response.ok(autos).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen von Autos nach Baujahr", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Fehler: Bitte gültiges Jahr angeben (z.B. 2020). " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/count")
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAutos() {
        LOGGER.info("GET Request für Anzahl aller Autos");

        try {
            int count = db.countAutos();
            LOGGER.info("Anzahl der Autos: " + count);
            return Response.ok("{\"count\": " + count + "}").build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Zählen der Autos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateAuto(Auto auto) {
        LOGGER.info("PUT Request zum Aktualisieren von Auto ID: " + auto.getIdAutos());

        try {
            // Validierung: Prüfen ob Auto existiert
            if (!db.autoExists(auto.getIdAutos())) {
                LOGGER.log(Level.WARNING, "Auto mit ID " + auto.getIdAutos() + " existiert nicht");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Fehler: Auto mit ID " + auto.getIdAutos() + " existiert nicht")
                        .build();
            }

            // Validierung: Prüfen ob Marke existiert
            if (!db.markeExists(auto.getFsMarken())) {
                LOGGER.log(Level.WARNING, "Marke mit ID " + auto.getFsMarken() + " existiert nicht");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Marke mit ID " + auto.getFsMarken() + " existiert nicht")
                        .build();
            }

            // Validierung: Pflichtfelder prüfen
            if (auto.getModell() == null || auto.getModell().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Modell darf nicht leer sein")
                        .build();
            }

            // Validierung 1: Baujahr darf nicht in der Zukunft liegen (DATE)
            if (auto.getBaujahr().isAfter(LocalDate.now())) {
                LOGGER.log(Level.WARNING, "Baujahr liegt in der Zukunft: " + auto.getBaujahr());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Baujahr darf nicht in der Zukunft liegen")
                        .build();
            }

            // Validierung 2: Gewicht muss größer als 0 sein (DECIMAL)
            if (auto.getGewicht() <= 0) {
                LOGGER.log(Level.WARNING, "Ungültiges Gewicht: " + auto.getGewicht());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Gewicht muss größer als 0 sein")
                        .build();
            }

            // Validierung 3: Leistung muss größer als 0 sein (INTEGER)
            if (auto.getLeistung() <= 0) {
                LOGGER.log(Level.WARNING, "Ungültige Leistung: " + auto.getLeistung());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Leistung muss größer als 0 sein (PS)")
                        .build();
            }

            int rowsUpdated = db.updateAuto(auto);
            if (rowsUpdated > 0) {
                LOGGER.info("Auto " + auto.getIdAutos() + " erfolgreich aktualisiert");
                return Response.ok("Auto " + auto.getIdAutos() + " erfolgreich aktualisiert").build();
            } else {
                LOGGER.log(Level.WARNING, "Auto mit ID " + auto.getIdAutos() + " nicht gefunden");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Auto nicht gefunden")
                        .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Aktualisieren von Auto " + auto.getIdAutos(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/all")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllAutos() {
        LOGGER.warning("DELETE Request zum Löschen ALLER Autos");

        try {
            int rowsDeleted = db.deleteAllAutos();
            LOGGER.info(rowsDeleted + " Autos wurden gelöscht");
            return Response.ok(rowsDeleted + " Autos erfolgreich gelöscht").build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Löschen aller Autos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }
}
