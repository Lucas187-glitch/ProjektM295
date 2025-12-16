package org.example.projekt.config;


import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.example.projekt.security.Roles;
import org.example.projekt.services.AutoServices;
import org.example.projekt.services.MarkeServices;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/resources")
public class RestConfig extends Application {
    public Set<Class<?>> getClasses() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new HashSet<Class<?>>(
                Arrays.asList(
                        Roles.class,
                        MarkeServices.class,
                        AutoServices.class));
    }
}
