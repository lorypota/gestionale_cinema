package it.unimib.finalproject.database.command.util;

import java.util.AbstractMap;
import java.util.stream.Collectors;

import it.unimib.finalproject.database.command.Command;
import it.unimib.finalproject.database.command.CommandRegistry;
import it.unimib.finalproject.database.resp.types.RESPArray;
import it.unimib.finalproject.database.resp.types.RESPBulkString;
import it.unimib.finalproject.database.resp.types.RESPError;
import it.unimib.finalproject.database.resp.types.RESPNumber;
import it.unimib.finalproject.database.resp.types.RESPString;
import it.unimib.finalproject.database.resp.types.RESPType;

public class CommandCommand extends Command {
    protected CommandRegistry registry;

    public CommandCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getCommandName() {
        return "COMMAND";
    }

    @Override
    public String getCommandSyntax() {
        return "[command:String]";
    }

    @Override
    public RESPType execute(AbstractMap<String, Object> store, RESPType[] args) throws RESPError {
        String commandName = null;
        if (args.length > 0) {
            if (!(args[0] instanceof RESPString))
                throw this.getSyntaxError();

            commandName = ((RESPString) args[0]).getString();
        }

        if (commandName != null) {
            var command = registry.get(commandName);

            return getCommandArray(command);
        }

        return registry.stream().map(this::getCommandArray).collect(Collectors.toCollection(RESPArray::new));
    }

    private RESPArray getCommandArray(Command command) {
        var argsCount = command.getCommandSyntax().split(" ").length;
        var arity = 1 + argsCount;

        return new RESPArray(new RESPType[] {
                // Name
                new RESPBulkString(command.getCommandName()),
                // Arity
                new RESPNumber(arity),
                // Flags
                new RESPArray(),
                // First Key
                new RESPNumber(argsCount > 0 ? 1 : 0),
                // Last Key
                new RESPNumber(argsCount),
                // Step Count
                new RESPNumber(1),
        });
    }
}
