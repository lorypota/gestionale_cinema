package it.unimib.finalproject.server.utils.dbclient;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

import org.glassfish.hk2.api.PerLookup;

import it.unimib.finalproject.server.utils.dbclient.resp.EscapeUtils;
import it.unimib.finalproject.server.utils.dbclient.resp.RESPReader;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPArray;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPBulkString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPNumber;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPType;

@PerLookup
public class DbConnector implements Closeable {

    // TODO: Error handling, multiple type handling
    // TODO: Gestire RESPNull tramite Optional
    // TODO: Gestire multi type
    // TODO: Documenta i metodi
    private Socket client;
    private RESPReader reader;
    private PrintWriter writer;

    public DbConnector(String host, int port) throws UnknownHostException, IOException {
        client = new Socket(host, port);
        reader = new RESPReader(new BufferedReader(new InputStreamReader(client.getInputStream())));
        writer = new PrintWriter(client.getOutputStream());
    }

    /**
     * Returns information about commands supported by the server.
     * If a command is specified, returns information only for that command.
     * 
     * @param command Optional command to get information about
     * @return RESPArray containing information about commands
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public RESPArray command(Optional<String> command) throws NumberFormatException, IOException, RESPError {
        var cmdToSend = new RESPArray(new RESPType[] {
                new RESPString("COMMAND"),
        });
        if (command.isPresent())
            cmdToSend.add(new RESPString(command.get()));
        this.writer.print(cmdToSend);
        this.writer.flush();

        return (RESPArray) this.reader.readRESP();
    }

    /**
     * Can be used to check if the server is able to reply to commands.
     * 
     * @return PONG
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public String ping() throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPString("PING")));
        this.writer.flush();

        return ((RESPString) this.reader.readRESP()).getString();
    }

    /**
     * Returns all the keys that store a hashmap as value.
     * 
     * @return Array of keys
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public String[] hashes() throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPString("HASHES")));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString())
                .toArray(String[]::new);
    }

    /**
     * Returns all the keys that store a string as value.
     * 
     * @return Array of keys
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public String[] strings() throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPString("STRINGS")));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString())
                .toArray(String[]::new);
    }

    /**
     * Decrements the number stored at key by one.
     * 
     * @param key Key to decrement
     * @return The value of key after the decrement
     * @throws RESPError If the key does not exist or contains a value different than a number
     * @throws NumberFormatException
     * @throws IOException
     */
    public Integer decr(String key) throws RESPError, NumberFormatException, IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("DECR"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * Increments the number stored at key by one.
     * 
     * @param key Key to increment
     * @return The value of key after the increment
     * @throws RESPError If the key does not exist or contains a value different than a number
     * @throws NumberFormatException
     * @throws IOException
     */
    public Integer incr(String key) throws RESPError, NumberFormatException, IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("INCR"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * Sets a String key=value pair.
     * 
     * @param key Key to set
     * @param value Value to set
     * @return OK if the key was set, null if the key already exists and is not a string or integer value and the previous value of key as a String if it was overwritten
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Optional<String> set(String key, String value) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("SET"), new RESPBulkString(EscapeUtils.escape(value)), new RESPBulkString(EscapeUtils.escape(value))
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPString) this.reader.readRESP()).getString());
    }

    /**
     * Sets a Integer key=value pair.
     * 
     * @param key Key to set
     * @param value Value to set
     * @return OK if the key was set, null if the key already exists and is not a string or integer value and the previous value of key as a String if it was overwritten
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Optional<String> set(String key, Integer value) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("SET"), new RESPBulkString(key), new RESPNumber(value)
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPString) this.reader.readRESP()).getString());
    }

    public String getString(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("GET"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPString) this.reader.readRESP()).getString();
    }

    public Integer getInteger(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("GET"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * Deletes a key-value pair.
     * 
     * @param key Key to delete
     * @return 1 if the key was deleted, 0 if the key did not exist
     * @throws NumberFormatException
     * @throws IOException
     */
    public Integer del(String key) throws NumberFormatException, IOException {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("DEL"), new RESPBulkString(key)
        }));
        this.writer.flush();


        try {
            return ((RESPNumber) this.reader.readRESP()).getNumber();
        } catch (RESPError e) {
            // Unreachable
            return 0;
        }
    }

    /**
     * Returns the lenght of the string stored at key.
     * 
     * @param key Key to check
     * @return The lenght of the string stored at key, 0 if the key does not exist
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError If the key contains a value different than a string
     */
    public Integer strlen(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("STRLEN"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * @param key
     * @param field
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Integer hdel(String key, String field) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HDEL"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * @param key
     * @param field
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Integer hexists(String key, String field) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HEXISTS"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * @param key
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public RESPArray hgetall(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HGETALL"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return (RESPArray) this.reader.readRESP();
    }

    /**
     * @param key
     * @param field
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Optional<String> hgetString(String key, String field) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HGET"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPString) this.reader.readRESP()).getString());
    }

    /**
     * @param key
     * @param field
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Optional<Integer> hgetInteger(String key, String field) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HGET"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPNumber) this.reader.readRESP()).getNumber());
    }

    /**
     * @param key
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public String[] hkeys(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HKEYS"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString())
                .toArray(String[]::new);
    }

    /**
     * @param key
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Integer hlen(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HLEN"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * @param key
     * @param field
     * @param value
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Integer hset(String key, String field, String value) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HSET"), new RESPBulkString(key), new RESPBulkString(field),
                new RESPBulkString(value)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * @param key
     * @param field
     * @param value
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Integer hset(String key, String field, Integer value) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HSET"), new RESPBulkString(key), new RESPBulkString(field), new RESPNumber(value)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * @param key
     * @param field
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Integer hstrlen(String key, String field) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HSTRLEN"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * @param key
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public String[] hvals(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HVALS"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString())
                .toArray(String[]::new);
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.reader.close();
        this.client.close();
    }
}
