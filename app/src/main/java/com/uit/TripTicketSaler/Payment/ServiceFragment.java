package com.uit.TripTicketSaler.Payment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uit.TripTicketSaler.R;
import com.uit.TripTicketSaler.databinding.FragmentServiceBinding;

public class ServiceFragment extends Fragment {
    private FragmentServiceBinding binding;
    private NavController navController;
    private static Bundle next;

    public ServiceFragment() {}

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceBinding.inflate(inflater, container, false);
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();

        if(getArguments()!=null) next = getArguments();
        binding.btnGoNext.setOnClickListener(view -> {
            GoNextFrag();
        });
        binding.backPress.setOnClickListener(view -> {
            BackPressClick();
        });

        return binding.getRoot();
    }

    private void GoNextFrag(){
        boolean[] checkService = {false, false, false, false};
        if(binding.checkSuitcase.isChecked()) checkService[0] = true;
        if(binding.checkBreakfast.isChecked()) checkService[1] = true;
        if(binding.checkMeal.isChecked()) checkService[2] = true;
        if(binding.checkInsurance.isChecked()) checkService[3] = true;
        next.putBooleanArray("service", checkService);
        navController.navigate(R.id.action_serviceFragment_to_totalCostFragment, next);
    }
    private void BackPressClick(){
        navController.navigate(R.id.action_serviceFragment_to_detailTrip);
    }
}