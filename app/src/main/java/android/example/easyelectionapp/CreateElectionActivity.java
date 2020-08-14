package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateElectionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "Hi5";
    String roomName;
    EditText startTime,endTime,dateText,titleEditText;
    TextInputLayout purposeEditText;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Button nextBtn;
    Toolbar toolbar;
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");


    public boolean res = true;
    ImageView sTime,eTime,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_election);

        //seting up intent.....
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        assert roomName != null;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);








        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        titleEditText = (EditText) findViewById(R.id.electionTitleEditText);
        purposeEditText = (TextInputLayout) findViewById(R.id.PurposeElectionEditText);
        dateText = (EditText) findViewById(R.id.editTextDate);
        startTime = (EditText) findViewById(R.id.editTextStartTime);
        endTime = (EditText) findViewById(R.id.editTextEndTime);



       date =  (ImageView) findViewById(R.id.dateImage);
       date.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DialogFragment datePicker = new DatePickerFragment();
               datePicker.show(getSupportFragmentManager(), "date picker");

           }
       });

       sTime = (ImageView) findViewById(R.id.timeImage1);
       sTime.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DialogFragment timePicker = new TimePickerFragment();
               timePicker.show(getSupportFragmentManager(),"time picker");
               res = false;
           }
       });

        eTime = (ImageView) findViewById(R.id.timeImage2);
        eTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
                res = true;
            }
        });


        nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = dateText.getText().toString();
                String start = startTime.getText().toString();
                String end = endTime.getText().toString();
                String title = titleEditText.getText().toString();
                String purpose = purposeEditText.getEditText().getText().toString();


                if(date.isEmpty())
                {
                    dateText.setError("Valid date is required");
                    dateText.requestFocus();
                    return;

                }
                if(start.isEmpty())
                {
                    startTime.setError("Valid time is required");
                    startTime.requestFocus();
                    return;

                }
                if(end.isEmpty())
                {
                    endTime.setError("Valid time is required");
                    endTime.requestFocus();
                    return;

                }
                if(title.isEmpty())
                {
                    titleEditText.setError("Enter this field");
                    titleEditText.requestFocus();
                    return;

                }
                if(purpose.isEmpty())
                {
                    purposeEditText.setError("Enter this field");
                    purposeEditText.requestFocus();
                    return;

                }

                String date1 = date + "T" + start;
                String date2 = date  + "T" + end;
                Log.i(TAG,date1);
                Log.i(TAG,date2);


                if(getDateFromString(date1).compareTo(getDateFromString(date2)) >= 0)
                {
                    Toast.makeText(CreateElectionActivity.this, "Start time must be less than end time.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"start time greater than end time.");
                }
                else {
                    //calling a method to store data in firebase.......
                    loadingElectionDetails(title, purpose, date1, date2);
                }

            }
        });


    }

    //simple date format method to get date..................
    public Date getDateFromString(String datetoSaved){

        try {
            Log.i(TAG, Objects.requireNonNull(format.parse(datetoSaved)).toString());
            return format.parse(datetoSaved);

        } catch (ParseException e){
            return null ;
        }

    }


    //loading election detials to firebase....
    public void loadingElectionDetails(String title, String purpose, String time1, String time2)
    {
        final DocumentReference docRef = fStore.collection("election").document();

        Map<String,Object> electionInfo = new HashMap<>();
        electionInfo.put("ownerId", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        electionInfo.put("roomId",roomName);
        electionInfo.put("start",time1);
        electionInfo.put("end",time2);
        electionInfo.put("title",title);
        electionInfo.put("purpose",purpose);
        electionInfo.put("id",docRef.getId());
        electionInfo.put("None",0);


        Log.i(TAG,roomName + " " + time1 + " " + time2 + " " + title + " " + purpose + " " + docRef.getId().toString());
        docRef.set(electionInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateElectionActivity.this, "Election Created Successfully", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"Election done");
                docRef.update("candidates", FieldValue.arrayUnion("None"));


                Intent intent = new Intent(CreateElectionActivity.this,AddCandidatesActivity.class);
                intent.putExtra("id",docRef.getId());
                intent.putExtra("roomName",roomName);
                finish();
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"Some Error : " + e.getMessage());
                Toast.makeText(CreateElectionActivity.this, "Some Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month+1);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        Log.i("Date",String.valueOf(year) + " " + String.valueOf(month+1) + " " + String.valueOf(dayOfMonth) );

        String currentDate = String.format("%02d/%02d/%d", dayOfMonth, month+1, year);

        dateText.setText(currentDate);


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if(!res) {
            startTime = (EditText) findViewById(R.id.editTextStartTime);
            String currentTime = String.format("%02d:%02d",hourOfDay ,minute);
            startTime.setText(currentTime);
        }
        else {
            endTime = (EditText) findViewById(R.id.editTextEndTime);
            String currentTime = String.format("%02d:%02d",hourOfDay ,minute);
            endTime.setText(currentTime);
        }
    }
}