package it.unimib.finalproject.server.utils.dbclient.resp.types;

public class RESPNull implements RESPType {

    public static final RESPNull NULL = new RESPNull();

    @Override
    public String toString() {
        return "$-1\r\n";
    }
}
