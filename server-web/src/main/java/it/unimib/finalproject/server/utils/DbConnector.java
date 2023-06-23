package it.unimib.finalproject.server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;

import it.unimib.finalproject.server.utils.dbclient.resp.RESPReader;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPArray;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPString;

public class DbConnector extends Reader {

    private Socket client;
    private RESPReader reader;
    private PrintWriter writer;


    public DbConnector(String host, int port) throws UnknownHostException, IOException {
        client = new Socket(host, port);
        reader = new RESPReader(new BufferedReader(new InputStreamReader(client.getInputStream())));
        writer = new PrintWriter(client.getOutputStream(), true);
    }

    public String ping() throws IOException {
        var command = new RESPArray();

        command.add(new RESPString("PING"));

        this.writer.print(command);
        this.writer.flush();

        return ((RESPString)this.reader.readRESP()).getString();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return this.reader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.reader.close();
        this.client.close();
    }
}
