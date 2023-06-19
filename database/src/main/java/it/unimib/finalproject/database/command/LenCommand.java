package it.unimib.finalproject.database.command;

import java.util.AbstractMap;

import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class LenCommand extends Command {

    @Override
    public String getCommandName() {
        return "LEN";
    }

    @Override
    public String getCommandSyntax() {
        return "LEN <key>";
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
            throw new RESPError(String.format("Key %s does not exist", keyString));
        }

        var valueObj = store.get(keyString);

        // If not string or not number, return error
        if (!(valueObj instanceof String || valueObj instanceof Integer)) {
            throw new RESPError(String.format("Value referenced by \"%s\" must be a string or a number", keyString));
        }

        var value = new RESPString(String.valueOf(valueObj.toString().length()));

        return new RESPArray(value);
    }

}
