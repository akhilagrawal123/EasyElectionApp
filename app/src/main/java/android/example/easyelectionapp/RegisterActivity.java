package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "TAG" ;



    FirebaseAuth mAuth;
    Button nextBtn,registerBtn;

    EditText phone, codeEnter,emailId;
    TextInputLayout password;
    CountryCodePicker codePicker;
    ProgressBar progressBar,registerProgressBar,otpEnteredProgressBar;
    TextView state,resendOtpBtn;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean verificationInProgress = false;
    Boolean phoneVerifiedSuccessfully = false;
    FirebaseFirestore fStore;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        phone = (EditText) findViewById(R.id.phone);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        state = (TextView) findViewById(R.id.state);
        codeEnter = (EditText) findViewById(R.id.codeEnter);
        codePicker = findViewById(R.id.ccp);
        emailId = (EditText) findViewById(R.id.emailId);
        password = (TextInputLayout) findViewById(R.id.password);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerProgressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        resendOtpBtn = (TextView)findViewById(R.id.resendOtpBtn);
        otpEnteredProgressBar = (ProgressBar) findViewById(R.id.otpEnteredProgressBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(phoneVerifiedSuccessfully)
                {
                    final String email = emailId.getText().toString();
                    String pass = password.getEditText().getText().toString();
                    final String mobNum = phone.getText().toString();

                    if(email.isEmpty())
                    {
                        emailId.setError("Enter a valid email");
                        emailId.requestFocus();
                        return;
                    }
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        emailId.setError("Enter a valid email");
                    }
                    if(pass.isEmpty())
                    {
                        password.setError("This field must be filled");
                        password.requestFocus();
                        return;
                    }
                    if(pass.length()<6)
                    {
                        password.setError("Password too short");
                    }

                    registerProgressBar.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            registerProgressBar.setVisibility(View.INVISIBLE);

                            if(task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();

                                assert user != null;
                                userId = user.getUid();

                                String username = "FullName";

                                final DocumentReference documentReference = fStore.collection("users").document(userId);
                                Map<String,Object> userInfo = new HashMap<>();
                                userInfo.put("email",email);
                                userInfo.put("phone",mobNum);
                                userInfo.put("username",username);
                                userInfo.put("uid",userId);

                                documentReference.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Log.i("OnSuccess","User profile created of user" + userId);
                                        documentReference.update("votes", FieldValue.arrayUnion(userId));

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.i("OnFailure","User profile not created of user" + userId);

                                    }
                                });

                                //send verification email..........
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(RegisterActivity.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this,ProfileActivity.class);
                                            intent.putExtra("result","close");
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finish();
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this, "Error occurred!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "Error" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Phone number not verified", Toast.LENGTH_SHORT).show();
                }

            }
        });



        nextBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if(!verificationInProgress)
                {
                    if(!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10)
                    {
                        String phoneNum = "+" + codePicker.getSelectedCountryCode() + phone.getText().toString();
                        Log.d(TAG, "onClick: phoneNumber " + phoneNum);
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending Otp...");
                        state.setVisibility(View.VISIBLE);
                        requestOtp(phoneNum);
                    }
                    else
                    {
                        phone.setError("Phone number is not valid");
                    }
                }
                else
                {
                    String userOtp = codeEnter.getText().toString();
                    if(!userOtp.isEmpty() && userOtp.length() == 6)
                    {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,userOtp);
                        verifyAuth(credential);
                    }
                    else
                    {
                        codeEnter.setError("Enter a valid OTP");
                    }
                }
            }
        });
    }

    private void verifyAuth(PhoneAuthCredential credential) {

        otpEnteredProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(RegisterActivity.this, "Phone is verified successfully", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    phoneVerifiedSuccessfully = true;
                    otpEnteredProgressBar.setVisibility(View.INVISIBLE);
                    nextBtn.setEnabled(false);
                    nextBtn.setText("Done");
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void requestOtp(String phoneNum) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                codeEnter.setVisibility(View.VISIBLE);
                verificationId = s;
                token = forceResendingToken;
                nextBtn.setText("Verify");
                verificationInProgress = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(RegisterActivity.this, "Otp expired! Re - Request the Otp.", Toast.LENGTH_SHORT).show();
                verificationInProgress = false;
                resendOtpBtn.setVisibility(View.VISIBLE);
                resendOtpBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(!verificationInProgress)
                        {
                            if(!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10)
                            {
                                String phoneNum = "+" + codePicker.getSelectedCountryCode() + phone.getText().toString();
                                Log.d(TAG, "onClick: phoneNumber " + phoneNum);
                                progressBar.setVisibility(View.VISIBLE);
                                state.setText("Sending Otp...");
                                state.setVisibility(View.VISIBLE);
                                requestOtp(phoneNum);
                            }
                            else
                            {
                                phone.setError("Phone number is not valid");
                            }
                        }
                        else
                        {
                            String userOtp = codeEnter.getText().toString();
                            if(!userOtp.isEmpty() && userOtp.length() == 6)
                            {
                                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,userOtp);
                                verifyAuth(credential);
                                resendOtpBtn.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                codeEnter.setError("Enter a valid OTP");
                            }
                        }

                    }
                });
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(RegisterActivity.this, "Cannot Create Account " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}