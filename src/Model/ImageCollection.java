package Model;

import Connection.DAOMessage;

import javax.imageio.ImageIO;
import java.net.URL;

public class ImageCollection implements Aggregate {

    private ImageVision currentSlide;
    private int current;

    public ImageCollection() {
        current = 0;
    }

    private class ImageIterator implements Iterator {

        @Override
        public boolean hasNext() {
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
            if (this.hasNext()) {
                current++;
                return currentSlide;
            }

            return null;
        }

        private boolean hasPrevious(){
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
            if (hasPrevious()) {
                return currentSlide;
            }else return null;
        }
    }

    @Override
    public Iterator getIterator() {
        return new ImageIterator();
    }

    public int getCurrent(){
        return current;
    }

}