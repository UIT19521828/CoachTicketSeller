package com.uit.TripTicketSaler.AccountManager;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.TripTicketSaler.MainActivity;
import com.uit.TripTicketSaler.Model.AppUser;
import com.uit.TripTicketSaler.R;
import com.uit.TripTicketSaler.databinding.FragmentLogInBinding;

public class LogInFragment extends Fragment {
    private FragmentLogInBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavController navController;

    public LogInFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.authNavHost);
        navController = hostFragment.getNavController();
        binding =  FragmentLogInBinding.inflate(inflater, container, false);

        binding.tvGoSignUp.setOnClickListener(view -> {
            navController.navigate(R.id.action_logInFragment_to_signUpFragment);
        });
        binding.btnLogin.setOnClickListener(view -> {
            SignInAccount();
        });
        return binding.getRoot();
    }

    private void SignInAccount(){
        String _email = binding.tvEmail.getText().toString();
        String _pass = binding.tvPassword.getText().toString();
        ClientAuth.mAuth.signInWithEmailAndPassword(_email, _pass)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        ClientAuth.mClient = ClientAuth.mAuth.getCurrentUser();
                        db.collection("Users").document(ClientAuth.mClient.getUid())
                                .get().addOnCompleteListener(task1 -> {
                                        ClientAuth.Client = task1.getResult().toObject(AppUser.class);
                                });
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getActivity(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}