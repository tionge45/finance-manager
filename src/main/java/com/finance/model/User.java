package com.finance.model;


import java.util.Objects;

public class User {

    private final int _id;
    private final String _username;
    private final String _email;
    private final String _hashedPasswords;

    public User(int id, String username, String email,
                String hashedPasswords){
        this._id = id;
        this._username = username;
        this._email = email;
        _hashedPasswords = hashedPasswords;
    }

    public int get_id() {
        return _id;
    }

    public String get_username() {
        return _username;
    }

    public String get_email() {
        return _email;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;

        User user = (User) obj;

        return (Objects.equals(_email, user._email) || Objects.equals(_id, user._id));
    }

    @Override
    public String toString(){

        return "Username: " + this._username + " Email: "
                + this._email + " ID: " + this._id;
    }
}
