package it.unimib.finalproject.server.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

import it.unimib.finalproject.server.config.DatabaseConfigs;
import it.unimib.finalproject.server.utils.dbclient.resp.RESPReader;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPArray;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPBulkString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPNumber;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPType;
import jakarta.inject.Singleton;

@Singleton
public class DbConnector implements Closeable {

    // TODO: Error handling, multiple type handling
    // TODO: Gestire RESPNull tramite Optional
    // TODO: Gestire multi type
    // TODO: Documenta i metodi
    private Socket client;
    private RESPReader reader;
    private PrintWriter writer;

    public DbConnector() throws UnknownHostException, IOException{
        this(DatabaseConfigs.DATABASE_HOST, DatabaseConfigs.DATABASE_PORT);
    }

    public DbConnector(String host, int port) throws UnknownHostException, IOException {
        client = new Socket(host, port);
        reader = new RESPReader(new BufferedReader(new InputStreamReader(client.getInputStream())));
        writer = new PrintWriter(client.getOutputStream());
    }

    public RESPArray command(Optional<String> command) throws NumberFormatException, IOException {
        var cmdToSend = new RESPArray(new RESPType[] {
            new RESPString("COMMAND"),
        });
        if (command.isPresent())
            cmdToSend.add(new RESPString(command.get()));
        this.writer.print(cmdToSend);
        this.writer.flush();
        
        return (RESPArray) this.reader.readRESP();
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

    public Integer del(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("DEL"), new RESPBulkString(key)
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

    public Integer hdel(String key, String field) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HDEL"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public Integer hexists(String key, String field) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HEXISTS"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public RESPArray hgetall(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HGETALL"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return (RESPArray) this.reader.readRESP();
    }

    public Optional<RESPString> hget(String key, String field) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HGET"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return Optional.ofNullable((RESPString) this.reader.readRESP());
    }

    public String[] hkeys(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HKEYS"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString()).toArray(String[]::new);
    }

    public Integer hlen(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HLEN"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public Integer hset(String key, String field, String value) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HSET"), new RESPBulkString(key), new RESPBulkString(field), new RESPBulkString(value)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public Integer hset(String key, String field, Integer value) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HSET"), new RESPBulkString(key), new RESPBulkString(field), new RESPNumber(value)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public Integer hstrlen(String key, String field) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HSTRLEN"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    public String[] hvals(String key) throws IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HVALS"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString()).toArray(String[]::new);
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.reader.close();
        this.client.close();
    }
}
