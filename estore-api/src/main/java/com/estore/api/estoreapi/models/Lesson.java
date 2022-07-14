package com.estore.api.estoreapi.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "courses")
public class Lesson {

    private String title;

    private String video;


    public Lesson() {
        
    }
    public Lesson(String title, String video) {
        this.title = title;
        this.video = video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

}
