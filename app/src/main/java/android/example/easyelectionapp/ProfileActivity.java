package android.example.easyelectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements update_username_custom.UpdateUsernameListner{

    TextView verifyEmail;
    Button verifyBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ImageView profileImage,changeImage,updateUsername;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    TextView pName,pEmail,pPhone;
    Toolbar toolbar;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Profile");


        verifyEmail = (TextView) findViewById(R.id.verifyText);
        verifyBtn  = (Button) findViewById(R.id.verifyBtn);
        profileImage  = (ImageView) findViewById(R.id.profile);
        changeImage = (ImageView) findViewById(R.id.changeimage);
        updateUsername = (ImageView) findViewById(R.id.changeName);

        updateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        StorageReference profileRef = storageReference.child("users/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        assert user != null;
        if(!user.isEmailVerified())
        {
            verifyEmail.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ProfileActivity.this, "Verification email has been sent again", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "Error occurred!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            });
        }



        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening gallery ........
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        pName = (TextView) findViewById(R.id.fullName);
        pEmail = (TextView) findViewById(R.id.emailProfile);
        pPhone = (TextView) findViewById(R.id.phoneProfile);

        fStore = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = fStore.collection("users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {
                    pEmail.setText(documentSnapshot.getString("email"));
                    pPhone.setText(documentSnapshot.getString("phone"));
                    pName.setText(documentSnapshot.getString("username"));
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);

                //uploading image......
                uploadImageToFirebase(imageUri);

            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {

        //upload image to firebase storage...........

        final StorageReference fileRef = storageReference.child("users/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       Picasso.get().load(uri).into(profileImage);

                       DocumentReference documentReference = fStore.collection("users").document(mAuth.getCurrentUser().getUid());
                       Map<String,Object> userInfo = new HashMap<>();
                       userInfo.put("imagepath",fileRef.getDownloadUrl().toString());

                       Log.i("ImagePath",fileRef.getDownloadUrl().toString());

                       documentReference.update(userInfo);

                   }
               });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.logoutBtn)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this,loginActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.resetPasswordBtn);
        {
            Toast.makeText(this, "Not Set Yet!!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialog()
    {
        update_username_custom dialogBox = new update_username_custom();
        dialogBox.show(getSupportFragmentManager(),"Update dialog");
    }


    @Override
    public void updateUsername(String newUsername) {


        DocumentReference documentReference = fStore.collection("users").document(mAuth.getCurrentUser().getUid());
        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("username",newUsername);

        Log.i("usernmae",newUsername);

        documentReference.update(userInfo);




    }
}