package com.uit.TripTicketSaler.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.Interface.IAdapterListener;
import com.uit.TripTicketSaler.Model.Ticket;
import com.uit.TripTicketSaler.Model.Trip;
import com.uit.TripTicketSaler.R;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final ArrayList<Ticket> lTicket;
    private final IAdapterListener ticketListener;

    public TicketAdapter(ArrayList<Ticket> lTicket, IAdapterListener ticketListener){
        this.lTicket = lTicket;
        this.ticketListener = ticketListener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ticket, parent, false);
        return new TicketViewHolder(view, ticketListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = lTicket.get(position);
        if(ticket==null) return;
        db.collection("Trips").document(ticket.getTrip())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Trip trip = task.getResult().toObject(Trip.class);
                        holder.plate.setText(trip.getCoach());
                        holder.timeDepar.setText(trip.GetDepartureDate());
                        holder.tripName.setText(trip.getTripName());
                    }
                });
        holder.totalCost.setText(ticket.getTotalCost() + "");
        holder.status.setText(ticket.getStatus());
        holder.timePur.setText(ticket.GetPurTime());
    }

    @Override
    public int getItemCount() {
        if(lTicket!=null){
            return lTicket.size();
        }
        return 0;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView tripName, timeDepar, timePur, totalCost, status, plate;
        public final IAdapterListener ticketListener;

        public TicketViewHolder(@NonNull View itemView, IAdapterListener ticketListener) {
            super(itemView);
            tripName = itemView.findViewById(R.id.tvTripName);
            timeDepar = itemView.findViewById(R.id.tvDepartureTime);
            timePur = itemView.findViewById(R.id.tvPurchaseDate);
            totalCost = itemView.findViewById(R.id.tvTotalCost);
            status = itemView.findViewById(R.id.tvStatus);
            plate = itemView.findViewById(R.id.tvPlate);
            this.ticketListener = ticketListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ticketListener.onClickTicket(getBindingAdapterPosition());
        }
    }
}
