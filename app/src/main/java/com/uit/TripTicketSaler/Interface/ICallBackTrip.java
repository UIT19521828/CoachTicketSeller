package com.uit.TripTicketSaler.Interface;

import com.uit.TripTicketSaler.Model.Trip;

import java.util.ArrayList;

public interface ICallBackTrip {
    void onCallbackTrip(ArrayList<Trip> tripList);
}
