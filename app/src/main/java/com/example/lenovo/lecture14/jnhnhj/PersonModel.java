package com.example.lenovo.lecture14.jnhnhj;

/**
 * Created by Lenovo on 2/13/2020.
 */

public class PersonModel {
    private String Name , Email , Password , Phone , Gender , ImageURL;

    public PersonModel(String name, String email, String password, String phone, String gender, String imageURL) {
        Name = name;
        Email = email;
        Password = password;
        Phone = phone;
        Gender = gender;
        ImageURL = imageURL;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
