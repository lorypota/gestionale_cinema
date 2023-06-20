package it.unimib.finalproject.database.command.hash;

import java.util.AbstractMap;
import java.util.stream.Collectors;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class HKeysCommand extends Command {
    
    @Override
    public String getCommandName() {
        return "HKEYS";
    }

    @Override
    public String getCommandSyntax() {
        return "<key:string>";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        if (!(args.length == 1 &&
                (args[0] instanceof RESPString))) {
            throw this.getSyntaxError();
        }

        var key = ((RESPString) args[0]).getString();

        // Returns 0 if not a hash, or hash non-existent
        var hash = store.get(key);
        if (!(hash instanceof AbstractMap<?, ?>)) {
            return new RESPArray();
        }

        var hashValue = (AbstractMap<?, ?>) hash;

        return hashValue.keySet()
                .stream()
                .map(entry -> new RESPBulkString(entry.toString()))
                .collect(Collectors.toCollection(RESPArray::new));
    }
}
