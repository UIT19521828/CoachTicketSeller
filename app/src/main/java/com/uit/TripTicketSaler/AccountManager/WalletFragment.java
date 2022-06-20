package com.uit.TripTicketSaler.AccountManager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.Adapter.TransitionAdapter;
import com.uit.TripTicketSaler.Interface.CustomPopup;
import com.uit.TripTicketSaler.R;
import com.uit.TripTicketSaler.databinding.FragmentWalletBinding;

import java.util.Observable;

import io.grpc.LoadBalancer;

public class WalletFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentWalletBinding binding;
    private NavController navController;

    public WalletFragment() {}

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
        binding = FragmentWalletBinding.inflate(inflater, container, false);
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rcvTrans.setLayoutManager(llm);
        RecyclerView.ItemDecoration decor = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.rcvTrans.addItemDecoration(decor);

        LoadTransition();
        requireActivity().getSupportFragmentManager().setFragmentResultListener("request_trans",
                getViewLifecycleOwner(), (requestKey, result) -> {
            LoadTransition();
        });

        binding.btnPlus.setOnClickListener(view -> {
            CustomPopup popup = new CustomPopup();
            CustomPopup.isAdd = true;
            popup.show(getChildFragmentManager(), "Transition");
        });
        binding.btnMinus.setOnClickListener(view -> {
            CustomPopup popup = new CustomPopup();
            CustomPopup.isAdd = false;
            popup.show(getChildFragmentManager(), "Transition");
        });

       binding.btnReload.setOnClickListener(view -> {
           LoadTransition();
           Toast.makeText(getActivity(), "Đã Load lại ví", Toast.LENGTH_SHORT).show();
       });

       binding.backPress.setOnClickListener(view -> BackPressClick());

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void LoadTransition(){
        binding.rcvTrans.setAdapter(null);
        binding.tvBalance.setText(ClientAuth.Client.getBalance() + "");
        TransitionAdapter adapter = new TransitionAdapter(ClientAuth.Client.getTransition_history());
        binding.rcvTrans.setAdapter(adapter);
    }

    private void BackPressClick(){
        navController.navigate(R.id.action_walletFragment_to_searchTicket);
    }

}