package it.unimib.finalproject.database.resp.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RESPArray implements List<RESPType>, RESPType {
    protected List<RESPType> array;

    public RESPArray(RESPType[] array) {
        this.array = Arrays.asList(array);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("*").append(array.size()).append("\r\n");
        for (var element : array)
            sb.append(element);
        return sb.toString();
    }

    @Override
    public int size() {
        return this.array.size();
    }

    @Override
    public boolean isEmpty() {
        return this.array.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.array.contains(o);
    }

    @Override
    public Iterator<RESPType> iterator() {
        return this.array.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.array.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.array.toArray(a);
    }

    @Override
    public boolean add(RESPType e) {
        return this.array.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.array.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.array.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends RESPType> c) {
        return this.array.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends RESPType> c) {
        return this.array.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.array.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.array.retainAll(c);
    }

    @Override
    public void clear() {
        this.array.clear();
    }

    @Override
    public RESPType get(int index) {
        return this.array.get(index);
    }

    @Override
    public RESPType set(int index, RESPType element) {
        return this.array.set(index, element);
    }

    @Override
    public void add(int index, RESPType element) {
        this.array.add(index, element);
    }

    @Override
    public RESPType remove(int index) {
        return this.array.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.array.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.array.lastIndexOf(o);
    }

    @Override
    public ListIterator<RESPType> listIterator() {
        return this.array.listIterator();
    }

    @Override
    public ListIterator<RESPType> listIterator(int index) {
        return this.array.listIterator(index);
    }

    @Override
    public List<RESPType> subList(int fromIndex, int toIndex) {
        return this.array.subList(fromIndex, toIndex);
    }
}
