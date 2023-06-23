package it.unimib.finalproject.server.utils.dbclient.resp.types;

public class RESPNumber implements RESPType {
    public static final RESPNumber ONE = new RESPNumber(1);
    public static final RESPNumber ZERO = new RESPNumber(0);

    protected int number;

    public RESPNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return String.format(":%d\r\n", this.number);
    }
}
