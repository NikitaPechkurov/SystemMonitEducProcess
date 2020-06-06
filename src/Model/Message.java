package Model;


import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class Message implements Serializable{

    String id;
    String id_user;
    String id_slide;
    String message;
    String type;
    ImageVision image;

    public Message(String id_user, String id_slide, String message, String type) throws IOException{
        this.id_user = id_user;
        this.id_slide = id_slide;
        this.message = message;
        this.type = type;
        image = new ImageVision(ImageIO.read(new URL("https://i.imgur.com/L0iDcra.png")));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getId_user() { return id_user; }

    public void setId_user(String id_user) { this.id_user = id_user; }

    public String getId_slide() { return id_slide; }

    public void setId_slide(String id_slide) { this.id_slide = id_slide; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public String getType() {
        return type;
    }

    public void setType(String type) { this.type = type; }

    public ImageVision getImageVision(){return image;}

    public void setImageVision(ImageVision image){this.image = image;}

    public String record(){ return "'"+id_user+"','"+id_slide+"','"+message+"','"+ type+"'"; }

    public String toString(){ return id_user+", "+id_slide+", "+message+", "+ type; }
}
