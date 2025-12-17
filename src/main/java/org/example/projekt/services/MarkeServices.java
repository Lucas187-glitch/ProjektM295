package org.example.projekt.services;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.projekt.modules.Marke;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@Path("/marken")
public class MarkeServices {
    private static final Logger LOGGER = Logger.getLogger(MarkeServices.class.getName());
    private DataBase db = new DataBase();

    @GET
    @Path("/ping")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        LOGGER.info("Ping Request für Marke API");
        return Response.ok("Marke API is running").build();
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMarke(@QueryParam("id") int id) {
        LOGGER.info("GET Request für Marke ID: " + id);

        try {
            Marke marke = db.readMarkeById(id);
            if (marke == null) {
                LOGGER.log(Level.WARNING, "Marke mit ID " + id + " nicht gefunden");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Marke nicht gefunden")
                        .build();
            }
            LOGGER.info("Marke mit ID " + id + " erfolgreich abgerufen");
            return Response.ok(marke).build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Lesen von Marke " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/all")
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMarken() {
        LOGGER.info("GET Request für alle Marken");

        try {
            List<Marke> marken = db.getAllMarken();
            LOGGER.info(marken.size() + " Marken erfolgreich abgerufen");
            return Response.ok(marken).build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Abrufen aller Marken", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @RolesAllowed("ADMIN")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteMarke(@QueryParam("id") int id) {
        LOGGER.info("DELETE Request für Marke ID: " + id);

        try {
            // Validierung: Prüfen ob Marke noch von Autos referenziert wird
            int autoCount = db.countAutosWithMarke(id);
            if (autoCount > 0) {
                LOGGER.log(Level.WARNING, "Marke " + id + " kann nicht gelöscht werden - wird von " + autoCount + " Auto(s) referenziert");
                return Response.status(Response.Status.CONFLICT)
                        .entity("Fehler: Marke kann nicht gelöscht werden. Sie wird noch von " + autoCount + " Auto(s) verwendet. Bitte löschen Sie zuerst die zugehörigen Autos.")
                        .build();
            }

            int rowsDeleted = db.deleteMarke(id);
            if (rowsDeleted > 0) {
                LOGGER.info("Marke " + id + " erfolgreich gelöscht");
                return Response.ok("Marke " + id + " erfolgreich gelöscht!").build();
            } else {
                LOGGER.log(Level.WARNING, "Marke mit ID " + id + " nicht gefunden");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Marke nicht gefunden")
                        .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Löschen von Marke " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addMarke(Marke marke) {
        LOGGER.info("POST Request zum Hinzufügen einer neuen Marke: " + marke.getName());

        try {
            // Validierung: Pflichtfelder prüfen
            if (marke.getName() == null || marke.getName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Fehler: Markenname darf nicht leer sein")
                        .build();
            }

            int rowsInserted = db.addMarke(marke);
            if (rowsInserted > 0) {
                LOGGER.info("Neue Marke erfolgreich hinzugefügt: " + marke.getName());
                return Response.status(Response.Status.CREATED)
                        .entity("Neue Marke " + marke.getName() + " erfolgreich hinzugefügt")
                        .build();
            } else {
                LOGGER.log(Level.WARNING, "Marke konnte nicht hinzugefügt werden");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Marke konnte nicht hinzugefügt werden")
                        .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Datenbankfehler beim Hinzufügen einer Marke", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Datenbankfehler: " + e.getMessage())
                    .build();
        }
    }
}
