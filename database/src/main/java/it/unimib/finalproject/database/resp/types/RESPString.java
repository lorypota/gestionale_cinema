package it.unimib.finalproject.database.resp.types;

public class RESPString implements RESPType {

    public static final RESPString OK = new RESPString("OK");

    protected String string;

    public RESPString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return "+" + string;
    }
}
