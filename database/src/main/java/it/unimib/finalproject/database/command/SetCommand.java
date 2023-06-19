package it.unimib.finalproject.database.command;

import java.util.AbstractMap;

import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class SetCommand extends Command {

    @Override
    public String getCommandName() {
        return "SET";
    }

    @Override
    public String getCommandSyntax() {
        return "SET <key> <value>";
    }

    @Override
    public RESPArray execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (args.length != 2) {
            this.getSyntaxError();
        }

        var key = args[0];

        if (!(key instanceof RESPString)) {
            throw new RESPError("Key must be a string");
        }

        var keyString = ((RESPString) args[0]).getString();
        var value = args[1];
        Object valueObj = null;

        if (value instanceof RESPString) {
            valueObj = ((RESPString) value).getString();
        } else if (value instanceof RESPNumber) {
            valueObj = ((RESPNumber) value).getNumber();
        }

        store.put(keyString, valueObj);

        return new RESPArray(RESPBulkString.OK);
    }
}
