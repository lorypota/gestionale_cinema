package it.unimib.finalproject.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.AbstractMap;

import it.unimib.finalproject.database.command.CommandRegistry;
import it.unimib.finalproject.database.resp.RESPReader;
import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

/**
 * Handler di una connessione del client.
 */
public class Handler extends Thread {
    private Socket client;
    private AbstractMap<String, Object> store;

    public Handler(Socket client, AbstractMap<String, Object> store) {
        this.client = client;
        this.store = store;
    }

    public void run() {
        try {
            try (var out = new PrintWriter(client.getOutputStream(), true)) {
                try (var in = new RESPReader(new BufferedReader(new InputStreamReader(client.getInputStream())))) {

                    RESPType input;

                    try {

                        while ((input = in.readRESP()) != null) {
                            try {

                                String commandName = null;
                                RESPType[] args = null;

                                if (input instanceof RESPArray) {
                                    var array = (RESPArray) input;
                                    commandName = ((RESPString) array.get(0)).getString();
                                    args = array.stream().skip(1).toArray(RESPType[]::new);
                                } else if (input instanceof RESPString) {
                                    commandName = ((RESPString) input).getString();
                                    args = new RESPType[0];
                                }

                                var command = CommandRegistry.inst().get(commandName);
                                var result = command.execute(store, args);
                                out.print(result);
                            } catch (RESPError e) {
                                out.print(e);
                            }

                            out.flush();
                        }
                    } catch (NumberFormatException e) {
                        out.print(new RESPError("Invalid number format"));
                    } catch (RESPError e) {
                        out.print(e);
                    }
                }
            }
            client.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
