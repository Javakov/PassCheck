package com.example.meetupsync;

public class Password {

    private int id;
    private String service;
    private String password;

    public Password(int id, String service, String password) {
        this.id = id;
        this.service = service;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getPassword() {
        return password;
    }
}

