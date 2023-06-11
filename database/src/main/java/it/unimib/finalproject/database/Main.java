package it.unimib.finalproject.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe principale in cui parte il database.
 */
public class Main {
    /**
     * Porta di ascolto.
     */
    public static final int PORT = 3030;

    /**
     * Avvia il database e l'ascolto di nuove connessioni.
     *
     * @return Un server HTTP Grizzly.
     */
    public static void startServer() {
        try {
            var server = new ServerSocket(PORT);

            /** Stucture proposal per command syntax
             * SET key <JSON> <--- Note: here Direct JSON instead of only strings like Redis
             * HSET key field <JSON> <--- Note: here Direct JSON instead of only strings like Redis
             * Check Redis docs for more info about the 2 commands above
             *
             *
             * var map1 = new ConcurrentHashMap<String, HashMap<String, String>>();
             * var map2 = new ConcurrentHashMap<String, String>();
             * var map = new ConcurrentHashMap<String, Object>();
             * map.put("key", new HashMap<String, String>());
             * var gay = map.get("key");
             * if (gay != null) {
             *  if (gay instanceof HashMap<?, ?> && command.equal("HGET")) {
             *      var hs = (HashMap<?, ?>) gay;
             *      System.out.println("Sono hashmap gay");
             *  } else if (gay instanceof String) {
             *      System.out.println("Sono stringa gay");
             *  } 
             * }
             */

            /**
             * Handling logic
             *
             *
             * new Handler(server.accept(), map).start();
             * get cycle
             * parse COMMAND, ARGS[]
             * Constuct concrete command based on single abstract command in a switch (throw if arguments are wrong)
             * execute command
             * return result
             */

            System.out.println("Database listening at localhost:" + PORT);
            while (true)
                new Handler(server.accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Handler di una connessione del client.
     */
    private static class Handler extends Thread {
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    out.println(inputLine);
                }

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
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
