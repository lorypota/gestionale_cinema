package it.unimib.finalproject.database.command.string;

import java.util.AbstractMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class GetCommand extends Command {

    @Override
    public String getCommandName() {
        return "GET";
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
            return RESPString.NULL;
        }

        var respValue = store.get(key);

        RESPType response = RESPString.NULL;
        if (respValue instanceof String) {
            response = new RESPBulkString((String) respValue);
        } else if (respValue instanceof Integer) {
            response = new RESPNumber((Integer) respValue);
        }

        return response;
    }
}
