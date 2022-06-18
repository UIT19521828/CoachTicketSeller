package com.uit.TripTicketSaler.AccountManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uit.TripTicketSaler.Model.AppUser;
import com.uit.TripTicketSaler.R;

import java.util.ArrayList;

public class ClientAuth extends AppCompatActivity {
    public static AppUser Client;
    public static FirebaseAuth mAuth;
    public static FirebaseUser mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authetication);

        mAuth = FirebaseAuth.getInstance();
    }

}
