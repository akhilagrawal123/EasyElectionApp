package android.example.easyelectionapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class PastFragment extends Fragment implements PastAdapter.OnPastElectionItemListener {

    private static final String TAG = "Hi7";

    RecyclerView pastRecyclerView;
    private Context context;
    public ArrayList<Elections> elections;
    int electionType;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    PastAdapter pastAdapter;

    public String roomId;




    public PastFragment() {
        // Required empty public constructor
    }

    public PastFragment(int electionType, ArrayList<Elections> elections, Context context)
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
        pastAdapter = new PastAdapter(elections,PastFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

              // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past, container, false);

        ElectionActivity activity = (ElectionActivity) getActivity();
        assert activity != null;
        roomId = activity.roomIdString();

        pastRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPast);
        pastRecyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        pastRecyclerView.setAdapter(pastAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.onCreate(null);
    }

    @Override
    public void OnPastElectionItemClick(int position) {

        Elections current  = elections.get(position);


        Intent intent = new Intent(context,ElectionResultActivity.class);
        intent.putExtra("id",current.getId());
        startActivity(intent);


    }

    @Override
    public void OnPastElectionItemLongClick(int position) {

    }
}