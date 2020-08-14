package android.example.easyelectionapp;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;




public class MyRoomsFragment extends Fragment implements MyRoomsAdapter.OnRoomItemListener{


    private static final String TAG = "Hi";


    public MyRoomsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View view;
    FloatingActionButton createRoomBtn;
    RecyclerView myRoomsRecyclerView;
    MyRoomsAdapter myRoomsAdapter;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    ArrayList<MyRooms> myRoomsArrayList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_my_rooms, container, false);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        createRoomBtn = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(),CreateRoomActivity.class));

            }
        });

        myRoomsRecyclerView = (RecyclerView) view.findViewById(R.id.myRoomsRecyclerView);
        myRoomsRecyclerView.setHasFixedSize(true);

        checkingUsersRoom();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    public void checkingUsersRoom()
    {
        myRoomsArrayList  = new ArrayList<>();
        DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {
                    Map<String, Object> docData = Objects.requireNonNull(task.getResult()).getData();

                    if (docData.get("rooms") != null) {

                        HashMap<String, String> roomList = (HashMap<String, String>) docData.get("rooms");

                        assert roomList != null;
                        for (HashMap.Entry<String, String> entry : roomList.entrySet()) {
                            myRoomsArrayList.add(new MyRooms(entry.getKey(), entry.getValue()));
                        }

                        myRoomsAdapter = new MyRoomsAdapter(myRoomsArrayList, (MyRoomsAdapter.OnRoomItemListener) MyRoomsFragment.this);
                        myRoomsAdapter.notifyDataSetChanged();
                        myRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        myRoomsRecyclerView.setAdapter(myRoomsAdapter);


                    }
                }

            }
        });
    }


    @Override
    public void OnRoomItemClick(int position) {

        MyRooms currentRoom =  myRoomsArrayList.get(position);

     if(currentRoom.getStatus().equals("admin"))
     {
         Toast.makeText(view.getContext(), "You Are Admin", Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(view.getContext(),ElectionActivity.class);
         intent.putExtra("roomName",currentRoom.getRoomName());
         startActivity(intent);
         Log.i(TAG,"You Are Admin");

     }
     else
     {
         Toast.makeText(view.getContext(), "You Are Not Admin", Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(view.getContext(),ElectionActivity.class);
         intent.putExtra("roomName",currentRoom.getRoomName());
         startActivity(intent);
         Log.i(TAG,"You Are not Admin");
     }

    }

    @Override
    public void OnRoomItemLongClick(int position) {

    }

}