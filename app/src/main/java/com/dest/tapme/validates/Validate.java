package com.dest.tapme.validates;

public class Validate {

    public static String signUpOrSignIn(String username, String email, String password, String retryPassword) {

        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

        if (username != null || retryPassword != null) {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || retryPassword.isEmpty()) {
                return "Input can not blank";
            } else if (username.length() < 6) {
                return "Username or name must be more than 6 characters";
            } else if (!email.matches(regex)) {
                return "Incorrect email format";
            } else if (password.length() < 8) {
                return "Password length must be more than 8 characters";
            } else if (!password.equals(retryPassword)) {
                return "The password entered is not the same";
            }
        } else {
            if (email.isEmpty() || password.isEmpty()) {
                return "Input can not blank";
            } else if (!email.matches(regex)) {
                return "Incorrect email format";
            } else if (password.length() < 8) {
                return "Password length must be more than 8 characters";
            }
        }
        return null;
    }

    public static String pointToScore(Integer myPoint, Integer point, String pointToString) {

        if (myPoint < point) {
            return "The points you enter are not enough to exchange them for scores.";
        } else if (pointToString.equals("0")) {
            return "The points you enter cannot be less than 1.";
        }

        return null;

    }

    public static String market(Integer price, Integer balance, String s) {

        if (balance < price) {
            return "your " + s + " is not enough to buy this item";
        }

        return null;

    }
}
