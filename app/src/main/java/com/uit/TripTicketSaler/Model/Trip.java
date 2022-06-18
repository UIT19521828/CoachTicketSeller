package com.uit.TripTicketSaler.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Trip implements Serializable {
    private int available;
    private String tripID;
    private String coach;
    private Timestamp departure_time;
    private String start;
    private String finish;
    private ArrayList<Boolean> seat1;
    private ArrayList<Boolean> seat2;
    private String tripName;

    public Trip(){};

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public Timestamp getDeparture_time() {
        return departure_time;
    }

    public String GetDepartureDate(){
        Date d = departure_time.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        String pickUp =
                  calendar.get(Calendar.HOUR_OF_DAY) + "giờ, ngày "
                + calendar.get(Calendar.DAY_OF_MONTH) + "/"
                +(calendar.get(Calendar.MONTH)+1) + "/"
                + calendar.get(Calendar.YEAR);
        return pickUp;
    }

    public void setDeparture_time(Timestamp departure_time) {
        this.departure_time = departure_time;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public ArrayList<Boolean> getSeat1() {
        return seat1;
    }

    public void setSeat1(ArrayList<Boolean> seat1) {
        this.seat1 = seat1;
    }

    public ArrayList<Boolean> getSeat2() {
        return seat2;
    }

    public void setSeat2(ArrayList<Boolean> seat2) {
        this.seat2 = seat2;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }
}
