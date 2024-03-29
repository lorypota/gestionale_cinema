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

import it.unimib.finalproject.server.utils.dbclient.resp.RESPReader;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPArray;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPBulkString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPNumber;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPString;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPType;

/**
 * @author DamianoPellegrini <d.pellegrini10@campus.unimib.it>
 */
@PerLookup
public class DbConnector implements Closeable {

    // TODO: Gestire multi type
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
        if (command.isPresent())
            this.writer.print(new RESPArray(new RESPType[] {
                    new RESPString("COMMAND"),
                    new RESPString(command.get())
            }));
        else
            this.writer.print(new RESPString("COMMAND"));
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
        this.writer.print(new RESPString("PING"));
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
        this.writer.print(new RESPString("HASHES"));
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
        this.writer.print(new RESPString("STRINGS"));
        this.writer.flush();

        return ((RESPArray) this.reader.readRESP()).stream().map(r -> ((RESPString) r).getString())
                .toArray(String[]::new);
    }

    /**
     * Decrements the number stored at key by one.
     * 
     * @param key Key to decrement
     * @return The value of key after the decrement
     * @throws RESPError             If the key does not exist or contains a value
     *                               different than a number
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
     * @throws RESPError             If the key does not exist or contains a value
     *                               different than a number
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
     * @param key   Key to set
     * @param value Value to set
     * @return OK if the key was set, null if the key already exists and is not a
     *         string or integer value and the previous value of key as a String if
     *         it was overwritten
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Optional<String> set(String key, String value) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("SET"), new RESPBulkString(key),
                new RESPBulkString(value)
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPString) this.reader.readRESP()).getString());
    }

    /**
     * Sets a Integer key=value pair.
     * 
     * @param key   Key to set
     * @param value Value to set
     * @return OK if the key was set, null if the key already exists and is not a
     *         string or integer value, the previous value of key as a String if
     *         it was overwritten
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

    public Optional<String> getString(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("GET"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPString) this.reader.readRESP()).getString());
    }

    public Optional<Integer> getInteger(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("GET"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPNumber) this.reader.readRESP()).getNumber());
    }

    /**
     * Deletes a key-value pair.
     * 
     * @param key Key to delete
     * @return 1 if the key was deleted, 0 if the key did not exist
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Integer del(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("DEL"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * Returns the lenght of the string stored at key.
     * 
     * @param key Key to check
     * @return The lenght of the string stored at key, 0 if the key does not exist
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError             If the key contains a value different than a
     *                               string
     */
    public Integer strlen(String key) throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("STRLEN"), new RESPBulkString(key)
        }));
        this.writer.flush();

        return ((RESPNumber) this.reader.readRESP()).getNumber();
    }

    /**
     * Deletes a field from a hash.
     * 
     * @param key   Key to the hash
     * @param field Field to delete
     * @return 1 if the field was deleted, 0 otherwise
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
     * Checks if a field exists in a hash.
     * 
     * @param key   Key to the hash
     * @param field Field to check
     * @return 1 if the field exists, 0 otherwise
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
     * Returns key-value pairs in a hash.
     * 
     * @param key Key to the hash
     * @return An array of key-value pairs
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
     * Returns the value of a field in a hash.
     * 
     * @param key   Key to the hash
     * @param field Field to get
     * @return The value of the field, null if the field does not exist
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
     * Returns the value of a field in a hash.
     * 
     * @param key   Key to the hash
     * @param field Field to get
     * @return The value of the field, null if the field does not exist
     * @throws NumberFormatException
     * @throws IOException
     * @throws RESPError
     */
    public Optional<Integer> hgetInteger(String key, String field)
            throws NumberFormatException, IOException, RESPError {
        this.writer.print(new RESPArray(new RESPType[] {
                new RESPBulkString("HGET"), new RESPBulkString(key), new RESPBulkString(field)
        }));
        this.writer.flush();

        return Optional.ofNullable(((RESPNumber) this.reader.readRESP()).getNumber());
    }

    /**
     * Returns the fields in a hash.
     * 
     * @param key Key to the hash
     * @return An array of fields
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
     * Returns the number of fields in a hash.
     * 
     * @param key Key to the hash
     * @return The number of fields in the hash, 0 if the key does not exist
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
     * Sets the value of a field in a hash.
     * 
     * @param key   Key to the hash
     * @param field Field to set
     * @param value Value to set
     * @return 1 if the field was created or updated, 0 otherwise
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
     * Sets the value of a field in a hash.
     * 
     * @param key   Key to the hash
     * @param field Field to set
     * @param value Value to set
     * @return 1 if the field was created or updated, 0 otherwise
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
     * Returns the length of the value of a field in a hash.
     * 
     * @param key   Key to the hash
     * @param field Field to get
     * @return The length of the value of the field, 0 if the field does not exist
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
     * Returns the values of all fields in a hash.
     * 
     * @param key Key to the hash
     * @return An array of values
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
