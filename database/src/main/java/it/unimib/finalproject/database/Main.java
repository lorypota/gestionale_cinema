package it.unimib.finalproject.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.unimib.finalproject.database.command.CommandRegistry;
import it.unimib.finalproject.database.command.hash.HDelCommand;
import it.unimib.finalproject.database.command.hash.HExistsCommand;
import it.unimib.finalproject.database.command.hash.HGetAllCommand;
import it.unimib.finalproject.database.command.hash.HGetCommand;
import it.unimib.finalproject.database.command.hash.HKeysCommand;
import it.unimib.finalproject.database.command.hash.HLenCommand;
import it.unimib.finalproject.database.command.hash.HSetCommand;
import it.unimib.finalproject.database.command.hash.HStrLenCommand;
import it.unimib.finalproject.database.command.hash.HValsCommand;
import it.unimib.finalproject.database.command.string.DecrCommand;
import it.unimib.finalproject.database.command.string.DelCommand;
import it.unimib.finalproject.database.command.string.GetCommand;
import it.unimib.finalproject.database.command.string.IncrCommand;
import it.unimib.finalproject.database.command.string.SetCommand;
import it.unimib.finalproject.database.command.string.StrLenCommand;
import it.unimib.finalproject.database.command.util.CommandCommand;
import it.unimib.finalproject.database.command.util.HashesCommand;
import it.unimib.finalproject.database.command.util.PingCommand;
import it.unimib.finalproject.database.command.util.StringsCommand;
import it.unimib.finalproject.database.resp.EscapeUtils;

/**
 * Classe principale in cui parte il database.
 */
public class Main {
    /**
     * Porta di ascolto.
     */
    public static final int PORT = 3030;

    public static final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<String, Object>();

    /**
     * Avvia il database e l'ascolto di nuove connessioni.
     *
     * @return Un server HTTP Grizzly.
     */
    public static void startServer() {
        // Utility commands
        CommandRegistry.inst().registerCommand(new CommandCommand(CommandRegistry.inst()));
        CommandRegistry.inst().registerCommand(new HashesCommand());
        CommandRegistry.inst().registerCommand(new PingCommand());
        CommandRegistry.inst().registerCommand(new StringsCommand());

        // String commands
        CommandRegistry.inst().registerCommand(new DecrCommand());
        CommandRegistry.inst().registerCommand(new DelCommand());
        CommandRegistry.inst().registerCommand(new GetCommand());
        CommandRegistry.inst().registerCommand(new IncrCommand());
        CommandRegistry.inst().registerCommand(new SetCommand());
        CommandRegistry.inst().registerCommand(new StrLenCommand());

        // Hash commands
        CommandRegistry.inst().registerCommand(new HDelCommand());
        CommandRegistry.inst().registerCommand(new HExistsCommand());
        CommandRegistry.inst().registerCommand(new HGetAllCommand());
        CommandRegistry.inst().registerCommand(new HGetCommand());
        CommandRegistry.inst().registerCommand(new HKeysCommand());
        CommandRegistry.inst().registerCommand(new HLenCommand());
        CommandRegistry.inst().registerCommand(new HSetCommand());
        CommandRegistry.inst().registerCommand(new HStrLenCommand());
        CommandRegistry.inst().registerCommand(new HValsCommand());

        try {
            try (var server = new ServerSocket(PORT)) {
                System.out.println("Database listening at localhost:" + PORT);
                while (true)
                    new Handler(server.accept(), store).start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        loadMockData();
        startServer();
    }

    private static void loadMockData() throws IOException {
        var movies = new ConcurrentHashMap<String, Object>();
        var halls = new ConcurrentHashMap<String, Object>();
        var projections = new ConcurrentHashMap<String, Object>();
        var bookings = new ConcurrentHashMap<String, Object>();

        var cl = Thread.currentThread().getContextClassLoader();
        var moviesStream = cl.getResourceAsStream("movies.json");
        var hallsStream = cl.getResourceAsStream("halls.json");
        var projectionsStream = cl.getResourceAsStream("projections.json");
        var bookingsStream = cl.getResourceAsStream("bookings.json");

        var moviesJson = Main.readJsonFile(moviesStream);
        var hallsJson = Main.readJsonFile(hallsStream);
        var projectionsJson = Main.readJsonFile(projectionsStream);
        var bookingsJson = Main.readJsonFile(bookingsStream);

        var mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        List<LinkedHashMap<String, Object>> moviesArray = mapper.readValue(moviesJson, List.class);
        @SuppressWarnings("unchecked")
        List<LinkedHashMap<String, Object>> hallsArray = mapper.readValue(hallsJson, List.class);
        @SuppressWarnings("unchecked")
        List<LinkedHashMap<String, Object>> projectionsArray = mapper.readValue(projectionsJson, List.class);
        @SuppressWarnings("unchecked")
        List<LinkedHashMap<String, Object>> bookingsArray = mapper.readValue(bookingsJson, List.class);

        for (var movie : moviesArray) {
            var movieId = movie.get("id").toString();
            movies.put(movieId, EscapeUtils.escape(mapper.writeValueAsString(movie)));
        }

        for (var hall : hallsArray) {
            var hallId = hall.get("id").toString();
            halls.put(hallId, EscapeUtils.escape(mapper.writeValueAsString(hall)));
        }

        for (var projection : projectionsArray) {
            var projectionId = projection.get("id").toString();
            projections.put(projectionId, EscapeUtils.escape(mapper.writeValueAsString(projection)));
        }

        for (var booking : bookingsArray) {
            var bookingId = booking.get("id").toString();
            bookings.put(bookingId, EscapeUtils.escape(mapper.writeValueAsString(booking)));
        }

        store.put("movies", movies);
        store.put("halls", halls);
        store.put("projections", projections);
        store.put("bookings", bookings);

        store.put("movies_id", (Integer) movies.size());
        store.put("halls_id", (Integer) halls.size());
        store.put("projections_id", (Integer) projections.size());
        store.put("bookings_id", (Integer) bookings.size());
    }

    private static String readJsonFile(InputStream stream) throws IOException {
        var sb = new StringBuilder();
        var reader = new BufferedReader(new InputStreamReader(stream));

        var input = "";
        while ((input = reader.readLine()) != null) {
            sb.append(input);
        }

        reader.close();
        return sb.toString();
    }
}
