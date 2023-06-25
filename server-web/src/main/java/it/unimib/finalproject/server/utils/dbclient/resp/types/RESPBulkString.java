package it.unimib.finalproject.server.utils.dbclient.resp.types;

import it.unimib.finalproject.server.utils.dbclient.resp.EscapeUtils;

public class RESPBulkString extends RESPString {

    public static final RESPString OK = new RESPBulkString("OK");

    public RESPBulkString(String string) {
        super(string);
    }

    @Override
    public String toString() {
        if (string == null) {
            return "$-1\r\n";
        }
        return String.format("$%d\r\n%s\r\n", string.length(), EscapeUtils.escape(string));
    }
}
