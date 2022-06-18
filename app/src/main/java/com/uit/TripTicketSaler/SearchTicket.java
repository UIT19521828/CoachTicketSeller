package com.uit.TripTicketSaler;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uit.TripTicketSaler.AccountManager.ClientAuth;
import com.uit.TripTicketSaler.Interface.ICallBackTrip;
import com.uit.TripTicketSaler.Model.City;
import com.uit.TripTicketSaler.Model.Trip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SearchTicket extends Fragment{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavController navController;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private Spinner citySpn;
    private Spinner citySpn1;
    private ImageButton datePicker;
    private TextView numPeopleTextview;
    private TextView numChildrenTextview;
    private TextView dateTextview;
    private Button btnSearchC;
    private ImageView btnUserProfile;
    private DrawerLayout drawerLayout;

    private ArrayList<City> lCity = new ArrayList<>();
    private ArrayList<String> lDist = new ArrayList<>();

    public int numPeople = 0;
    public int numChildren = 0;
    private int y, m, d;
    private City cStart;
    private City cEnd;

    public SearchTicket() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_ticket, container, false);

        NavHostFragment hostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = hostFragment.getNavController();


        citySpn = v.findViewById(R.id.citySpinner1);
        citySpn1 = v.findViewById(R.id.citySpinner2);
        numPeopleTextview = v.findViewById(R.id.numPeopleTextview);
        numChildrenTextview = v.findViewById(R.id.numChildrenTextview);
        dateTextview = v.findViewById(R.id.dateTextview);
        datePicker = v.findViewById(R.id.datePickerActions);
        ImageButton adultA = v.findViewById(R.id.adultA);
        ImageButton adultM = v.findViewById(R.id.adultM);
        ImageButton childA = v.findViewById(R.id.childA);
        ImageButton childM = v.findViewById(R.id.childM);
        btnSearchC = v.findViewById(R.id.searchCoach);
        btnUserProfile = v.findViewById(R.id.btnUserProfile);
        drawerLayout = v.findViewById(R.id.drawerMain);
        navigationView = (NavigationView) v.findViewById(R.id.navigation_menu);

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        btnUserProfile.setOnClickListener(view -> {
            drawerLayout.openDrawer(navigationView);
        });
        //tvUsername.setText(ClientAuth.Client.getUsername());
        navigationView.setNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.menuAllTicket) GoToAllTicket();
            if(item.getItemId()==R.id.menuProfile) {GotoProfile();}
            return true;
        });
        LoadCities();
        numPeopleTextview.setText(String.valueOf(numPeople));
        numChildrenTextview.setText(String.valueOf(numChildren));
        citySpn.setSelection(0);
        citySpn1.setSelection(1);

        adultA.setOnClickListener(view -> {
            numPeople++;
            numPeopleTextview.setText(String.valueOf(numPeople));
        });
        adultM.setOnClickListener(view -> {
            if (numPeople != 0) {
                numPeople--;
                numPeopleTextview.setText(String.valueOf(numPeople));
            }
        });
        childA.setOnClickListener(view -> {
            numChildren++;
            numChildrenTextview.setText(String.valueOf(numChildren));
        });
        childM.setOnClickListener(view -> {
            if (numChildren != 0) {
                numChildren--;
                numChildrenTextview.setText(String.valueOf(numChildren));
            }
        });

        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);

        dateTextview.setText(y + "/" + (m + 1) + "/" + d);

        datePicker.setOnClickListener(view -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(),
                    (datePicker, i, i1, i2) -> {
                        dateTextview.setText(i2 + "/" + (i1 + 1) + "/" + i);
                        y = i;
                        m = i1;
                        d = i2;
                    }, y, m, d);
            pickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
            pickerDialog.show();
        });

        btnSearchC.setOnClickListener(view -> {
            SearchCoachClick();
        });

        return v;
    }

    private void LoadCities() {
        db.collection("Cities").get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    City city = doc.toObject(City.class);
                                    lCity.add(city);
                                }
                                Collections.sort(lCity);
                                for (City item : lCity) {
                                    lDist.add(item.getCname());
                                }
                                ArrayAdapter<String> aaC = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_spinner_dropdown_item, lDist);
                                citySpn.setAdapter(aaC);
                                citySpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        cStart = ReturnCity(citySpn.getSelectedItem().toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                                citySpn1.setAdapter(aaC);
                                citySpn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        cEnd = ReturnCity(citySpn1.getSelectedItem().toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }
                        }
                );
    }

    private City ReturnCity(String x) {
        for (City item : lCity) {
            if (x == item.getCname()) return item;
        }
        return null;
    }

    private void SearchCoachClick() {
        SearchFSData((tripList) -> {
            Bundle bundle = new Bundle();
            String afterD = "Sau ngày: " + d + "/" + (m + 1) + "/"+ y;
            bundle.putSerializable("Trips", tripList);
            bundle.putInt("startInt", cStart.getDistance());
            bundle.putInt("endInt", cEnd.getDistance());
            bundle.putString("startS", cStart.getCname());
            bundle.putString("endS", cEnd.getCname());
            bundle.putInt("numCus", numPeople);
            bundle.putInt("numChild", numChildren);
            bundle.putString("afterD" ,afterD);
            navController.navigate(R.id.action_searchTicket_to_listTrip, bundle);
        });
    }

    private void SearchFSData(ICallBackTrip myCallBack){
        ArrayList<Trip> lTrip = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(y, m, d, 0, 0);
        Timestamp ts = new Timestamp(c.getTime());
        db.collection("Trips")
                .whereEqualTo("start", cStart.getCname()).whereEqualTo("finish", cEnd.getCname())
                .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Trip trip =  doc.toObject(Trip.class);
                                if(trip.getAvailable() >= numPeople + numChildren &&
                                    trip.getDeparture_time().compareTo(ts) > 0 ) {
                                    lTrip.add(trip);
                                }
                            }
                            myCallBack.onCallbackTrip(lTrip);
                        }
                    }
                );
    }

    private void GoToAllTicket(){
        navController.navigate(R.id.action_searchTicket_to_allTicketFragment);
    }
    private void GotoProfile(){
        navController.navigate(R.id.action_searchTicket_to_userProfileFragment);
    }
}