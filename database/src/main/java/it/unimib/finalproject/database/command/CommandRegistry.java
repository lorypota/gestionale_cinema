package it.unimib.finalproject.database.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import it.unimib.finalproject.database.resp.types.RESPError;

public class CommandRegistry implements List<Command> {

    private final List<Command> commands = new ArrayList<>();
    private static final CommandRegistry instance = new CommandRegistry();

    private CommandRegistry() {}

    public static CommandRegistry inst() {
        return instance;
    }

    public Command registerCommand(Command command) {
        this.add(command);
        return command;
    }

    public Command get(String expectedCommand) throws RESPError {

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

    @Override
    public int size() {
        return this.commands.size();
    }

    @Override
    public boolean isEmpty() {
        return this.commands.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.commands.contains(o);
    }

    @Override
    public Iterator<Command> iterator() {
        return this.commands.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.commands.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.commands.toArray(a);
    }

    @Override
    public boolean add(Command e) {
        return this.commands.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.commands.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.commands.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Command> c) {
        return this.commands.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Command> c) {
        return this.commands.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.commands.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.commands.retainAll(c);
    }

    @Override
    public void clear() {
        this.commands.clear();
    }

    @Override
    public Command get(int index) {
        return this.commands.get(index);
    }

    @Override
    public Command set(int index, Command element) {
        return this.commands.set(index, element);
    }

    @Override
    public void add(int index, Command element) {
        this.commands.add(index, element);
    }

    @Override
    public Command remove(int index) {
        return this.commands.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.commands.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.commands.lastIndexOf(o);
    }

    @Override
    public ListIterator<Command> listIterator() {
        return this.commands.listIterator();
    }

    @Override
    public ListIterator<Command> listIterator(int index) {
        return this.commands.listIterator(index);
    }

    @Override
    public List<Command> subList(int fromIndex, int toIndex) {
        return this.commands.subList(fromIndex, toIndex);
    }
}
