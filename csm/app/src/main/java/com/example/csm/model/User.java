package com.example.csm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    public enum Role {
        @JsonProperty("SUBSCRIBER")
        SUBSCRIBER,
        @JsonProperty("ADMIN")
        ADMIN
    }

    @JsonProperty("id")
    int id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("role")
    private Role role;

    public User() {}

    public User(int id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
