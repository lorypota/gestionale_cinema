package it.unimib.finalproject.database.command;

import java.util.ArrayList;
import java.util.List;

import it.unimib.finalproject.database.resp.types.RESPError;

public class CommandRegistry {

    private static final List<Command> commands = new ArrayList<>();

    public static Command registerCommand(Command command) {
        commands.add(command);
        return command;
    }

    public static Command get(String expectedCommand) throws RESPError {

        if (expectedCommand == null) {
            throw new RESPError("Invalid command");
        }

        for (Command command : commands) {
            if (command.getCommandName().equalsIgnoreCase(expectedCommand)) {
                return command;
            }
        }

        throw new RESPError("Invalid command");
    }
}
