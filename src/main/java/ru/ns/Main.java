package ru.ns;

import database.DatabaseManager;

public class Main {

    public static void main(String[] args) {

        DatabaseManager db =
                DatabaseManager.getInstance();

        System.out.println(
                db.getConnection()
        );
    }
}