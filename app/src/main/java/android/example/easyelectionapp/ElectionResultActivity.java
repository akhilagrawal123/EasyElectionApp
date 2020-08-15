package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ElectionResultActivity extends AppCompatActivity {

    private static final String TAG = "Result";
    public Context context = ElectionResultActivity.this;
    RecyclerView resultRecyclerView;
    ResultAdapter adapter;
    ArrayList<Result> results = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    public String id;
    Toolbar toolbar;
    TextView winner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_result);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        assert id != null;
        Log.i(TAG,id);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        resultRecyclerView = (RecyclerView) findViewById(R.id.resultRecyclerView);
        resultRecyclerView.setHasFixedSize(true);
        winner = (TextView) findViewById(R.id.winner);

        fetchingCandidates();
    }

    public void fetchingCandidates()
    {
        DocumentReference docRef = fStore.collection("election").document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    List<String> members = (List<String>) documentSnapshot.get("candidates");

                    for(int i=0;i<members.size();i++)
                    {
                       fetchingResult(members.get(i));
                       Log.i(TAG,members.get(i));
                    }



                }

            }
        });
    }

    public void fetchingResult(final String name)
    {
        DocumentReference docRef = fStore.collection("election").document(id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Log.i(TAG,"results fetch properly");
                if(documentSnapshot.exists())
                {
                    String vote = Objects.requireNonNull(documentSnapshot.get(name)).toString();

                    Log.i(TAG,name + " " + vote);

                    results.add(new Result(name,vote));
                }

                Collections.sort(results, new Comparator<Result>() {
                    @Override
                    public int compare(Result o1, Result o2) {
                        return o2.getVote().compareTo(o1.getVote());
                    }
                });

                if(results.size() > 1) {

                    if (Integer.parseInt(results.get(0).getVote()) > Integer.parseInt(results.get(1).getVote())) {
                        winner.setVisibility(View.VISIBLE);
                        winner.setText("Congratulations to " + results.get(0).getName());
                    } else {
                        winner.setVisibility(View.VISIBLE);
                        winner.setText("Draw");
                    }
                }

                adapter = new ResultAdapter(results);
                adapter.notifyDataSetChanged();
                resultRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                resultRecyclerView.setAdapter(adapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.i(TAG,"Some error : " + e.getMessage());
                Toast.makeText(context, "Error ocured : " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}