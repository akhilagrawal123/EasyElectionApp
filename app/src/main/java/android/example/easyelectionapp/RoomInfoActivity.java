package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RoomInfoActivity extends AppCompatActivity {

    private static final String TAG = "Hi5";
    public String roomName;
    TextView name,desc;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        name = (TextView) findViewById(R.id.nameTxt);
        desc = (TextView) findViewById(R.id.descTxt);

        name.setText(roomName);

        //fetching room description from firebase......
        DocumentReference docRef = fStore.collection("rooms").document(roomName);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists() && documentSnapshot.getString("description") != null) {
                    Log.i(TAG, Objects.requireNonNull(documentSnapshot.getString("description")));
                    desc.setText(documentSnapshot.getString("description"));
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RoomInfoActivity.this, "Some Error : " + e.getMessage() , Toast.LENGTH_SHORT).show();
                        Log.i(TAG,"Error " + e.getMessage());

                    }
                });



    }
}