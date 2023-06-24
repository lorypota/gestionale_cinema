package it.unimib.finalproject.server.utils.dbclient.resp.types;

public class RESPString implements RESPType {

    public static final RESPString OK = new RESPString("OK");

    protected String string;

    public RESPString(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    @Override
    public String toString() {
        if (this.string == null) {
            return "$-1\r\n";
        }
        return String.format("+%s\r\n", this.string);
    }
}
