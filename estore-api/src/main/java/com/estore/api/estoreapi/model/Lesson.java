package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Lesson {

    @JsonProperty("title")
    private String title;

    @JsonProperty("video")
    private String video;

    public Lesson(@JsonProperty("title") String title, @JsonProperty("video") String video) {
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
