package com.uit.TripTicketSaler.Model;

public class City implements Comparable<City> {
    private int distance;
    private String cname;

    public City(){}

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public int compareTo(City o) {
        int compare = o.getDistance();
        return this.distance - compare;
    }
}
