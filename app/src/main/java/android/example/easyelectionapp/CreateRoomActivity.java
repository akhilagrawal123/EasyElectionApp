package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateRoomActivity extends AppCompatActivity {

    private static final String TAG = "Hi";
    TextInputLayout roomDescription;
    EditText roomName;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String phoneNum;
    Button createBtn;
    Toolbar toolbar;
    ProgressBar roomCreateProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        roomDescription = (TextInputLayout) findViewById(R.id.roomDescription);
        roomName = (EditText)findViewById(R.id.roomName);
        createBtn = (Button) findViewById(R.id.createBtn);
        roomCreateProgressBar = (ProgressBar) findViewById(R.id.roomCreateProgressBar);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String roomDesc = roomDescription.getEditText().getText().toString();
                final String roomNm = roomName.getText().toString();

                if(roomDesc.isEmpty())
                {
                    roomDescription.setError("This field is required");
                    roomDescription.requestFocus();
                    return;
                }

                if(roomNm.isEmpty())
                {
                    roomName.setError("This field is required");
                    roomName.requestFocus();
                    return;
                }


                roomCreateProgressBar.setVisibility(View.VISIBLE);
               checkingRoomNameAvailability(roomNm,roomDesc);


            }
        });

    }
    //checking availability of room name
    public void checkingRoomNameAvailability(final String name, final String desc)
    {
        fStore.collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            boolean avail = true;
                            for (QueryDocumentSnapshot qds : Objects.requireNonNull(task.getResult())) {
                                if (name.equals(qds.getId())) {
                                    avail = false;
                                }
                                Log.i(TAG, name + " " + qds.getId() + " " + String.valueOf(avail));
                            }

                            if (avail) {
                                //now calling create room method......
                                createRoom(name, desc);
                                Log.i(TAG, "Room Available");
                            } else {
                                Toast.makeText(CreateRoomActivity.this, "Room Name not available. Please try another.", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Room Not Available");
                            }

                        }
                        else
                        {
                            Toast.makeText(CreateRoomActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"Error"+task.getException());
                        }

                    }
                });


    }
    //creating room method...................
    public void createRoom(final String name, String desc)
    {
        DocumentReference documentReference = fStore.collection("rooms").document(name);

        Map<String,Object> roomInfo = new HashMap<>();
        roomInfo.put("admin", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        roomInfo.put("description",desc);
        roomInfo.put("roomName",name);

        documentReference.set(roomInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG,"Room Added Successfully");
                Toast.makeText(CreateRoomActivity.this, "Room Created Successfully", Toast.LENGTH_SHORT).show();

                createAdmin(name);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        DocumentReference document = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        //Map<String,Object> userInfo = new HashMap<>();
       // userInfo.put(name,"admin");

        document.update("rooms." + name, "admin").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG,"Room added to user profile successfully");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    //making user as admin of room....
    public void createAdmin(String roomNm)
    {
        roomCreateProgressBar.setVisibility(View.INVISIBLE);
        DocumentReference document = fStore.collection("rooms").document(roomNm);

        document.update("members",FieldValue.arrayUnion(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()));

        Intent intent = new Intent(CreateRoomActivity.this,MainActivity.class);
        finish();
        startActivity(intent);

    }
}