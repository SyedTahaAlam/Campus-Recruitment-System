package com.example.syedtahaalam.campusrecruitementsystems;

public class Candidate {

    private String Name;
    private String Email;
    private String Phone;
    private String JobTitle;

    public Candidate(){

    }

    public Candidate(String name, String email, String phone, String jobTitle){

        Name = name;
        Email = email;
        Phone = phone;
        JobTitle = jobTitle;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setJobTitle(String jobTitle) {
        JobTitle = jobTitle;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public String getJobTitle() {
        return JobTitle;
    }
}
