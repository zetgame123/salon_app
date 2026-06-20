package ru.ns.model;

import java.time.LocalDate;

public class User {

    private int idUser;
    private int idRole;
    private String login;
    private String phone;
    private LocalDate registrationDate;
    private String email;
    private String passwordHash;

    public User() {
    }

    public User(int idUser, int idRole, String login,
                String phone, LocalDate registrationDate,
                String email, String passwordHash) {
        this.idUser = idUser;
        this.idRole = idRole;
        this.login = login;
        this.phone = phone;
        this.registrationDate = registrationDate;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}