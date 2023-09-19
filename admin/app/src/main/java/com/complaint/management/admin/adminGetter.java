package com.complaint.management.admin;

public class adminGetter {
    String Username;
    String Email;
    String Key;
    String Mobile;
    String Token;
    String Role;
    String State;
    String Status;

    public String getState() {
        return State;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getToken() {
        return Token;
    }

    public String getRole() {
        return Role;
    }

    public String getStatus() {
        return Status;
    }
}
