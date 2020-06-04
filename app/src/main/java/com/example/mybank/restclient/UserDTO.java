package com.example.mybank.restclient;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDTO implements Serializable {
    private String id;
    private String email;
    private String name;
    private boolean blacklist;
    private ArrayList<AccountDTO> accountsList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void setBlacklist(boolean blacklist) {
        this.blacklist = blacklist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<AccountDTO> getAccountsList() {
        return accountsList;
    }

    public void setAccountsList(ArrayList<AccountDTO> accountsList) {
        this.accountsList = accountsList;
    }
}
