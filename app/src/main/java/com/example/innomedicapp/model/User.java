package com.example.innomedicapp.model;

import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private Integer id, user_type;
    private String name, email, password, phone, img;
    private boolean status;

    public User() {
    }

    public User(String string)  {
        try {
            JSONObject object = new JSONObject(string);
            this.id = object.getInt("id");
            this.user_type = object.getInt("user_type");
            this.name = object.getString("name");
            this.email = object.getString("email");
            this.phone = object.getString("phone");
            this.img = object.getString("img");

            if(object.getInt("status") == 1) this.status = true;
        }catch (Exception e) {
            System.out.println(e);
        }
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserTypeName(){
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
