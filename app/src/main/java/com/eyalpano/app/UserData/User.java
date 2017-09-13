package com.eyalpano.app.UserData;

public class User {

    public int idUsers;
    public String telephone ,pass,fullname,waze,comments;

    public User(int idUsers, String telephone, String pass, String fullname,String waze,String comments) {
       this.idUsers = idUsers;
        this.telephone = telephone;
        this.pass = pass;
        this.fullname = fullname;
        this.waze = waze;
        this.comments = comments;
    }

    public User(String telephone, String pass, String fullname) {
        this(0, telephone,  pass,  fullname,null,null);
    }

}
