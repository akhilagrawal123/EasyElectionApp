package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ElectionActivity extends AppCompatActivity {


    private static final String TAG = "Hi3";
    //variables description......
    TabLayout electionTabLayout;
    TabItem ongoing,upcoming,past;
    ViewPager2 viewPager;
    ElectionsPagerAdapter electionsPagerAdapter;
    Toolbar toolbar;
    String roomName;
    ArrayList<Elections> electionsArrayList;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        electionTabLayout = findViewById(R.id.electionTabLayout);
        ongoing = findViewById(R.id.ongoingElections);
        upcoming = findViewById(R.id.upcomingElection);
        past = findViewById(R.id.pastElection);
        viewPager = findViewById(R.id.electionViewPager);
        electionsArrayList = new ArrayList<>();

        fetchingElectionInfo(roomName);




    }


    //fetching election info from firebase
    public void fetchingElectionInfo(String room)
    {
        fStore.collection("election")
                .whereEqualTo("roomId" , room)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            Log.i(TAG,"TASk is successfully executed");
                            for(QueryDocumentSnapshot qds : Objects.requireNonNull(task.getResult()))
                            {
                                Log.i(TAG,"id : " + qds.getId());
                                Log.i(TAG,"title : " + qds.getString("title"));
                                Log.i(TAG,"purpose : " + qds.getString("purpose"));
                                Log.i(TAG,"roomId : " + qds.getString("roomId"));
                                Log.i(TAG,"ownerId : " + qds.getString("ownerId"));
                                Log.i(TAG,"Start : " + qds.getString("start"));
                                Log.i(TAG,"End : " + qds.getString("end"));


                                format.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                                Log.i(TAG,format.format(new Date()).toString());

                                electionsArrayList.add(new Elections(qds.getId(),qds.getString("roomId"),qds.getString("ownerId"),qds.getString("start"),qds.getString("end"),qds.getString("title"),qds.getString("purpose")));

                                //Log.i(TAG,qds.getString("id") + " " + qds.getString("roomId") + " " + qds.getString("ownerId") + " " + qds.getString("purpose") + " " + qds.getString("title") + " " + Objects.requireNonNull(qds.getTimestamp("end")).toString() + " " + Objects.requireNonNull(qds.getTimestamp("start")).toString());
                            }
                            //setting up fragments to an activity............
                            //starts..........
                            electionsPagerAdapter = new ElectionsPagerAdapter(getSupportFragmentManager(), getLifecycle(),electionsArrayList, ElectionActivity.this);
                            electionsPagerAdapter.notifyDataSetChanged();
                            viewPager.setAdapter(electionsPagerAdapter);

                            new TabLayoutMediator(electionTabLayout, viewPager,new TabLayoutMediator.TabConfigurationStrategy() {
                                @Override
                                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                    if(position==0)
                                    {
                                        tab.setText("Ongoing");
                                    }
                                    if(position == 1)
                                    {
                                        tab.setText("Upcoming");
                                    }
                                    if(position == 2)
                                    {
                                        tab.setText("Past");
                                    }

                                }
                            }).attach();



                        }
                        else
                        {
                            Toast.makeText(ElectionActivity.this, "Some error : "  + task.getException(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.election_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.roomMembers)
        {
            Log.i(TAG,"Members Item Selected");
            Intent intent = new Intent(ElectionActivity.this,MembersActivity.class);
            intent.putExtra("roomName",roomName);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.roomInfoItem)
        {
            Log.i(TAG,"RoomInfo Item Selected");
            Intent intent = new Intent(ElectionActivity.this,RoomInfoActivity.class);
            intent.putExtra("roomName",roomName);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public String roomIdString()
    {
        return roomName;
    }
}