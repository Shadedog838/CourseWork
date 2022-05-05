package com.estore.api.estoreapi.controller.requests;

import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An authenticated course request. Consists of a {@link Course} and a
 * {@link User}.
 */
public class AuthenticatedRequest<T> {
    @JsonProperty("data")
    private final T data;
    @JsonProperty("userName")
    private final String userName;

    /**
     * Creates a new {@link AuthenticatedRequest} with the given course and user.
     *
     * @param data     The course.
     * @param userName The username.
     */
    public AuthenticatedRequest(@JsonProperty("data") T data, @JsonProperty("userName") String userName) {
        this.data = data;
        this.userName = userName;
    }

    public T getData() {
        return data;
    }

    public String getUserName() {
        return userName;
    }

}
