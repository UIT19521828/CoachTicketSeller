package com.uit.TripTicketSaler.Payment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.uit.TripTicketSaler.AccountManager.ClientAuth;
import com.uit.TripTicketSaler.MainActivity;
import com.uit.TripTicketSaler.Model.Ticket;
import com.uit.TripTicketSaler.Model.Trip;
import com.uit.TripTicketSaler.R;
import com.uit.TripTicketSaler.databinding.FragmentTicketDetailBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TicketDetailFragment extends Fragment {
    private FragmentTicketDetailBinding binding;
    private NavController navController;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Trip trip;
    private Ticket ticket;

    public TicketDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BackPressClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentTicketDetailBinding.inflate(inflater, container, false);
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();
        ticket = (Ticket)getArguments().getSerializable("ticket");

        db.collection("Trips").document(ticket.getTrip())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        trip = task.getResult().toObject(Trip.class);
                        binding.tvCoachPlate.setText(trip.getCoach());
                        binding.tvTripName.setText(trip.getTripName());
                        binding.tvStartDate.setText(trip.GetDepartureDate());
                    }
                });

        ArrayList<String> arrSelected =  ticket.getSeatNumber();
        String seatNum ="";
        for(String item : arrSelected){
            seatNum += item + " ";
        }
        int nA = ticket.getNumAdult();
        int nC = ticket.getNumChild();
        String service = "";
        if(Boolean.TRUE.equals(ticket.getService().get("suitcase"))) service += ",H??nh l??";
        if(Boolean.TRUE.equals(ticket.getService().get("breakfast"))) service += ",B???a s??ng";
        if(Boolean.TRUE.equals(ticket.getService().get("meal"))) service += ",B???a ch??nh";
        if(Boolean.TRUE.equals(ticket.getService().get("insurance"))) service += ",B???o hi???m";
        if(!service.equals("")) service = service.substring(1);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(ticket.ticketID, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.imgQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        binding.tvStatus.setText(ticket.getStatus());
        if(!Objects.equals(ticket.getStatus(), "???? s??? d???ng")) {
            binding.tvStatus.setTextColor(Color.RED);
        }
        else{
            binding.btnCancel.setVisibility(View.INVISIBLE);
            binding.btnCancel.setEnabled(false);
        }
        binding.tvTicketDetail.setText(ticket.getDetail());
        binding.tvTotalCost.setText(Integer.toString(ticket.getTotalCost()));
        binding.tvBookingDate.setText(ticket.GetPurTime());
        binding.tvCustomerName.setText(ClientAuth.Client.getUsername());
        binding.tvTicketID.setText("ID: "+ticket.ticketID);
        String slkh = nA + " ng?????i l???n ";
        if(nC>0) slkh += nC + " tr??? em";
        binding.tvSLKH.setText(slkh);

        binding.tvService.setText(service);
        binding.tvSelectedSeats.setText(seatNum);

        binding.btnHomePage.setOnClickListener(view -> {
            navController.navigate(R.id.action_ticketDetailFragment_to_searchTicket);
        });
        binding.btnCancel.setOnClickListener(view -> {
            CancelTicket();
        });
        binding.backPress.setOnClickListener(view -> {
            BackPressClick();
        });
        return binding.getRoot();
    }

    private void CancelTicket(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("B???n c?? mu???n h???y v?? n??y kh??ng?")
                .setPositiveButton("C??", (dialog, id) -> {
                    if(Objects.equals(ticket.getStatus(), "???? thanh to??n")){
                        ClientAuth.Client.AddToWallet(ticket.getTotalCost());
                        db.collection("Users").document(ClientAuth.mClient.getUid())
                                .update("balance", ClientAuth.Client.getBalance());
                    }
                    ArrayList<Boolean> seat1 = trip.getSeat1();
                    ArrayList<Boolean> seat2 = trip.getSeat2();

                    ArrayList<String> seatNum = ticket.getSeatNumber();
                    for(String item : seatNum){
                        int count = Integer.parseInt(String.valueOf(item.charAt(1)));
                        if(item.charAt(0)=='A'){
                            count = (count - 1) * 3;
                            seat1.set(count, false);
                        }
                        if(item.charAt(0)=='B'){
                            count = (count - 1) * 3 + 1;
                            seat1.set(count, false);
                        }
                        if(item.charAt(0)=='C'){
                            count = (count - 1) * 3 + 2;
                            seat1.set(count, false);
                        }
                        if(item.charAt(0)=='D'){
                            count = (count - 1) * 3;
                            seat2.set(count, false);
                        }
                        if(item.charAt(0)=='E'){
                            count = (count - 1) * 3 + 1;
                            seat2.set(count, false);
                        }
                        if(item.charAt(0)=='F'){
                            count = (count - 1) * 3 + 2;
                            seat2.set(count, false);
                        }
                    }
                    int plus = ticket.getNumAdult() + ticket.getNumChild();
                    Map<String, Object> hm = new HashMap<>();
                    hm.put("seat1", seat1);
                    hm.put("seat2", seat2);
                    db.collection("Tickets").document(ticket.ticketID)
                            .delete().addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    DocumentReference tripRef = db.collection("Trips").document(ticket.getTrip());
                                    tripRef.update(hm).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            tripRef.update("available", FieldValue.increment(plus))
                                                    .addOnCompleteListener(task3 -> {
                                                        Toast.makeText(getActivity(), "H???y v?? th??nh c??ng", Toast.LENGTH_SHORT).show();
                                                        navController.navigate(R.id.action_ticketDetailFragment_to_searchTicket);
                                                    });
                                        } else {
                                            Toast.makeText(getActivity(), "H???y v?? th???t b???i", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "H???y v?? th???t b???i", Toast.LENGTH_SHORT).show();
                                }
                            });

                })
                .setNegativeButton("Kh??ng", (dialog, id) -> {
                    Toast.makeText(getActivity(), "Kh??ng h???y v??", Toast.LENGTH_SHORT).show();
                });
        builder.show();
    }

    private void BackPressClick(){
        navController.navigate(R.id.action_ticketDetailFragment_to_allTicketFragment);
    }
}