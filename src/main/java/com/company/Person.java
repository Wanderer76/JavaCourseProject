package com.company;

import java.util.Date;

public class Person{

    private final String name;
    private final String surname;
    private String city;
    private String birthdate;
    private String photo;
    private int vkId;

    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.city = "None";
        this.birthdate = "None";
        this.photo = "None";
        this.vkId = -1000;
    }

    @Override
    public String toString() {
        return vkId + ": Имя - " + name + ", Фамилия - " + surname + ", Дата рождения - " + birthdate + ", Город - " +city + ", Photo - "+ photo;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getCity() {
        return city;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public int getVkId() {
        return vkId;
    }

    public void setVkId(int vkId) {
        this.vkId = vkId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
