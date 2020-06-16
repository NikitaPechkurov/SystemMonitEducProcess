package Model;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class ImageVision implements Serializable {
    /**
     * Класс, предназначенный для представления картинки
     * в сообщении как массив пикселей.
     * @author Nikita Pechkurov
     * *@version 2
     */
    int width; int height; int[] pixels;

    public ImageVision(BufferedImage bi) {
        /**
         * Конструктор класса. Принмиает в качестве параметра экземпляр изображения
         * BufferedImage. Внутри класса происходит разбиение изображение на
         * пиксели и сохранение в массив.
         */
        width = bi.getWidth();
        height = bi.getHeight();
        pixels = new int[width * height];
        int[] tmp = bi.getRGB(0,0,width,height,pixels,0,width);
    }

    public BufferedImage getImage() {
        /**
         * Данный метод предназначен для возврата изображения
         * в виде BufferedImage.
         */
        BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0,0,width,height,pixels,0,width);
        return bi;
    }

}