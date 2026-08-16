package com.majordna.model;

public abstract class User {
    private String name;
    private int age;

    protected User(String name, int age) {
        setName(name);
        setAge(age);
    }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        this.name = name.trim();
    }

    public int getAge() { return age; }
    public void setAge(int age) {
        if (age < 12 || age > 100) throw new IllegalArgumentException("Age must be between 12 and 100");
        this.age = age;
    }

    public abstract String getUserType();
}
