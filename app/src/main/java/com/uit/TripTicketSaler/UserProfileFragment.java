package com.uit.TripTicketSaler;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.AccountManager.ClientAuth;
import com.uit.TripTicketSaler.Model.AppUser;
import com.uit.TripTicketSaler.databinding.FragmentUserProfileBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class UserProfileFragment extends Fragment {
    FragmentUserProfileBinding binding;
    private NavController navController;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AppUser main;
    private int d=1, m=1, y=2001;

    public UserProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();
        main = ClientAuth.Client;

        LoadUSerData();
        binding.btnChange.setOnClickListener(view -> {
            ChangeUserDate();
        });
        binding.backPress.setOnClickListener(view -> {
            navController.navigate(R.id.action_userProfileFragment_to_searchTicket);
        });

        try {
            Calendar cal = Calendar.getInstance();

            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(main.getDob());
            if(date!=null) {
                cal.setTime(date);
                y = cal.get(Calendar.YEAR);
                m = cal.get(Calendar.MONTH);
                d = cal.get(Calendar.DAY_OF_MONTH);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        binding.datePicker.setOnClickListener(view -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(),
                    (datePicker, i, i1, i2) -> {
                        binding.tvDob.setText(i2 + "/" + (i1 + 1) + "/" + i);
                        y = i;
                        m = i1;
                        d = i2;
                    }, y, m, d);
            pickerDialog.show();
        });

        return binding.getRoot();
    }

    private void LoadUSerData(){
        binding.tvAddress.setText(main.getAddress());
        binding.tvCMND.setText(main.getCmnd());
        binding.tvDob.setText(main.getDob());
        binding.tvFullname.setText(main.getUsername());
        binding.tvPhone.setText(main.getPhoneNum());
    }

    private void ChangeUserDate(){
        main.ChangeDataUser(binding.tvFullname.getText().toString(), binding.tvCMND.getText().toString(),
                binding.tvPhone.getText().toString(), binding.tvDob.getText().toString(),
                binding.tvAddress.getText().toString());
        db.collection("Users").document(ClientAuth.mClient.getUid())
                .set(main).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Chỉnh sửa thông tin người dùng thành công", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Chỉnh sửa thất bại: "
                                + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
        LoadUSerData();
    }

}