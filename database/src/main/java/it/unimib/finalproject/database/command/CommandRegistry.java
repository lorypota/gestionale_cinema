package it.unimib.finalproject.database.command;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {

    private static final List<Command> commands = new ArrayList<>();

    public static Command registerCommand(Command command) {
        commands.add(command);
        return command;
    }

    public static Command get(String expectedCommand) throws Exception {

        if (expectedCommand == null) {
            throw new Exception("Invalid command");
        }

        for (Command command : commands) {
            if (command.getCommandName().equalsIgnoreCase(expectedCommand)) {
                return command;
            }
        }

        throw new Exception("Invalid command");
    }
}
