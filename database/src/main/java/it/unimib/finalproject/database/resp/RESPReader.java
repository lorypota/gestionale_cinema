package it.unimib.finalproject.database.resp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class RESPReader extends Reader {

    private BufferedReader reader;

    public RESPReader(BufferedReader reader) {
        this.reader = reader;
    }

    public RESPType readRESP() throws IOException, NumberFormatException {

        var input = this.reader.readLine();
        if (input == null) {
            return null;
        }

        if (!input.matches("([+\\-:$*])\\w+")) {
            return new RESPError(String.format("Unknown command: %s", input));
        }

        switch (input.charAt(0)) {
            case '+':
                return new RESPString(input.substring(1));
            case '-':
                return new RESPError(input.substring(1));
            case ':':
                return new RESPNumber(Integer.parseInt(input.substring(1)));
            case '$':
                var length = Integer.parseInt(input.substring(1));
                if (length == -1) {
                    return new RESPBulkString(null);
                }

                var buffer = new char[length];
                this.reader.read(buffer, 0, length);
                this.reader.readLine();

                return new RESPBulkString(new String(buffer));
            case '*':
                var count = Integer.parseInt(input.substring(1));
                var array = new RESPType[count];

                for (int i = 0; i < count; i++) {
                    array[i] = this.readRESP();
                }

                return new RESPArray(array);
            default:
                return new RESPError(String.format("Unknown command: %s", input));
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return this.reader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}