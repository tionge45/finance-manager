package com.finance;

import com.finance.auth.YandexSignIn;

import java.util.Scanner;

public class YandexLoginMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type '1' to start login:");
        String input = scanner.nextLine().trim();

        if ("1".equalsIgnoreCase(input)) {
            YandexSignIn yandexSignIn = new YandexSignIn();
            try {
                yandexSignIn.loginWithYandex();
            } catch (Exception e) {
                System.err.println("Login failed: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Unrecognized command. Exiting.");
        }

        scanner.close();
    }
}
