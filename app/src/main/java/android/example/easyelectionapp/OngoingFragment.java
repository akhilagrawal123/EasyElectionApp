package android.example.easyelectionapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


public class OngoingFragment extends Fragment implements OngoingAdapter.OnOngoingElectionItemListener {


    private static final String TAG = "Hi6";

    RecyclerView ongoingRecyclerView;
    OngoingAdapter ongoingAdapter;
    FloatingActionButton createElection;
    public Context context;
    public ArrayList<Elections> elections;
    int electionType;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;



    public OngoingFragment() {
        // Required empty public constructor
    }

    public OngoingFragment(int electionType, ArrayList<Elections> elections, Context context)
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
        ongoingAdapter = new OngoingAdapter(elections,(OngoingAdapter.OnOngoingElectionItemListener) OngoingFragment.this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_ongoing, container, false);

        ElectionActivity activity = (ElectionActivity) getActivity();
        assert activity != null;
        final String roomId = activity.roomIdString();

        ongoingRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewOngoing);
        ongoingRecyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        ongoingRecyclerView.setAdapter(ongoingAdapter);


        createElection = view.findViewById(R.id.createElection);
        createElection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checking that user is admin of room or not..............
                fStore.collection("rooms")
                        .document(roomId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists())
                                {
                                    Log.i(TAG, documentSnapshot.getString("admin") + "  " + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                                    if(Objects.equals(documentSnapshot.getString("admin"), mAuth.getCurrentUser().getUid()))
                                    {
                                        Log.i(TAG,"User can conduct election." + documentSnapshot.getString("admin"));
                                        Intent intent = new Intent(context,CreateElectionActivity.class);
                                        intent.putExtra("roomName",roomId);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "Sorry, As you are not admin of this room. So you are not allowed to conduct any elections..", Toast.LENGTH_SHORT).show();
                                        Log.i(TAG,"Cant add member...");
                                    }

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.i(TAG,"Error : " + e.getMessage());

                            }
                        });


            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.onCreate(null);
    }

    @Override
    public void OnOngoingElectionItemClick(int position) {

        Elections current = elections.get(position);

        Intent intent = new Intent(context,VotingActivity.class);
        intent.putExtra("id",current.getId());
        intent.putExtra("title",current.getTitle());
        intent.putExtra("purpose",current.getPurpose());
        intent.putExtra("roomName",current.getRoomId());

        startActivity(intent);

    }

    @Override
    public void OnOngoingElectionItemLongClick(int position) {

    }
}