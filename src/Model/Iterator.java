package Model;

public interface Iterator {
    /**
     * Интерфейс, определяющий структуру итератора, включая необходимые
     * методы для выполнения итерации с помощью ImageIterator.
     * @author Nikita Pechkurov
     * *@version 2
     */
    public boolean hasNext();
    /**
     * Данный метод предназначен для проверки существования
     * следующего значения итератора.
     * @return
     */
    public Object next();
    /**
     * Данный метод предназначен для возврата
     * следующего значения итератора.
     * @return
     */
    public Object preview();
    /**
     * Данный метод предназначен для возврата
     * текущего значения итератора.
     * @return
     */
}