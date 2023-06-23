package it.unimib.finalproject.database.command.string;

import java.util.AbstractMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class DelCommand extends Command {

    @Override
    public String getCommandName() {
        return "DEL";
    }

    @Override
    public String getCommandSyntax() {
        return "<key:string>";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (!(args.length == 1 && args[0] instanceof RESPString)) {
            throw this.getSyntaxError();
        }

        var key = ((RESPString) args[0]).getString();
        if (!store.containsKey(key)) {
            return RESPNumber.ZERO;
        }
        store.remove(key);

        return RESPNumber.ONE;
    }
}
