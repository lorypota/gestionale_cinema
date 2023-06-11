package it.unimib.finalproject.database.command;

import java.util.AbstractMap;

import it.unimib.finalproject.database.resp.types.RESPType;

public abstract class Command {
    public abstract String getCommandName();

    public String getCommandSyntax() {
        return this.getCommandName();
    }

    public String getCommandDescription() {
        return "";
    }

    public String getCommandExample() {
        return this.getCommandName();
    }

    public abstract RESPType execute(AbstractMap<String, Object> store, String[] args) throws Exception;

    @Override
    public String toString() {
        return String.format("Usage: %s\r\n" +
                "--------------------\r\n" +
                "%s\r\n\r\n" +
                "%s", this.getCommandSyntax(), this.getCommandDescription(), this.getCommandExample());
    }
}
