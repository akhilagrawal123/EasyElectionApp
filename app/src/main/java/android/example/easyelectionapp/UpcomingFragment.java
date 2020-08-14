package android.example.easyelectionapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class UpcomingFragment extends Fragment implements UpcomingAdapter.OnUpcomingElectionItemListener {


    private static final String TAG = "Hi8";

    RecyclerView upcomingRecyclerView;
    private Context context;
    private ArrayList<Elections> elections;
    int electionType;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    UpcomingAdapter upcomingAdapter;




    public UpcomingFragment() {
        // Required empty public constructor
    }

    public UpcomingFragment(int electionType, ArrayList<Elections> elections, Context context)
    {
        this.electionType = electionType;
        this.elections = elections;
        this.context = context;

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upcomingAdapter = new UpcomingAdapter(elections,UpcomingFragment.this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_upcoming, container, false);

        upcomingRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUpcoming);
        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        upcomingRecyclerView.setAdapter(upcomingAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.onCreate(null);
    }

    @Override
    public void OnUpcomingElectionItemClick(int position) {

        Toast.makeText(context, "Election is not started yet.", Toast.LENGTH_SHORT).show();
        Log.i(TAG,"election not started");

    }

    @Override
    public void OnUpcomingElectionItemLongClick(int position) {

    }
}