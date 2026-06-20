package ru.ns.security;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

    private static final Pattern pattern =
            Pattern.compile(PASSWORD_REGEX);

    public static boolean isValid(String password) {
        return pattern.matcher(password)
                .matches();
    }
}