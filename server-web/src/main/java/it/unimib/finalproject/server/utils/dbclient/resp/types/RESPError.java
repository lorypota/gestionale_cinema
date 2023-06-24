package it.unimib.finalproject.server.utils.dbclient.resp.types;

public class RESPError extends Throwable implements RESPType {
    protected String error;

    public RESPError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return String.format("-%s\r\n", this.error);
    }

    @Override
    public String getMessage() {
        return error;
    }
}
