package com.example.fyp;

public class Users {

    public String usernameTxt,emailTxt;

    public String getUsernameTxt() {
        return usernameTxt;
    }

    public void setUsernameTxt(String usernameTxt) {
        this.usernameTxt = usernameTxt;
    }

    public String getEmailTxt() {
        return emailTxt;
    }

    public void setEmailTxt(String emailTxt) {
        this.emailTxt = emailTxt;
    }

    public Users(){}


    public Users(String usernameTxt, String emailTxt){
        this.usernameTxt =usernameTxt;
        this.emailTxt=emailTxt;
    }
}
