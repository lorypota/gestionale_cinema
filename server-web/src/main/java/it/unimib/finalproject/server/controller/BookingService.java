package it.unimib.finalproject.server.controller;

import java.io.IOException;

import it.unimib.finalproject.server.utils.DbConnector;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class BookingService {
    @Inject
    private DbConnector conn;
    public String ciao() {
        try {
            return conn.ping();
        } catch (IOException e) {
            return "suca";
        }
    }
}
