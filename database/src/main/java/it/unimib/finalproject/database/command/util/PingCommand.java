package it.unimib.finalproject.database.command.util;

import java.util.AbstractMap;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.resp.types.RESPString;
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
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) {
        return new RESPString("PONG");
    }
}
