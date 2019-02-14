package com.example.syedtahaalam.campusrecruitementsystems;

public class Job {

    private String Title;
    private String Email;
    private String Experience;
    private String Deadline;
    private String Salary;
    private String Location;
    private String Qualification;
    private String CompanyID;

    public Job(){

    }

public Job(String title,String email,String experience,String deadline,String salary, String location, String qualification, String companyID){

        Title = title;
        Email = email;
        Salary = salary;
        Location = location;
        Qualification = qualification;
        Experience = experience;
        Deadline = deadline;
        CompanyID = companyID;
}

    public void setTitle(String title) {
        Title = title;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setExperience(String experience) {
        Experience = experience;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public void setSalary(String salary) {
        Salary = salary;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setQualification(String qualification) {
        Qualification = qualification;
    }

    public String getTitle() {
        return Title;
    }

    public String getEmail() {
        return Email;
    }

    public String getExperience() {
        return Experience;
    }

    public String getDeadline() {
        return Deadline;
    }

    public String getSalary() {
        return Salary;
    }

    public String getLocation() {
        return Location;
    }

    public String getQualification() {
        return Qualification;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }
}
