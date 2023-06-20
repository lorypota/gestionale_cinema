package it.unimib.finalproject.database.command.hash;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class HSetCommand extends Command {

    @Override
    public String getCommandName() {
        return "HSET";
    }

    @Override
    public String getCommandSyntax() {
        return "<key:string> <field:string> <value:string|number>";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (!(args.length == 3 &&
                (args[0] instanceof RESPString) &&
                (args[1] instanceof RESPString) &&
                ((args[2] instanceof RESPString) || (args[2] instanceof RESPNumber)))) {
            throw this.getSyntaxError();
        }

        var key = ((RESPString) args[0]).getString();
        var field = ((RESPString) args[1]).getString();
        var value = args[2];

        // Creates the hash if non-existent
        if (!store.containsKey(key)) {
            store.put(key, new ConcurrentHashMap<String, Object>());
        }

        // Returns 0 if key not hash
        var hash = store.get(key);
        if (!(hash instanceof AbstractMap<?, ?>)) {
            return RESPNumber.ZERO;
        }

        var hashValue = (AbstractMap<String, Object>) hash;

        // If the containing value is not a string or a number, returns 0
        if (hashValue.containsKey(field)
                && !((hashValue.get(field) instanceof String) || (hashValue.get(field) instanceof Integer))) {
            return RESPNumber.ZERO;
        }

        if (value instanceof RESPString) {
            hashValue.put(field, ((RESPString) value).getString());
        } else {
            hashValue.put(field, ((RESPNumber) value).getNumber());
        }

        return RESPNumber.ONE;
    }

}
