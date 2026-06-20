package ru.ns.model;

public class Client {

    private int idClient;
    private String surname;
    private String name;
    private String patronymic;
    private String phone;
    private boolean regularClient;

    public Client() {
    }

    public Client(int idClient, String surname,
                  String name, String patronymic,
                  String phone, boolean regularClient) {
        this.idClient = idClient;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.phone = phone;
        this.regularClient = regularClient;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isRegularClient() {
        return regularClient;
    }

    public void setRegularClient(boolean regularClient) {
        this.regularClient = regularClient;
    }
}