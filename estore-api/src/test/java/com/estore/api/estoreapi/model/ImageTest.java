package com.estore.api.estoreapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ImageTest {

    @Test
    public void testImage() {

        // Setup
        String expected_link = "image";

        // Invoke
        Image image = new Image(expected_link);

        // Analyze
        assertEquals(expected_link, image.getLink());
    }

    @Test
    public void testSetImage() {
        // Setup
        Image image = new Image("image1");
        image.setLink("image2");

        // Analyze
        assertEquals("image2", image.getLink());
    }
}
