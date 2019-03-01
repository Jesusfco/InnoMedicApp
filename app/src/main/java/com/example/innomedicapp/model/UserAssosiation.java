package com.example.innomedicapp.model;

import org.json.JSONObject;

public class UserAssosiation {

    private Integer id, user_id, assosiated_id;
    private boolean confirmed;
    private User user, assosiated;

    public UserAssosiation() {
    }

    public UserAssosiation(String string) {

        try {

            JSONObject object = new JSONObject(string);
            this.id = object.getInt("id");
            this.user_id = object.getInt("user_id");
            this.assosiated_id = object.getInt("assosiated_id");

            if(object.getInt("confirmed") == 1) this.confirmed = true;

            if(!object.isNull("user")) this.user = new User(object.getString("user"));

            if(!object.isNull("assosiated")) this.assosiated = new User(object.getString("assosiated"));

        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getAssosiated_id() {
        return assosiated_id;
    }

    public void setAssosiated_id(Integer assosiated_id) {
        this.assosiated_id = assosiated_id;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAssosiated() {
        return assosiated;
    }

    public void setAssosiated(User assosiated) {
        this.assosiated = assosiated;
    }
}
