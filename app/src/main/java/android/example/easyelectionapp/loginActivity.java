package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.Objects;

public class loginActivity extends AppCompatActivity {

    EditText email,password;
    Button loginBtn;
    TextView signUpTxt;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.emailIdLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        mAuth = FirebaseAuth.getInstance();

        loginBtn = (Button) findViewById(R.id.loginBtn);
        signUpTxt = (TextView) findViewById(R.id.signUpText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAdd = email.getText().toString();
                String pass = password.getText().toString();



                if(emailAdd.isEmpty())
                {
                    email.setError("Enter a valid email address");
                    email.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches())
                {
                    email.setError("Enter a valid email address");
                }

                if(pass.isEmpty())
                {
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }

                if(pass.length() < 6 )
                {
                    password.setError("Password incorrect");
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(emailAdd,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if(task.isSuccessful())
                        {
                            Toast.makeText(loginActivity.this, "User Successfully Login In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(loginActivity.this,ProfileActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(loginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });



            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(loginActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
    }
}