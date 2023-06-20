package it.unimib.finalproject.database.command.hash;

import java.util.AbstractMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNull;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class HGetCommand extends Command {

    @Override
    public String getCommandName() {
        return "HGET";
    }

    @Override
    public String getCommandSyntax() {
        return "<key:string> <field:string>";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (!(args.length == 2 &&
                (args[0] instanceof RESPString) &&
                (args[1] instanceof RESPString))) {
            throw this.getSyntaxError();
        }

        var key = ((RESPString) args[0]).getString();
        var field = ((RESPString) args[1]).getString();

        // Returns (nil) if not a hash, or hash non-existent
        var hash = store.get(key);
        if (!(hash instanceof AbstractMap<?, ?>)) {
            return RESPNull.NULL;
        }

        var hashValue = (AbstractMap<?, ?>) hash;
        var value = hashValue.get(field);

        RESPType response = RESPNull.NULL;
        if (value instanceof String) {
            response = new RESPBulkString((String) value);
        } else if (value instanceof Integer) {
            response = new RESPNumber((Integer) value);
        }

        return value == null ? RESPNull.NULL : response;
    }

}
