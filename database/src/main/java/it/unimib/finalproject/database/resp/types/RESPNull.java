package it.unimib.finalproject.database.resp.types;

public class RESPNull implements RESPType {
    @Override
    public String toString() {
        return "$-1\r\n";
    }
}
