package it.unimib.finalproject.database.command.util;

import java.util.AbstractMap;
import java.util.stream.Collectors;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPType;

public class StringsCommand extends Command {

    @Override
    public String getCommandName() {
        return "STRINGS";
    }

    @Override
    public RESPArray execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (args.length != 0) {
            throw this.getSyntaxError();
        }

        return store.entrySet()
                .stream()
                .filter(e -> e.getValue() instanceof String)
                .map(e -> e.getKey())
                .map(RESPBulkString::new)
                .collect(Collectors.toCollection(RESPArray::new));
    }

}
