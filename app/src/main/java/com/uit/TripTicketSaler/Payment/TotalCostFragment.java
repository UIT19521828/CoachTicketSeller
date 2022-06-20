package com.uit.TripTicketSaler.Payment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.AccountManager.ClientAuth;
import com.uit.TripTicketSaler.DetailTrip;
import com.uit.TripTicketSaler.Model.Coupon;
import com.uit.TripTicketSaler.Model.Ticket;
import com.uit.TripTicketSaler.Model.Trip;
import com.uit.TripTicketSaler.R;
import com.uit.TripTicketSaler.databinding.FragmentTotalCostBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TotalCostFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentTotalCostBinding binding;
    private NavController navController;

    private int travelCost = 0, suitCost = 0, mealCost = 0, insCost = 0, totalCost=0, finalCost = 0;
    private HashMap<String, Boolean> service = new HashMap<>();
    private String pMethod;

    public TotalCostFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTotalCostBinding.inflate(inflater, container, false);
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();

        travelCost += getArguments().getInt("money");

        boolean[] checkService = getArguments().getBooleanArray("service");
        if(checkService[0]) suitCost += 50;
        if(checkService[1]) mealCost += 15;
        if(checkService[2]) mealCost += 60;
        if(checkService[3]) insCost += 50;
        totalCost += travelCost * 110 / 100 + suitCost + mealCost + insCost;
        finalCost += totalCost;

        service.put("suitcase", checkService[0]);
        service.put("breakfast", checkService[1]);
        service.put("meal", checkService[2]);
        service.put("insurance", checkService[3]);

        binding.tvAll.setText(Integer.toString(finalCost));
        binding.tvRedAll.setText(Integer.toString(finalCost));
        binding.tvTicketPrice.setText(Integer.toString(travelCost));
        binding.tvInsurance.setText(Integer.toString(insCost));
        binding.tvMeal.setText(Integer.toString(mealCost));
        binding.tvPacked.setText(Integer.toString(mealCost));
        binding.tvTax.setText(Integer.toString(travelCost * 10 / 100 ));
        binding.totalTicketPrice.setText(Integer.toString(travelCost * 110 / 100));
        binding.tvServiceFee.setText(Integer.toString(suitCost + mealCost + insCost));

        binding.btnInputCode.setOnClickListener(view -> {
            InputVoucher(binding.editCode.getText().toString());
        });

        binding.btnPay.setOnClickListener(view -> {
            MakePurchase();
        });
        binding.backPress.setOnClickListener(view -> {

        });
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void InputVoucher(String coupon){
        db.collection("Vouchers").document(coupon)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Coupon vc = task.getResult().toObject(Coupon.class);
                        if(vc==null) {
                            Toast.makeText(getActivity(), "Coupon không tồn tại", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!vc.isStatus()) {
                            Toast.makeText(getActivity(), "Coupon không khả dụng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        finalCost = totalCost * (100 - vc.getOff()) / 100;
                        binding.tvAll.setText(Integer.toString(finalCost));
                        binding.tvRedAll.setText(Integer.toString(finalCost));
                        String giam = (totalCost * vc.getOff() / 100) + " (-" + vc.getOff() + "%)";
                        binding.tvCoupon.setText(giam);
                        Toast.makeText(getActivity(), "Quý khách đã được giảm giá", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void MakePurchase(){
        Bundle bundle = getArguments();

        ArrayList<Boolean> seat1 = (ArrayList<Boolean>) getArguments().getSerializable("seat1");
        ArrayList<Boolean> seat2 = (ArrayList<Boolean>) getArguments().getSerializable("seat2");
        Map<String, Object> hm = new HashMap<>();
        hm.put("seat1", seat1);
        hm.put("seat2", seat2);
        String status = "";
        if(binding.radioCredit.isChecked()) {
            pMethod = "Thẻ ATM";
            status = "Đã thanh toán";
        }
        if(binding.radioVisa.isChecked()) {
            if(ClientAuth.Client.getBalance() < finalCost){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Ví không đủ tiền, vui lòng chọn hình thức thanh toán khác hoặc chuyển tiền vào ví");
                builder.show();
                return;
            }
            pMethod = "Ví App";
            status = "Đã thanh toán";
            ClientAuth.Client.AddToWallet(-finalCost);
            db.collection("Users").document(ClientAuth.mClient.getUid())
                    .update("balance", ClientAuth.Client.getBalance());
        }
        if(binding.radioCash.isChecked()) {
            pMethod = "Tiền mặt";
            status = "Chưa thanh toán";
        }

        Timestamp ts = new Timestamp(new Date());

        Ticket ticket = new Ticket(
                DetailTrip.tripID,
                ClientAuth.mClient.getUid(),
                getArguments().getString("detail"),
                finalCost,
                getArguments().getStringArrayList("seatNumber"),
                service,
                getArguments().getInt("numAdult"),
                getArguments().getInt("numChild"),
                status,
                pMethod,
                ts
        );
        int minus = -ticket.getNumAdult() - ticket.getNumChild();

        db.collection("Tickets").add(ticket)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Đặt vé thành công", Toast.LENGTH_SHORT).show();
                        ticket.ticketID = task.getResult().getId();
                        DocumentReference ref = db.collection("Trips").document(DetailTrip.tripID);
                        ref.update("available", FieldValue.increment(minus))
                                .addOnCompleteListener(task1 -> {
                                    ref.update(hm).addOnCompleteListener(task2 -> {
                                        bundle.putSerializable("ticket", ticket);
                                        navController.navigate(R.id.action_totalCostFragment_to_ticketDetailFragment, bundle);
                                    });
                                });
                    }
                    else{
                        Toast.makeText(getActivity(), "Đặt vé thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}