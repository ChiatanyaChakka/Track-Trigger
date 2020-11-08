package com.example.loginpage;

public class UserInfo {
    private String username,phone,mail,profession;

    public UserInfo(){
    }

    public UserInfo(String mail, String phone,String username,String profession){
        this.mail = mail;
        this.phone = phone;
        this.username = username;
        this.profession = profession;
    }

    public String getProfession(){
        return profession;
    }

    public void setProfession(String profession){
        this.profession = profession;
    }

    public String getMail(){
        return mail;
    }

    public void setMail(String mail) {
        this.mail=mail;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public String getUsername(){
        return username;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }

    public String getPhone(){
        return phone;
    }
}
