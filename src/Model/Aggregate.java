package Model;

public interface Aggregate {
    /**
     * Данный интерфейс предназначен для определения структур классов,
     * которые можно повторять.
     * @author Nikita Pechkurov
     * *@version 2
     */
    public Iterator getIterator();
    /**
     * Метод, возвращающий итератор из класса-реализации.
     */
}