package com.example.syedtahaalam.campusrecruitementsystems;

public class User {

    private String Name;
    private String Email;
    private String Phone;
    private String Type;

    public User(){

    }

    public User(String name, String email, String phone, String type){

        Name = name;
        Email= email;
        Phone = phone;
        Type = type;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getType() {
        return Type;
    }
}
