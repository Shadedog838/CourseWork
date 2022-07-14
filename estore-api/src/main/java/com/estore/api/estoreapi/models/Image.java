package com.estore.api.estoreapi.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "courses")
public class Image {

    private String link;


    public Image() {
        
    }
    public Image(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
