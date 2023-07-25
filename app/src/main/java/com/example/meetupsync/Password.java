package com.example.meetupsync;

public class Password {

    private int id;
    private String service;
    private String login;
    private String password;

    public Password(int id, String service, String login, String password) {
        this.id = id;
        this.service = service;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getService() {
        return service;
    }
    public String getLogin(){
        return login;
    }

    public String getPassword() {
        return password;
    }
}

