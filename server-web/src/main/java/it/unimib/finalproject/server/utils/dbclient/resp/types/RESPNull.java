package it.unimib.finalproject.server.utils.dbclient.resp.types;

public class RESPNull extends RESPString {

    RESPNull() {
        super(null);
    }

    public static final RESPNull NULL = new RESPNull();

    @Override
    public String getString() {
        return null;
    }

    @Override
    public String toString() {
        return "$-1\r\n";
    }
}
