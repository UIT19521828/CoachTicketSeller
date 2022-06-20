package com.uit.TripTicketSaler.Model;

import java.util.Map;

public class AppUser {
    private String username;
    private String cmnd;
    private String phoneNum;
    private String dob;
    private String address;
    private int balance = 0;
    private Map<String, Integer> transition_history;

    public AppUser() {
    }

    public void ChangeDataUser(String username, String cmnd, String phoneNum, String dob, String address) {
        this.username = username;
        this.cmnd = cmnd;
        this.phoneNum = phoneNum;
        this.dob = dob;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Integer> getTransition_history() {
        return transition_history;
    }

    public void setTransition_history(Map<String, Integer> transition_history) {
        this.transition_history = transition_history;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    public void AddToWallet(int add){
        balance += add;
    }
    public void AddToTransition(String key, int value){
        transition_history.put(key, value);
    }
}
