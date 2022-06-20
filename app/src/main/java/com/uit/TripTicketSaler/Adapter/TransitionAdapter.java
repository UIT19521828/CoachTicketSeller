package com.uit.TripTicketSaler.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.uit.TripTicketSaler.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TransitionAdapter extends RecyclerView.Adapter<TransitionAdapter.TransitionViewHolder>{
    private final Map<String, Integer> lTrans;
    private final ArrayList<String> lKeys;
    
    public TransitionAdapter(Map<String, Integer> lTrans){
        this.lTrans = lTrans;
        ArrayList<String> list = new ArrayList<>(lTrans.keySet());
        Collections.sort(list, Collections.reverseOrder());
        lKeys = list;
    }
    
    @NonNull
    @Override
    public TransitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transition_history, parent, false);
        return new TransitionAdapter.TransitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransitionViewHolder holder, int position) {
        String key = lKeys.get(position);
        int value = lTrans.get(key);
        long convert = Long.parseLong(key);
        Date d = new Date(convert);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        String pickUp = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE)
                + ":" + calendar.get(Calendar.SECOND) + " ngày "
                + calendar.get(Calendar.DAY_OF_MONTH) + "/"
                +(calendar.get(Calendar.MONTH)+1) + "/"
                + calendar.get(Calendar.YEAR);
        holder.tvTime.setText(pickUp);
        if(value > 0){
            holder.tvPlus.setText(value + "");
        }
        else{
            holder.cardView.setStrokeColor(Color.parseColor("#FF6347"));
            holder.imgPlus.setVisibility(View.GONE);
            holder.imgMinus.setVisibility(View.VISIBLE);
            holder.layoutPlus.setVisibility(View.GONE);
            holder.layoutMinus.setVisibility(View.VISIBLE);
            value = -value;
            holder.tvMinus.setText(value + "");
        }
    }

    @Override
    public int getItemCount() {
        return lTrans.size();
    }

    public static class TransitionViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imgPlus;
        private final ImageView imgMinus;
        private final LinearLayout layoutPlus, layoutMinus;
        private final TextView tvPlus, tvMinus, tvTime;
        private final MaterialCardView cardView;
        public TransitionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imgMinus = itemView.findViewById(R.id.imgWithdraw);
            imgPlus = itemView.findViewById(R.id.imgPlus);
            layoutMinus = itemView.findViewById(R.id.layoutMinus);
            layoutPlus = itemView.findViewById(R.id.layoutPLus);
            tvMinus = itemView.findViewById(R.id.tvMinus);
            tvPlus = itemView.findViewById(R.id.tvPlus);
            tvTime = itemView.findViewById(R.id.tvTimeTrans);
        }
    }

}
