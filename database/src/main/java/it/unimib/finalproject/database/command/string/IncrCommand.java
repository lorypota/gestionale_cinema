package it.unimib.finalproject.database.command.string;

import java.util.AbstractMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class IncrCommand extends Command {

    @Override
    public String getCommandName() {
        return "INCR";
    }

    @Override
    public String getCommandSyntax() {
        return "<key:string>";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (!(args.length == 1 && (args[0] instanceof RESPString))) {
            throw this.getSyntaxError();
        }

        var key = ((RESPString) args[0]).getString();
        if (!store.containsKey(key)) {
            store.put(key, 0);
        }

        var value = store.get(key);

        // If not integer, return error
        if (!(value instanceof Integer)) {
            throw new RESPError(String.format("Value referenced by \"%s\" must be a number", key));
        }

        return new RESPNumber((Integer) store.put(key, (Integer) value + 1));
    }

}
