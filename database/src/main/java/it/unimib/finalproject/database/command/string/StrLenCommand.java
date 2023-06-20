package it.unimib.finalproject.database.command.string;

import java.util.AbstractMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class StrLenCommand extends Command {

    @Override
    public String getCommandName() {
        return "STRLEN";
    }

    @Override
    public String getCommandSyntax() {
        return "<key:String>";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (!(args.length == 1 && (args[0] instanceof RESPString))) {
            throw this.getSyntaxError();
        }

        var key = ((RESPString) args[0]).getString();
        if (!store.containsKey(key)) {
            return RESPNumber.ZERO;
        }

        var valueObj = store.get(key);

        // If not string, return error
        if (!(valueObj instanceof String)) {
            throw new RESPError(String.format("Value referenced by \"%s\" must be a string", key));
        }

        return new RESPNumber(((String) valueObj).length());
    }

}
