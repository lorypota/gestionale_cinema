package it.unimib.finalproject.database.command;

import java.util.AbstractMap;

import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class DelCommand extends Command {

    @Override
    public String getCommandName() {
        return "DEL";
    }

    @Override
    public String getCommandSyntax() {
        return "DEL <key>";
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
        store.remove(keyString);

        return new RESPArray(RESPBulkString.OK);
    }
}
