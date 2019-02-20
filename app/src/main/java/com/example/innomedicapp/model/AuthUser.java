package com.example.innomedicapp.model;

import android.content.SharedPreferences;

public class AuthUser {

    private Integer id, user_type;
    private String name, email, password, phone, img, token;
    private boolean status;

    public AuthUser() {
    }

    public AuthUser(SharedPreferences pref) {
        this.id = pref.getInt("id", 0);
        this.user_type = pref.getInt("user_type", 0);
        this.name = pref.getString("name", "");
        this.email = pref.getString("email", "");
        this.phone = pref.getString("phone", "");
        this.img = pref.getString("img", "");
        this.token = pref.getString("token", "");
        this.status = true;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String userTypeName(){
        if(this.user_type == 1)
            return "Infante";
        else if(this.user_type == 2)
            return "Tutor";
        else if(this.user_type == 3)
            return "Externo";
        else
            return "Administrador";

    }
}
