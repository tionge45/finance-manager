package com.finance.model;

import java.util.Objects;
import org.mindrot.jbcrypt.BCrypt;

public class User {

    private int userID;
    private String userName;
    private String userEmail;
    private String userPassword;


    public User(int userID, String userName, String userEmail, String userPassword){

        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;

    }

    public User(){};

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {return userPassword;}

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;

        User user = (User) obj;

        return (Objects.equals(userEmail, user.userEmail) || Objects.equals(userID, user.userID));
    }

    @Override
    public String toString(){

        return "Username: " + this.getUserName() + " Email: "
                + this.getUserEmail() + " ID: " + this.getUserID();
    }

    public void setPassword(String userPassword) {
        this.userPassword = PasswordUtils.hashPassword(userPassword);
    }

    public boolean verifyPassword(String inputPassword) {
        return PasswordUtils.checkPassword(inputPassword, this.userPassword );
    }
}
