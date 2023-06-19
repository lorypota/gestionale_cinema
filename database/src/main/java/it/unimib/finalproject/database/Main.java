package it.unimib.finalproject.database;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.finalproject.database.command.CommandRegistry;
import it.unimib.finalproject.database.command.DelCommand;
import it.unimib.finalproject.database.command.GetCommand;
import it.unimib.finalproject.database.command.LenCommand;
import it.unimib.finalproject.database.command.PingCommand;
import it.unimib.finalproject.database.command.SetCommand;
import it.unimib.finalproject.database.command.StringsCommand;

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
        CommandRegistry.registerCommand(new PingCommand());
        CommandRegistry.registerCommand(new StringsCommand());

        // String commands
        CommandRegistry.registerCommand(new GetCommand());
        CommandRegistry.registerCommand(new SetCommand());
        CommandRegistry.registerCommand(new LenCommand());
        CommandRegistry.registerCommand(new DelCommand());

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
        startServer();
    }
}
