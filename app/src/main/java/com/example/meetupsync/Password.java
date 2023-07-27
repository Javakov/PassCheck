package com.example.meetupsync;

public class Password {

    private int id;
    private String service;
    private String login;
    private String password;

    public Password(String service, String login, String password) {
        this.service = service;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    public String getLogin(){
        return login;
    }
    public void setLogin(String login){
        this.login = login;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}

