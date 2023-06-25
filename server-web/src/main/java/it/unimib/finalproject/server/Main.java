package it.unimib.finalproject.server;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.repositories.BookingRepository;
import it.unimib.finalproject.server.service.BookingService;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import jakarta.inject.Singleton;

/**
 * Classe principale.
 *
 */
public class Main {
    // URL HTTP di base in cui Grizzly si pone in ascolto.
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Avvia il server HTTP Grizzly esponendo le risorse JAX-RS definite
     * nell'applicazione.
     *
     * @return Un server HTTP Grizzly.
     */
    public static HttpServer startServer() {
        // Crea una file di configurazione per una risorsa, in questo caso tutte
        // le classi del package dell'esercizio.
        final var rc = new ResourceConfig()
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        try {
                            bind(new DbConnector("localhost", 3030)).to(DbConnector.class);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bind(BookingRepository.class).to(BookingRepository.class).in(Singleton.class);
                        bind(BookingService.class).to(BookingService.class).in(Singleton.class);
                        bind(JsonMapper.class).to(JsonMapper.class).in(Singleton.class);
                    }
                })
                .packages(Main.class.getPackageName());

        // Crea e avvia un server HTTP che espone l'applicazione Jersey all'URL
        // predefinito.
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Metodo principale.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final var server = startServer();
        System.out.println(String.format("Jersey app started with endpoints available at "
                + "%s%nHit Ctrl-C to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}
