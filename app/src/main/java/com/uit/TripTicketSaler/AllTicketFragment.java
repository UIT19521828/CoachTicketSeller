package com.uit.TripTicketSaler;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uit.TripTicketSaler.AccountManager.ClientAuth;
import com.uit.TripTicketSaler.Adapter.TicketAdapter;
import com.uit.TripTicketSaler.Adapter.TripAdapter;
import com.uit.TripTicketSaler.Interface.IAdapterListener;
import com.uit.TripTicketSaler.Interface.ICallBackTicket;
import com.uit.TripTicketSaler.Model.Ticket;
import com.uit.TripTicketSaler.databinding.FragmentAllTicketBinding;

import java.util.ArrayList;

public class AllTicketFragment extends Fragment implements IAdapterListener {
    private FragmentAllTicketBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavController navController;
    ArrayList<Ticket> lTicket = new ArrayList<>();
    public AllTicketFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BackClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllTicketBinding.inflate(inflater, container, false);
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rcvTicket.setLayoutManager(llm);
        RecyclerView.ItemDecoration decor = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.rcvTicket.addItemDecoration(decor);
        LoadFragAllTicket();

        binding.backPress.setOnClickListener(view -> {
            BackClick();
        });

        return binding.getRoot();
    }

    private void LoadFragAllTicket(){
        GetAllTicket(mList -> {
            lTicket = mList;
            TicketAdapter ticketAdapter = new TicketAdapter(lTicket, this);
            binding.rcvTicket.setAdapter(ticketAdapter);
        });
    }

    private void GetAllTicket(ICallBackTicket callBackTicket){
        lTicket.clear();
        db.collection("Tickets").whereEqualTo("userID", ClientAuth.mClient.getUid())
                .orderBy("purchaseDate", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot item : task.getResult()){
                            Ticket ticket = item.toObject(Ticket.class);
                            ticket.ticketID = item.getId();
                            lTicket.add(ticket);
                        }
                        callBackTicket.onCallbackTrip(lTicket);
                    }
                });
    }

    @Override
    public void onClickTicket(int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("ticket", lTicket.get(pos));
        navController.navigate(R.id.action_allTicketFragment_to_ticketDetailFragment, bundle);
    }

    private void BackClick(){
        navController.navigate(R.id.action_allTicketFragment_to_searchTicket);
    }
}