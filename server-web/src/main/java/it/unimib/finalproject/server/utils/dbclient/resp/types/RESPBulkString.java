package it.unimib.finalproject.server.utils.dbclient.resp.types;

public class RESPBulkString extends RESPString {

    public static final RESPBulkString OK = new RESPBulkString("OK");

    public RESPBulkString(String string) {
        super(string);
    }

    @Override
    public String toString() {
        if (string == null) {
            return new RESPNull().toString();
        }
        return String.format("$%d\r\n%s\r\n", string.length(), string);
    }
}
