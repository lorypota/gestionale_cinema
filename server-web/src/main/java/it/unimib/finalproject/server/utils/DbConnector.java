package it.unimib.finalproject.server.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import it.unimib.finalproject.server.utils.dbclient.resp.RESPReader;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPArray;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPBulkString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPNumber;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPType;

public class DbConnector implements Closeable {

    // TODO: Error handling, multiple type handling
    private Socket client;
    private RESPReader reader;
    private PrintWriter writer;

    public DbConnector(String host, int port) throws UnknownHostException, IOException {
        client = new Socket(host, port);
        reader = new RESPReader(new BufferedReader(new InputStreamReader(client.getInputStream())));
        writer = new PrintWriter(client.getOutputStream(), true);
    }

    public String ping() throws IOException {
        this.writer.print(new RESPArray(new RESPString("PING")));
        this.writer.flush();

        return ((RESPString) this.reader.readRESP()).getString();
    }

    public String[] hashes() throws IOException {
        this.writer.print(new RESPArray(new RESPString("HASHES")));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString()).toArray(String[]::new);
    }

    public String[] strings() throws IOException {
        this.writer.print(new RESPArray(new RESPString("STRINGS")));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString()).toArray(String[]::new);
    }

    public Integer decr(String key) throws RESPError, NumberFormatException, IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("DECR"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public Integer incr(String key) throws RESPError, NumberFormatException, IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("INCR"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public String set(String key, String value) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("SET"), new RESPBulkString(key), new RESPBulkString(value)
        }));
        this.writer.flush();

        return ((RESPString) this.reader.readRESP()).getString();
    }

    public String set(String key, Integer value) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("SET"), new RESPBulkString(key), new RESPNumber(value)
        }));
        this.writer.flush();

        return ((RESPString) this.reader.readRESP()).getString();
    }

    public String getString(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("GET"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPString) this.reader.readRESP()).getString();
    }

    public Integer getInteger(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("GET"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public Integer strlen(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("STRLEN"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public Integer del(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("DEL"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.reader.close();
        this.client.close();
    }
}
