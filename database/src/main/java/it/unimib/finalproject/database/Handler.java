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
    }

    public void run() {
        try {
            var out = new PrintWriter(client.getOutputStream(), true);
            var in = new RESPReader(new BufferedReader(new InputStreamReader(client.getInputStream())));

            RESPType input;

            while ((input = in.readRESP()) != null) {

                try {

                    String commandName = null;
                    String[] args = null;

                    if (input instanceof RESPArray) {
                        var array = (RESPArray) input;
                        commandName = ((RESPString) array.get(0)).getString();
                        args = new String[array.size() - 1];
                        for (int i = 1; i < array.size(); i++) {
                            args[i - 1] = ((RESPString) array.get(i)).getString();
                        }
                    } else if (input instanceof RESPString) {
                        commandName = ((RESPString) input).getString();
                        args = new String[0];
                    }

                    var command = CommandRegistry.get(commandName);
                    var result = command.execute(store, args);
                    out.println(result);
                } catch (Exception e) {
                    out.println(new RESPError(e.getMessage()));
                }
            }

            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
