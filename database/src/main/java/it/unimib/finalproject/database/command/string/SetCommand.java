package it.unimib.finalproject.database.command.string;

import java.util.AbstractMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNull;
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
        return "<key:string> <value:string|number>";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (!(args.length == 2 &&
                (args[0] instanceof RESPString) &&
                ((args[1] instanceof RESPString) || (args[1] instanceof RESPNumber)))) {
            throw this.getSyntaxError();
        }

        var key = ((RESPString) args[0]).getString();
        var respValue = args[1];

        if (store.containsKey(key) && !((store.get(key) instanceof String) || (store.get(key) instanceof Integer))) {
            return RESPNull.NULL;
        }

        Object previous = null;
        if (respValue instanceof RESPString) {
            previous = store.put(key, ((RESPString) respValue).getString());
        } else {
            previous = store.put(key, ((RESPNumber) respValue).getNumber());
        }

        if (previous == null) {
            return RESPString.OK;
        }

        return new RESPBulkString(previous.toString());
    }
}
