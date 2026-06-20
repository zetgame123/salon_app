package ru.ns.model;

public class Branch {

    private int idBranch;
    private String name;
    private String address;

    public Branch() {
    }

    public Branch(int idBranch, String name, String address) {
        this.idBranch = idBranch;
        this.name = name;
        this.address = address;
    }

    public int getIdBranch() {
        return idBranch;
    }

    public void setIdBranch(int idBranch) {
        this.idBranch = idBranch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}