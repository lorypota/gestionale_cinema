package it.unimib.finalproject.database.command;

import java.util.AbstractMap;
import java.util.stream.Collectors;

import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNull;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class GetCommand extends Command {

    @Override
    public String getCommandName() {
        return "GET";
    }

    @Override
    public String getCommandSyntax() {
        return "GET <key>";
    }

    @Override
    public RESPArray execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (args.length != 1) {
            this.getSyntaxError();
        }

        var key = args[0];
        if (!(key instanceof RESPString)) {
            throw new RESPError("Key must be a string");
        }

        var keyString = ((RESPString) args[0]).getString();
        if (!store.containsKey(keyString)) {
            return new RESPArray(RESPNull.NULL);
        }

        return store.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(keyString))
                .filter(e -> e.getValue() instanceof String || e.getValue() instanceof Double)
                .map(e -> new RESPBulkString(e.getValue().toString()))
                .collect(Collectors.toCollection(RESPArray::new));
    }
}
