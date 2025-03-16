package com.finance.database;

import com.finance.model.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
;
import static org.junit.jupiter.api.Assertions.*;
class UserDAOTest {

    @Test
    void addUser() throws SQLException {

        int randomID = 55;
        int randomID2 = 45;
        String userID = String.valueOf(randomID);
        String userID2 = String.valueOf(randomID2);
        String userName = "Test User";
        String userEmail = "tio@gmail.com";
        String userPassword = "testPassword";

        UserDAO userDAO = new UserDAO(Connection );

        userDAO.addUser(userID, userName, userEmail, userPassword);

        userDAO.addUser(userID2, userName, userEmail, userPassword);

        User user = UserDAO.getUserByUserID(userID);

        assertNotNull(user);
        assertEquals(userName, user.getUserName());

    }

    @Test
    void getUserByLogin() {
    }

    @Test
    void authenticateUser() {
    }

    @Test
    void updateUserPassword() {
    }

    @Test
    void updateUserName() {
    }

    @Test
    void deleteUser() {
    }
}