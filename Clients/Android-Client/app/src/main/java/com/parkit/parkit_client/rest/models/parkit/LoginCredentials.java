package com.parkit.parkit_client.rest.models.parkit;

/**
 * Created by vikram on 29/11/15.
 */
public class LoginCredentials {

    public String email, password;

    public LoginCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return "Email ID : "+this.email+"\nPassword : "+this.password;
    }


}
