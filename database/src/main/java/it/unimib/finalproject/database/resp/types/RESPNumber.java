package it.unimib.finalproject.database.resp.types;

public class RESPNumber implements RESPType {
    protected int number;

    public RESPNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return ":" + number;
    }
}
