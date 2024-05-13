package org.fubar.passholder.dto;

public class Password {
    private int id;
    private String service;
    private String login;
    private String password;
    private String comment;
    private String label;

    public Password(String service, String login, String password, String comment, String label) {
        this.service = service;
        this.login = login;
        this.password = password;
        this.comment = comment;
        this.label = label;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

