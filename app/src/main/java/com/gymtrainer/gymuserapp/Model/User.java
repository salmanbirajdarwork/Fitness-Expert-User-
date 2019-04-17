package com.gymtrainer.gymuserapp.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by haroonpc on 3/14/2019.
 */

public class User implements Serializable
{
    public String name,email,phonenumber,address,city,gender,age,imageUrl,userid;

    public User(String name, String email, String phonenumber, String address, String city, String gender, String age,String imageUrl,String userid) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.address = address;
        this.city = city;
        this.gender = gender;
        this.age = age;
        this.imageUrl = imageUrl;
        this.userid = userid;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
