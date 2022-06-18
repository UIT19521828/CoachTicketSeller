package com.uit.TripTicketSaler.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Ticket implements Serializable {
    public String ticketID;
    private String trip;
    private String userID;
    private String detail;
    private int totalCost;
    private ArrayList<String> seatNumber;
    private HashMap<String, Boolean> service;
    private int numAdult;
    private int numChild;
    private String status;
    private String pMethod;
    private Timestamp purchaseDate;

    public Ticket(){}

    public Ticket(String trip, String userID, String detail, int totalCost, ArrayList<String> seatNumber, HashMap<String, Boolean> service, int numAdult, int numChild, String status, String pMethod, Timestamp pd) {
        this.trip = trip;
        this.userID = userID;
        this.totalCost = totalCost;
        this.detail = detail;
        this.seatNumber = seatNumber;
        this.service = service;
        this.numAdult = numAdult;
        this.numChild = numChild;
        this.status = status;
        this.pMethod = pMethod;
        this.purchaseDate = pd;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public ArrayList<String> getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(ArrayList<String> seatNumber) {
        this.seatNumber = seatNumber;
    }

    public HashMap<String, Boolean> getService() {
        return service;
    }

    public void setService(HashMap<String, Boolean> service) {
        this.service = service;
    }

    public int getNumAdult() {
        return numAdult;
    }

    public void setNumAdult(int numAdult) {
        this.numAdult = numAdult;
    }

    public int getNumChild() {
        return numChild;
    }

    public void setNumChild(int numChild) {
        this.numChild = numChild;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getpMethod() {
        return pMethod;
    }

    public void setpMethod(String pMethod) {
        this.pMethod = pMethod;
    }

    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String GetPurTime(){
        Date d = purchaseDate.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        String pickUp = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " ngày "
                        + calendar.get(Calendar.DAY_OF_MONTH) + "/"
                        +(calendar.get(Calendar.MONTH)+1) + "/"
                        + calendar.get(Calendar.YEAR);
        return pickUp;
    }
}
