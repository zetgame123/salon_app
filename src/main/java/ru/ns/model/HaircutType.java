package ru.ns.model;

public class HaircutType {

    private int idHaircutType;
    private String name;
    private String gender;

    public HaircutType() {
    }

    public HaircutType(int idHaircutType,
                       String name,
                       String gender) {
        this.idHaircutType = idHaircutType;
        this.name = name;
        this.gender = gender;
    }

    public int getIdHaircutType() {
        return idHaircutType;
    }

    public void setIdHaircutType(int idHaircutType) {
        this.idHaircutType = idHaircutType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}