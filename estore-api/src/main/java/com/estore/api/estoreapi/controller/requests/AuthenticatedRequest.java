package com.estore.api.estoreapi.controller.requests;

import com.estore.api.estoreapi.models.Course;
import com.estore.api.estoreapi.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An authenticated course request. Consists of a {@link Course} and a
 * {@link User}.
 */
public class AuthenticatedRequest<T> {
    @JsonProperty("data")
    private final T data;
    @JsonProperty("userId")
    private final String userId;

    /**
     * Creates a new {@link AuthenticatedRequest} with the given course and user.
     *
     * @param data     The course.
     * @param userName The userid.
     */
    public AuthenticatedRequest(@JsonProperty("data") T data, @JsonProperty("userId") String userId) {
        this.data = data;
        this.userId = userId;
    }

    public T getData() {
        return data;
    }

    public String getUserId() {
        return userId;
    }

}
