package com.uit.TripTicketSaler.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.Interface.IAdapterListener;
import com.uit.TripTicketSaler.Model.Coach;
import com.uit.TripTicketSaler.Model.Trip;
import com.uit.TripTicketSaler.R;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.CarViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final ArrayList<Trip> lTrip;
    private final ArrayList<String> ltripID;
    private final int start;
    private final int end;
    private final int p, c;
    private final IAdapterListener coachListener;

    public TripAdapter(ArrayList<Trip> lTrip, ArrayList<String> ltripID, int start, int end, int p, int c, IAdapterListener coachListener) {
        this.lTrip = lTrip;
        this.start = start;
        this.end = end;
        this.p = p;
        this.c = c;
        this.ltripID = ltripID;
        this.coachListener = coachListener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new CarViewHolder(view, coachListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Trip trip = lTrip.get(position);
        if(trip==null) return;
        int t = Math.abs(end-start);
        db.collection("TravelCars").document(trip.getCoach())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Coach tCar = task.getResult().toObject(Coach.class);
                        int money = tCar.getPrice() * t;
                        money *= (p + c / 2);
                        holder.duration.setText("Thời gian: "+ t*1.5* tCar.getSpeed() + " tiếng");
                        holder.totalCost.setText(money + " $");
                        holder.feature.setText(tCar.getDetail());
                    }
                    else{
                        Log.d("Loi Lay Data", task.getException().toString());
                    }
                }
        );

        holder.plate.setText("Xe: "+ trip.getCoach());
        holder.timePickUp.setText(trip.GetDepartureDate());
        holder.availSeat.setText("còn: " + trip.getAvailable() + "chỗ");
    }

    @Override
    public int getItemCount() {
        if(lTrip!=null){
            return lTrip.size();
        }
        return 0;
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView timePickUp;
        private final TextView plate;
        private final TextView feature;
        private final TextView totalCost;
        private final TextView duration;
        private final TextView availSeat;
        public final IAdapterListener coachListener;

        public CarViewHolder(@NonNull View itemView, IAdapterListener coachListener)  {
            super(itemView);
            timePickUp = itemView.findViewById(R.id.timePickUp);
            plate = itemView.findViewById(R.id.plate);
            feature = itemView.findViewById(R.id.feature);
            totalCost = itemView.findViewById(R.id.totalCost);
            duration = itemView.findViewById(R.id.duration);
            availSeat = itemView.findViewById(R.id.availableSeat);
            this.coachListener = coachListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            coachListener.onClickTicket(getBindingAdapterPosition());
        }
    }

}
