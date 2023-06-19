package it.unimib.finalproject.database.command;

import java.util.AbstractMap;

import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class PingCommand extends Command {

    @Override
    public String getCommandName() {
        return "PING";
    }

    @Override
    public String getCommandDescription() {
        return "Returns PONG";
    }

    @Override
    public RESPArray execute(AbstractMap<String, Object> store, RESPType[] args) {
        return new RESPArray(new RESPBulkString("PONG"));
    }
}
