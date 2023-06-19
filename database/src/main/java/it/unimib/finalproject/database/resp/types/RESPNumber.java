package it.unimib.finalproject.database.resp.types;

public class RESPNumber implements RESPType {
    protected double number;

    public RESPNumber(double number) {
        this.number = number;
    }

    public double getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return String.format(":%s\r\n", this.number);
    }
}
