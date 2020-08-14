package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VotingActivity extends AppCompatActivity implements CandidateAdapter.OnCandidateItemListener {

    private static final String TAG = "Vote";
    public Context context = VotingActivity.this;
    public String id,title,purpose;
    RecyclerView candidatesRecyclerView;
    CandidateAdapter candidateAdapter;
    ArrayList<Candidates> candidates = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Toolbar toolbar;
    String roomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        purpose = intent.getStringExtra("purpose");
        roomName = intent.getStringExtra("roomName");

        TextView titleText = (TextView) findViewById(R.id.voteTitle);
        TextView purposeText = (TextView) findViewById(R.id.voteDescription);

        titleText.setText(title);
        purposeText.setText(purpose);

        candidatesRecyclerView = (RecyclerView) findViewById(R.id.candidateRecyclerView);
        candidatesRecyclerView.setHasFixedSize(true);

        fetchCandidatesList();

    }

    public void fetchCandidatesList()
    {
        DocumentReference docRef = fStore.collection("election").document(id);

                  docRef.get()
                  .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                          if(task.isSuccessful())
                          {
                              DocumentSnapshot documentSnapshot = task.getResult();
                              assert documentSnapshot != null;
                              List<String> members = (List<String>) documentSnapshot.get("candidates");

                              for(int i=0;i<members.size();i++)
                              {
                                  candidates.add(new Candidates(members.get(i)));
                                  Log.i(TAG,members.get(i) + " Successsfully added");

                              }
                              Log.i(TAG,"Task is successful");



                              candidateAdapter = new CandidateAdapter(candidates,VotingActivity.this);
                              candidateAdapter.notifyDataSetChanged();
                              candidatesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                              candidatesRecyclerView.setAdapter(candidateAdapter);
                          }
                          else
                          {
                              Toast.makeText(context, "Some Error  : " + task.getException(), Toast.LENGTH_SHORT).show();
                              Log.i(TAG,task.getException().toString());
                          }

                      }
                  });
    }

    @Override
    public void OnCandidateItemClick(int position) {



    }

    @Override
    public void OnCandidateItemLongClick(int position) {

             Candidates current = candidates.get(position);

             final String name = current.getName();

        fStore.collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            boolean flag  = false;
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;
                            List<String> members = (List<String>) documentSnapshot.get("votes");

                            assert members != null;
                            for(int i = 0; i<members.size(); i++)
                            {
                                if(members.get(i).equals(id))
                                {
                                    flag = true;
                                    break;
                                }
                            }

                            if(flag)
                            {
                                Toast.makeText(context, "You have already given vote.", Toast.LENGTH_SHORT).show();
                                Log.i(TAG,"Already given vote");
                            }
                            else
                            {
                                Log.i(TAG,"not given vote");
                                new AlertDialog.Builder(context)
                                        .setTitle("Vote Confirmation")
                                        .setMessage("Are you sure you wanted to give your vote to " + name+" .You cannot undo this later.")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                uploadVote(name);

                                            }
                                        })
                                        .setNegativeButton("NO",null)
                                        .show();
                            }


                        }
                        else
                        {
                            Toast.makeText(context, "Some error : " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"Error : " + task.getException());
                        }

                    }
                });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    public void uploadVote(String name)
    {
        DocumentReference docRef = fStore.collection("election").document(id);

        docRef.update(name, FieldValue.increment(1));
        Log.i(TAG,"Vote given successfully");

        addingVoteToUserId();
    }

    //adding vote history to user id also
    public void addingVoteToUserId()
    {
        DocumentReference docRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        docRef.update("votes",FieldValue.arrayUnion(id));
        Log.i(TAG,"User id also get updated");

        Intent intent = new Intent(VotingActivity.this,ElectionActivity.class);
        intent.putExtra("roomName",roomName);
        finish();
        startActivity(intent);
    }
}