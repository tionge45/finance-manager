package com.finance.model;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Setter
public class User {

    private String userName;
    private String userEmail;
    private String userPassword;

    public User(String userName, String userEmail, String userPassword){
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public User(){};

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return (Objects.equals(userEmail, user.userEmail));
    }

    @Override
    public String toString(){
        return "Username: " + this.getUserName() + " Email: "
                + this.getUserEmail() ;
    }

    public void setPassword(String userPassword) {
        this.userPassword = PasswordUtils.hashPassword(userPassword);
    }

    public boolean verifyPassword(String inputPassword) {
        return PasswordUtils.checkPassword(inputPassword, this.userPassword );
    }

    public String getUserEmail(){
        return userEmail;
    }

    public String getUserName(){
        return userName;
    }

}
