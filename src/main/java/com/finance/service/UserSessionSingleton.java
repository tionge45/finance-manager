package com.finance.service;

import com.finance.model.User;

public class UserSessionSingleton {


    private static User loggedInUser;

    private String currentScreen;

    private UserSessionSingleton(){}
    private static UserSessionSingleton userSessionSingleton = null;

    public static void setLoggedInUser(User user){
        loggedInUser = user;
    }

    public static User getLoggedInUser(){
        return loggedInUser;
    }

    public static void clear(){
        loggedInUser = null;
    }

    public String getCurrentScreen(){
        return currentScreen;
    }

    public void setCurrentScreen(String currentScreen){
        this.currentScreen = currentScreen;
    }

    public static UserSessionSingleton getInstance(){
        if (userSessionSingleton == null) {
            userSessionSingleton = new UserSessionSingleton();
        }
        return userSessionSingleton;

    }

}


