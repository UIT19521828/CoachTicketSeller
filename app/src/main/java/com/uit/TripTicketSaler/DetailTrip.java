package com.uit.TripTicketSaler;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.Model.Coach;
import com.uit.TripTicketSaler.Model.Trip;

import java.util.ArrayList;

public class DetailTrip extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavController navController;

    private LinearLayout linearSeat;
    private ViewGroup groupSeats;
    private ImageView btnBack;
    private TextView tvStart, tvEnd, tvTime, timePickUp, plate, feature, totalCost, duration, tvGhiChu;
    private View cardDetail;
    private static int numCus, numChild, totalN, start, end, money;
    private static String desStart, desEnd;
    public static String tripID;
    private Trip trip;
    private ArrayList<Boolean> seat1 = new ArrayList<>();
    private ArrayList<Boolean> seat2 = new ArrayList<>();
    private ArrayList<String> arrSelected = new ArrayList<>();
    private int numSelected = 0;
    private String dataSeats1 = "/", dataSeats2 = "/";
    int STATUS_AVAILABLE = 1;
    int STATUS_BOOKED = 2;

    private ArrayList<TextView> seatTexViewList = new ArrayList<>();
    private int seatSize = 150;
    private int seatGaping = 10;

    public DetailTrip() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detail_trip, container, false);
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();

        groupSeats = v.findViewById(R.id.groupSeats);
        btnBack = v.findViewById(R.id.backPress2);
        cardDetail = v.findViewById(R.id.carDetail);
        timePickUp = cardDetail.findViewById(R.id.timePickUp);
        plate = cardDetail.findViewById(R.id.plate);
        feature = cardDetail.findViewById(R.id.feature);
        totalCost = cardDetail.findViewById(R.id.totalCost);
        duration = cardDetail.findViewById(R.id.duration);
        tvStart = v.findViewById(R.id.txtStart);
        tvEnd = v.findViewById(R.id.txtEnd);
        tvGhiChu = v.findViewById(R.id.tvDeparture);
        Button btnNext = v.findViewById(R.id.btnGoNext);

        linearSeat = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearSeat.setOrientation(LinearLayout.VERTICAL);
        linearSeat.setLayoutParams(params);
        linearSeat.setPadding(8 * seatGaping, 8 * seatGaping, 8 * seatGaping, 8 * seatGaping);

        if(getArguments()!=null){
            desStart = getArguments().getString("pickUp");
            desEnd = getArguments().getString("dest");
            start = getArguments().getInt("start");
            end = getArguments().getInt("end");
            numCus = getArguments().getInt("adult");
            numChild = getArguments().getInt("child");
            tripID = getArguments().getString("tripID");
            totalN = numChild + numCus;
        }
        db.collection("Trips").document(tripID).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        trip = task.getResult().toObject(Trip.class);
                        seat1 = trip.getSeat1();
                        seat2 = trip.getSeat2();
                        LoadSeatNum();
                        LoadCardView(trip, start, end, numCus, numChild);
                        Load3RowSeat(dataSeats1, true);
                        if(dataSeats2!="/"){
                            TextView tv = new TextView(getActivity());
                            tv.setText("TẦNG 2");
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
                            tv.setTextColor(Color.BLACK);
                            tv.setGravity(Gravity.CENTER);
                            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, seatSize);
                            layoutParams.setMargins(150, seatGaping, seatGaping, seatGaping);
                            tv.setLayoutParams(layoutParams);
                            linearSeat.addView(tv);
                            Load3RowSeat(dataSeats2, false);
                        }
                    }
                });

        tvStart.setText(desStart);
        tvEnd.setText(desEnd);

        groupSeats.addView(linearSeat);
        btnBack.setOnClickListener(view -> {
            BackPressClick();
        });

        btnNext.setOnClickListener(view -> {
            PassTicketData();
        });

        return v;
    }

    private void PassTicketData(){
        if(numSelected != totalN){
            Toast.makeText(getActivity(), "Bạn chưa chọn hết chỗ ngồi", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("money", money);
        bundle.putSerializable("trip", trip);
        bundle.putStringArrayList("seatNumber", arrSelected);
        bundle.putString("detail", tvGhiChu.getText().toString());
        bundle.putInt("numAdult", numCus);
        bundle.putInt("numChild", numChild);
        bundle.putSerializable("seat1", seat1);
        bundle.putSerializable("seat2", seat2);
        navController.navigate(R.id.action_detailTrip_to_serviceFragment, bundle);
    }

    private void SetTextView(TextView tv, int count, boolean f1){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, seatSize);
        layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
        tv.setLayoutParams(layoutParams);
        tv.setPadding(0, 0, 0, 2 * seatGaping);
        tv.setId(count);
        tv.setGravity(Gravity.CENTER);
        tv.setText(ReturnSeatNum(count, f1));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
    }

    private void Load3RowSeat(String arrSeat, boolean f1){
        LinearLayout layout = null;

        int count = 0;

        for (int index = 0; index < arrSeat.length(); index++) {
            if (arrSeat.charAt(index) == '/') {
                layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setGravity(Gravity.CENTER);
                linearSeat.addView(layout);
            } else if (arrSeat.charAt(index) == 'U') {
                TextView tv = new TextView(getActivity());
                SetTextView(tv, count, f1);
                tv.setTag(STATUS_BOOKED);
                tv.setBackgroundResource(R.drawable.booked_img);
                tv.setTextColor(Color.WHITE);
                layout.addView(tv);
                seatTexViewList.add(tv);
                if(f1) tv.setOnClickListener(this::SelectSeat1);
                else tv.setOnClickListener(this::SelectSeat2);
                count++;
            } else if (arrSeat.charAt(index) == 'A') {
                TextView tv = new TextView(getActivity());
                SetTextView(tv, count, f1);
                tv.setBackgroundResource(R.drawable.available_img);
                tv.setTextColor(Color.BLACK);
                tv.setTag(STATUS_AVAILABLE);
                layout.addView(tv);
                seatTexViewList.add(tv);
                if(f1) tv.setOnClickListener(this::SelectSeat1);
                else tv.setOnClickListener(this::SelectSeat2);
                count++;
            }else if (arrSeat.charAt(index) == '_') {
                TextView tv = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                tv.setLayoutParams(layoutParams);
                tv.setBackgroundColor(Color.TRANSPARENT);
                tv.setText("");
                layout.addView(tv);
            }
        }
    }

    private StringBuilder ReturnSeatNum(int count, boolean f1){
        StringBuilder builder = new StringBuilder("");
        if(count%3==0) {
            if(f1) builder.append("A");
            else builder.append("D");
        }
        else if(count%3==1){
            if(f1) builder.append("B");
            else builder.append("E");
        }
        else {
            if(f1) builder.append("C");
            else builder.append("F");
        }
        int numS = (count / 3) + 1;
        builder.append(numS);
        return builder;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BackPressClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void LoadSeatNum(){
        for(int i = 0; i < seat1.size(); i++){
            if(seat1.get(i)) dataSeats1 += "U";
            else dataSeats1 += "A";
            if(i%3!=2) dataSeats1 += "_";
            else dataSeats1 += "/";
        }

        if(seat2.size() > 0){
            for(int i = 0; i < seat2.size(); i++){
                if(seat2.get(i)) dataSeats2 += "U";
                else dataSeats2 += "A";
                if(i%3!=2) dataSeats2 += "_";
                else dataSeats2 += "/";
            }
        }
    }

    private void LoadCardView(Trip trip, int s, int e, int p, int c){
        int t = Math.abs(e-s);

        db.collection("TravelCars").document(trip.getCoach())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Coach tCar = task.getResult().toObject(Coach.class);
                        duration.setText(t*1.5*tCar.getSpeed() + " tiếng");
                        money = tCar.getPrice() * t;
                        money *= (p + c / 2);
                        totalCost.setText(Integer.toString(money));
                        feature.setText(tCar.getDetail());
                        plate.setText(tCar.getPlate());
                    }
                    else{
                        Log.d("Loi Lay Data", task.getException().toString());
                    }
                });
        timePickUp.setText(trip.GetDepartureDate());
    }

    private void SelectSeat1(View view){
        TextView tv = (TextView) view;
        if ((int) view.getTag() == STATUS_AVAILABLE) {
            if (seat1.get(view.getId())) {
                seat1.set(view.getId(), false);
                numSelected--; arrSelected.remove(tv.getText().toString());
                view.setBackgroundResource(R.drawable.available_img);
            } else {
                if(numSelected >=totalN){
                    Toast.makeText(getActivity(), "Bạn đã chọn quá số chỗ!", Toast.LENGTH_SHORT).show();
                }
                else{
                    seat1.set(view.getId(), true);
                    numSelected++; arrSelected.add(tv.getText().toString());
                    view.setBackgroundResource(R.drawable.your_seat_img);
                }
            }
        } else {
            Toast.makeText(getActivity(), "Chỗ ngồi đã được đặt", Toast.LENGTH_SHORT).show();
        }
    }

    private void SelectSeat2(View view){
        TextView tv = (TextView) view;
        if ((int) view.getTag() == STATUS_AVAILABLE) {
            if (seat2.get(view.getId())) {
                seat2.set(view.getId(), false);
                numSelected--; arrSelected.remove(tv.getText().toString());
                view.setBackgroundResource(R.drawable.available_img);
            } else {
                if(numSelected >=totalN){
                    Toast.makeText(getActivity(), "Bạn đã chọn quá số chỗ!", Toast.LENGTH_SHORT).show();
                }
                else{
                    seat2.set(view.getId(), true);
                    numSelected++;
                    arrSelected.add(tv.getText().toString());
                    view.setBackgroundResource(R.drawable.your_seat_img);
                }
            }
        } else {
            Toast.makeText(getActivity(), "Chỗ ngồi đã được đặt", Toast.LENGTH_SHORT).show();
        }
    }

    private void BackPressClick(){
        navController.navigate(R.id.action_detailTrip_to_listTrip);
    }
}