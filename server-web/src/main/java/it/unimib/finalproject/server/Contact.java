package it.unimib.finalproject.server;

/**
 * Rappresenta un contatto della rubrica telefonica.
 */
public class Contact {
    // Identificativo univoco del contatto.
    private int id;

    // Nome del contatto.
    private String name;

    // Numero di telefono associato al contatto.
    private String number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
