package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCandidatesActivity extends AppCompatActivity {

    private static final String TAG = "Hi6";
    Toolbar toolbar;
    EditText can1,can2,can3,can4,can5;
    Button add;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String id,roomName;
    ProgressBar candidatesProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidates);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        roomName = intent.getStringExtra("roomName");

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        can1 = (EditText) findViewById(R.id.candidate1);
        can2 = (EditText) findViewById(R.id.candidate2);
        can3 = (EditText) findViewById(R.id.candidate3);
        can4 = (EditText) findViewById(R.id.candidate4);
        can5 = (EditText) findViewById(R.id.candidate5);
        candidatesProgressBar = (ProgressBar) findViewById(R.id.candidatesAddedProgressBar);

        add = (Button) findViewById(R.id.addBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name1 = can1.getText().toString();
                String name2 = can2.getText().toString();
                String name3 = can3.getText().toString();
                String name4 = can4.getText().toString();
                String name5 = can5.getText().toString();

                if(name1.isEmpty() && name2.isEmpty() && name3.isEmpty() && name4.isEmpty() && name5.isEmpty())
                {
                    Toast.makeText(AddCandidatesActivity.this, "Minimum One Candidate Is Required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                candidatesProgressBar.setVisibility(View.VISIBLE);

                AddToFirebase(name1,name2,name3,name4,name5);
            }
        });




    }


    //adding all candidates to firebase........
    public void AddToFirebase(String nm1,String nm2,String nm3,String nm4,String nm5) {
        DocumentReference documentReference = fStore.collection("election").document(id);

        if (!nm1.isEmpty()) {
            documentReference.update("candidates", FieldValue.arrayUnion(nm1));
            Map<String, Object> vote = new HashMap<>();
            vote.put(nm1, 0);
            documentReference.update(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Log.i(TAG, "vote has been added");

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.i(TAG, "Some error :" + e.getMessage());

                        }
                    });
            Log.i(TAG, nm1);
        }
        if (!nm2.isEmpty()) {
            documentReference.update("candidates", FieldValue.arrayUnion(nm2));
            Log.i(TAG, nm2);
            Map<String, Object> vote = new HashMap<>();
            vote.put(nm2, 0);
            documentReference.update(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Log.i(TAG, "vote has been added");

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.i(TAG, "Some error :" + e.getMessage());

                        }
                    });
        }

        if (!nm3.isEmpty()) {
            documentReference.update("candidates", FieldValue.arrayUnion(nm3));
            Log.i(TAG, nm3);
            Map<String, Object> vote = new HashMap<>();
            vote.put(nm3, 0);
            documentReference.update(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Log.i(TAG, "vote has been added");

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.i(TAG, "Some error :" + e.getMessage());

                        }
                    });
        }

        if (!nm4.isEmpty()) {
            documentReference.update("candidates", FieldValue.arrayUnion(nm4));
            Log.i(TAG, nm4);
            Map<String, Object> vote = new HashMap<>();
            vote.put(nm4, 0);
            documentReference.update(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Log.i(TAG, "vote has been added");

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.i(TAG, "Some error :" + e.getMessage());

                        }
                    });
        }

        if (!nm5.isEmpty()) {
            documentReference.update("candidates", FieldValue.arrayUnion(nm5));
            Log.i(TAG, nm5);
            Map<String, Object> vote = new HashMap<>();
            vote.put(nm5, 0);
            documentReference.update(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Log.i(TAG, "vote has been added");


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.i(TAG, "Some error :" + e.getMessage());

                        }
                    });
        }

        Log.i(TAG, "All SEt");
        candidatesProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(AddCandidatesActivity.this, "Candidates Added Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddCandidatesActivity.this,ElectionActivity.class);
        intent.putExtra("roomName",roomName);
        finish();
        startActivity(intent);


    }
}