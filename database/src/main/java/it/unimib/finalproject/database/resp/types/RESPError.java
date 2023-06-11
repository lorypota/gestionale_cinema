package it.unimib.finalproject.database.resp.types;

public class RESPError implements RESPType{
    protected String error;

    public RESPError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "-" + error;
    }
}
