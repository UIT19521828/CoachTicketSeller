package com.uit.TripTicketSaler.Interface;

import com.uit.TripTicketSaler.Model.Ticket;

import java.util.ArrayList;

public interface ICallBackTicket {
    void onCallbackTrip(ArrayList<Ticket> tripList);
}
