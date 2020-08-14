package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UsersListActivity extends AppCompatActivity implements UsersListAdapter.OnUserItemListener {

    private static final String TAG = "Hi";
    UsersListAdapter usersListAdapter;
    RecyclerView userListRecyclerView;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    Toolbar toolbar;
    Context context = UsersListActivity.this;
    String roomName,senderName;


    ArrayList<AllUsers> allUsersArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();





        //setting recycler view.....
        userListRecyclerView = (RecyclerView) findViewById(R.id.userListRecyclerView);
        userListRecyclerView.setHasFixedSize(true);



        //fetching all users list from firebase to here...
        fetchUsersList();


    }

    public void fetchUsersList()
    {
        fStore.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot qds : Objects.requireNonNull(task.getResult()))
                            {
                                String name = qds.getString("username");
                                String uri = qds.getString("imagePath");
                                String uid = qds.getString("uid");

                                if(name != null && uri != null && uid != null)
                                {
                                    Log.i("HI",name);
                                    Log.i("HI",uri);
                                    Log.i("HI",uid);
                                }

                                allUsersArrayList.add(new AllUsers(name,uri,uid));

                            }

                            usersListAdapter = new UsersListAdapter(allUsersArrayList,(UsersListAdapter.OnUserItemListener) context);
                            usersListAdapter.notifyDataSetChanged();
                            userListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            userListRecyclerView.setAdapter(usersListAdapter);

                        }

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                usersListAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public void OnUserItemClick(int position) {

    }

    @Override
    public void OnUserItemLongClick(int position) {

         final AllUsers currentUser = allUsersArrayList.get(position);

         final String uid = currentUser.getUid();

        new AlertDialog.Builder(UsersListActivity.this)
                .setTitle("Invitation Confirmation")
                .setMessage("Are you sure that you want to invite \n" + currentUser.getUserName() + " to join your room " + roomName)
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i(TAG,uid);

                        //fetching sender name from firebase..........
                        DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists()) {

                                    Log.i(TAG, Objects.requireNonNull(documentSnapshot.getString("username")));

                                    //calling a method to send notification.........
                                    sendingNotification(uid,documentSnapshot.getString("username"));
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Some Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i(TAG, Objects.requireNonNull(e.getMessage()));

                            }
                        });




                    }
                })
                .setNegativeButton("Cancel",null)
                .show();



    }

    //sending notification to join the room....
    public void sendingNotification(String uid,String name)
    {
        DocumentReference document = fStore.collection("notifications").document();

        Map<String,Object> data = new HashMap<>();


        data.put("to",uid);
        data.put("from", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        data.put("room",roomName);
        data.put("senderName",name);

        document.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Log.i(TAG,"Invitation Send Successfully");
                    Toast.makeText(context, "Invitation send successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UsersListActivity.this,MembersActivity.class);
                    intent.putExtra("roomName",roomName);
                    finish();
                    startActivity(intent);
                }
                else
                {
                    Log.i(TAG,"Invitation sending failed.");
                    Toast.makeText(context, "Invitation not send", Toast.LENGTH_SHORT).show();
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
}