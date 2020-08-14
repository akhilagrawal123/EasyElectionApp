package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaExtractor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MembersActivity extends AppCompatActivity {

    private static final String TAG = "Hi4";
    Toolbar toolbar;
    FloatingActionButton addMembers;
    String roomName;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    RecyclerView membersRecyclerView;
    ArrayList<Members> membersArrayList = new ArrayList<>();
    MembersAdapter membersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        Log.i(TAG,roomName);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        membersRecyclerView = (RecyclerView) findViewById(R.id.memberRecyclerView);
        membersRecyclerView.setHasFixedSize(true);


        //fetching and displaying members list to members activity.......
        fetchMemberList();



        addMembers = findViewById(R.id.addMembers);
        addMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //checking that user is admin of room or not..............
                fStore.collection("rooms")
                        .document(roomName)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists())
                                {
                                    Log.i(TAG, documentSnapshot.getString("admin") + "  " + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                                    if(Objects.equals(documentSnapshot.getString("admin"), mAuth.getCurrentUser().getUid()))
                                    {
                                        Log.i(TAG,"User can add members  " + documentSnapshot.getString("admin"));
                                        Intent intent1 = new Intent(MembersActivity.this,UsersListActivity.class);
                                        intent1.putExtra("roomName",roomName);
                                        startActivity(intent1);

                                    }
                                    else
                                    {
                                        Toast.makeText(MembersActivity.this, "Sorry, As you are not admin of this room. So you are not allowed to add any member.", Toast.LENGTH_SHORT).show();
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




    }

    public void fetchMemberList()
    {
       fStore.collection("rooms").document(roomName)
               .get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                       if(task.isSuccessful())
                       {
                           DocumentSnapshot documentSnapshot = task.getResult();
                           assert documentSnapshot != null;
                           List<String> members = (List<String>) documentSnapshot.get("members");

                           for(int i=0;i<members.size();i++)
                           {
                               String id = members.get(i);
                               Log.i(TAG,id);

                               //adding member details to members modal class to show in recycler view.,.,,,,,,,,,,,,//
                               addingToList(id);

                           }


                       }
                       else
                       {
                           Toast.makeText(MembersActivity.this, "Error : " +task.getException(), Toast.LENGTH_SHORT).show();
                           Log.i(TAG,"Error : "+ task.getException());
                       }

                   }
               });
    }

    public void addingToList(String id)
    {
        final DocumentReference docRef  = fStore.collection("users").document(id);

       docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists())
               {
                   Log.i(TAG,documentSnapshot.getString("username") + "  "  + documentSnapshot.getString("imagePath")+ "  " + documentSnapshot.getString("uid"));
                   membersArrayList.add(new Members(documentSnapshot.getString("username"),documentSnapshot.getString("imagePath"),documentSnapshot.getString("uid")));
               }
               membersAdapter = new MembersAdapter(membersArrayList);
               membersAdapter.notifyDataSetChanged();
               membersRecyclerView.setLayoutManager(new LinearLayoutManager(MembersActivity.this));
               membersRecyclerView.setAdapter(membersAdapter);

           }
       })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {

                       Toast.makeText(MembersActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                       Log.i(TAG,"Eror : " + e.getMessage());

                   }
               });
    }


}