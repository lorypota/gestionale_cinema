package it.unimib.finalproject.server.model;

public class Hall{
    private int id;
    private int rows;
    private int columns;

    public Hall(int id, int rows, int columns) {
        this.id = id;
        this.rows = rows;
        this.columns = columns;
    }

    public int getId() {
        return id;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
