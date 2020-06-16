package Model;

import Connection.DAOMessage;

import javax.imageio.ImageIO;
import java.net.URL;

public class ImageCollection implements Aggregate {
    /**
     * Класс-реализация интерфейса Aggregate. Внутри данного класса
     * реализуется класс ImageIterator, предназначенный для работы
     * с изображениями.
     * @author Nikita Pechkurov
     * *@version 2.1
     */
    private ImageVision currentSlide;
    private int current;

    public ImageCollection() {
        /**
         * Конструктор класса по умолчанию. Устанавливает
         * значение текущего слайда в 0.
         */
        current = 0;
    }

    private class ImageIterator implements Iterator {
        /**
         * Класс, предназначенный для перемещения по слайдам.
         * Реализует интерфейс Итератор.
         * @author Nikita Pechkurov
         * *@version 2.1
         */
        @Override
        public boolean hasNext() {
            /**
             * Реализация метода, проверяющего наличие следующей
             * картинки в базе данных. Возвращает утверждение true или false.
             */
            String slideNum = String.valueOf(current + 1);
            try {
                URL url = new URL(DAOMessage.searchImageMessage(slideNum).getMessage());
                currentSlide = new ImageVision(ImageIO.read(url));
                System.out.println("Номер выданного слайда:" + slideNum);
                return true;
            } catch (Exception ex) {
                System.err.println("Неудалось загрузить картинку! " + slideNum);
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        public Object next() {
            /**
             * Реализация метода возврата следующего изображения.
             * Возвращает экземпляр класса Object.
             */
            if (this.hasNext()) {
                current++;
                return currentSlide;
            }

            return null;
        }

        private boolean hasPrevious(){
            /**
             * Метод проверки наличия предыдущего (текущего)
             * изображения в базе данных.
             * Возвращает true или false.
             */
            current--;
            String slideNum = String.valueOf(current);
            try {
                URL url = new URL(DAOMessage.searchImageMessage(slideNum).getMessage());
                currentSlide = new ImageVision(ImageIO.read(url));
                System.out.println("Номер выданного слайда:" + slideNum);
                return true;
            } catch (Exception ex) {
                System.err.println("Неудалось загрузить картинку! " + slideNum);
                ex.printStackTrace();
                return false;
            }
        }


        public Object preview() {
            /**
             * Реализация метода, возвращающего текущий слайд.
             */
            if (hasPrevious()) {
                return currentSlide;
            }else return null;
        }
    }

    @Override
    public Iterator getIterator() {
        /**
         * Реализация метода, возвращающего итератор.
         */
        return new ImageIterator();
    }

    public int getCurrent(){
        /**
         * Данный метод предназначен для возврата текущего
         * значения слайда.
         */
        return current;
    }

}