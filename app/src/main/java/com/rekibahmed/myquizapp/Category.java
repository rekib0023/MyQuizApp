package com.rekibahmed.myquizapp;

public class Category {
    public static final int PROGRAMMING = 1;
    public static final int MATHS = 2;
    public static final int PHYSICS = 3;

    private int id;
    private String name;

    public Category(){}

    public Category(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
