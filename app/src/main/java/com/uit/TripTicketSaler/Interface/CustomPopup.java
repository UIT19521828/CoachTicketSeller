package com.uit.TripTicketSaler.Interface;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.AccountManager.ClientAuth;
import com.uit.TripTicketSaler.MainActivity;
import com.uit.TripTicketSaler.databinding.PopupTransitionBinding;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CustomPopup extends DialogFragment {
    private PopupTransitionBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static boolean isAdd;
    private static int changed = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopupTransitionBinding.inflate(inflater, container, false);
        if(!isAdd) {
            binding.headerPop.setText("Rút tiền khỏi ví");
            binding.headerPop.setTextColor(Color.parseColor("#FF6347"));
            binding.btnTrans.setBackgroundColor(Color.parseColor("#FF6347"));
        }

        binding.btnTrans.setOnClickListener(view -> {
            OpenAlert();
        });

        return binding.getRoot();
    }
    private void OpenAlert(){
        if(binding.editTrans.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Bạn chưa chọn số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        int add = Integer.parseInt(binding.editTrans.getText().toString());
        if(add==0) {
            Toast.makeText(getActivity(), "Bạn chưa chọn số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isAdd) add = -add;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn muốn giao dịch với số tiền: " + add)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    AddToWallet();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    Toast.makeText(getActivity(), "Không giao dịch", Toast.LENGTH_SHORT).show();
                });
        builder.show();
    }

    private void AddToWallet(){
        int add = Integer.parseInt(binding.editTrans.getText().toString());
        if(!isAdd) {
            if(add > ClientAuth.Client.getBalance()) {
                Toast.makeText(getActivity(), "Tài khoản không đủ tiền để rút", Toast.LENGTH_SHORT).show();
                return;
            }
            add = -add;
        }

        ClientAuth.Client.AddToWallet(add);
        Calendar calendar = Calendar.getInstance();
        long trans = calendar.getTimeInMillis();
        String key = trans + "";

        ClientAuth.Client.AddToTransition(key, add);
        db.collection("Users").document(ClientAuth.mClient.getUid())
                .set(ClientAuth.Client).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        changed++;
                        Bundle bundle = new Bundle();
                        bundle.putInt("request_trans", changed);
                        requireActivity().getSupportFragmentManager().setFragmentResult("request_trans", bundle);
                        Toast.makeText(getActivity(), "Giao dịch thành công", Toast.LENGTH_SHORT).show();

                        getDialog().dismiss();
                    }
                    else{
                        Toast.makeText(getActivity(), "Giao dịch thất bại", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                });

    }
    @Override public void onAttach(Context context)
    {
        super.onAttach(context);
    }

}
