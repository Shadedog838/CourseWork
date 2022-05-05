package com.estore.api.estoreapi.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Course {
    @JsonProperty("id")
    private final int id;

    @JsonProperty("image")
    private Image image;

    @JsonProperty("title")
    private String title;

    @JsonProperty("price")
    private double price;

    @JsonProperty("description")
    private String description;

    @JsonProperty("studentsEnrolled")
    private int studentsEnrolled;

    @JsonProperty("tags")
    private Set<String> tags;

    @JsonProperty("content")
    private List<Lesson> content;

    public Course(@JsonProperty("id") int id, @JsonProperty("image") Image image, @JsonProperty("title") String title,
            @JsonProperty("price") double price,
            @JsonProperty("description") String description, @JsonProperty("studentsEnrolled") int studentsEnrolled,
            @JsonProperty("tags") Set<String> tags, @JsonProperty("content") List<Lesson> content) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.price = price;
        this.description = description;
        this.studentsEnrolled = studentsEnrolled;
        this.tags = tags;
        this.content = content;
    }

    /**
     * test constructor for writing unit tests
     * 
     * @param id
     * @param title
     * @param price
     * @param description
     * @param tags
     */
    public Course(int id, String title, double price, String description, Set<String> tags) {
        this.id = id;
        this.image = null;
        this.title = title;
        this.price = price;
        this.description = description;
        this.studentsEnrolled = 0;
        this.tags = tags;
        this.content = new ArrayList<>();
    }

    /**
     * another test constructor for writing unit tests
     * 
     * @param id
     * @param title
     * @param price
     * @param description
     * @param tags
     */
    public Course(int id, String title, double price, String description) {
        this.id = id;
        this.image = null;
        this.title = title;
        this.price = price;
        this.description = description;
        this.studentsEnrolled = 0;
        this.tags = new HashSet<>();
        this.content = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public void setStudentsEnrolled(int studentsEnrolled) {
        this.studentsEnrolled = studentsEnrolled;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public List<Lesson> getContent() {
        return content;
    }

    public void setContent(List<Lesson> content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        return id + title.hashCode() + description.hashCode() + studentsEnrolled + tags.hashCode() + content.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Course) {
            Course other = (Course) obj;
            // TODO: add more checks here
            return (id == other.id) && (title.equals(other.title)) && (description.equals(other.description));
        }
        return false;
    }

    @Override
    public String toString() {
        return "Course(id=" + id + ",title=" + title + ",description=" + description;
    }
}
