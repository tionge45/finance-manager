package com.finance.auth;

public class YandexUser {
    private final String id;
    private final String login;
    private final String email;

    public YandexUser(String id, String login, String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }
}
