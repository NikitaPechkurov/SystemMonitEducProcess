package Model;

import javafx.beans.property.SimpleStringProperty;

public class User {
    SimpleStringProperty id;
    SimpleStringProperty username;
    SimpleStringProperty password;
    SimpleStringProperty post;

    public User(String id, String username, String password,String post) {
        this.id = new SimpleStringProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.post = new SimpleStringProperty(post);
    }

    public String getId() { return id.get(); }

    public SimpleStringProperty idProperty() { return id; }

    public void setId(String id) { this.id.set(id); }

    public String getUsername() { return username.get(); }

    public SimpleStringProperty usernameProperty() { return username; }

    public void setUsername(String username) { this.username.set(username); }

    public String getPassword() { return password.get(); }

    public SimpleStringProperty passwordProperty() { return password; }

    public void setPassword(String password) { this.password.set(password); }

    public String getPost() { return post.get(); }

    public SimpleStringProperty postProperty() { return post; }

    public void setPost(String post) { this.post.set(post); }

    public String record(){
        return username+","+password +", "+ post;
    }

    public String toString(){return " "+id.get()+", "+username.get()+", "+ password.get()+", "+ post.get();}
}
