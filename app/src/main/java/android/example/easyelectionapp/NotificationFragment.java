package android.example.easyelectionapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationItemListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Hi2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    //declaring variables................
    View view;
    RecyclerView notificationsRecyclerView;
    ArrayList<Notifications> notificationsArrayList;
    NotificationAdapter notificationAdapter;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    ProgressBar notifyProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_notification, container, false);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        notifyProgressBar = view.findViewById(R.id.notifyProgressBar);

        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setHasFixedSize(true);

        showNotifications();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    //fetching notifications from firebase and display it to recycler view
    public void showNotifications()
    {
        notificationsArrayList = new ArrayList<>();
        fStore.collection("notifications")
                .whereEqualTo("to",mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            if(task.getResult() != null) {
                                for (QueryDocumentSnapshot qds : Objects.requireNonNull(task.getResult())) {

                                    Log.i(TAG,qds.getString("room")+ " " + qds.getString("from")+ " " + qds.getString("senderName") + " " + qds.getId());

                                    notificationsArrayList.add(new Notifications(qds.getString("room"),qds.getString("from"),qds.getString("senderName"),qds.getId()));

                                }

                                notificationAdapter = new NotificationAdapter((NotificationAdapter.OnNotificationItemListener)NotificationFragment.this, notificationsArrayList);
                                notificationAdapter.notifyDataSetChanged();
                                notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                notificationsRecyclerView.setAdapter(notificationAdapter);
                            }
                        }
                        else
                        {
                            Log.i(TAG,"Some Error : " + task.getException());
                            Toast.makeText(view.getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void OnNotificationItemClick(int position) {

    }

    @Override
    public void OnNotificationItemLongClick(int position) {

        Notifications currentNotification = notificationsArrayList.get(position);
        final String id = currentNotification.getId();
        final String roomName = currentNotification.getRoomId();

        new AlertDialog.Builder(view.getContext())
                .setTitle("Confirmation Alert")
                .setMessage("Are you sure you want to accept this invitation.")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG,"Invitation Accepted");

                        //firstly calling a method to make the user a member of invited room...........
                        notifyProgressBar.setVisibility(View.VISIBLE);
                        creatingUserMemberOfRoom(roomName,id);

                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(view.getContext(), "You Have Declined the invitation", Toast.LENGTH_SHORT).show();
                        Log.i(TAG,"Invitation Declined");

                        //directly calling a method to delete the notification as it has been responded...
                        notifyProgressBar.setVisibility(View.VISIBLE);
                        deleteNotification(id);


                    }
                })
                .show();

    }


    //creating member of room...........
    public void creatingUserMemberOfRoom(final String roomName, final String id)
    {
       DocumentReference docRef = fStore.collection("rooms").document(roomName);

       docRef.update("members", FieldValue.arrayUnion(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()))
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {

                       if(task.isSuccessful())
                       {
                           Log.i(TAG,"User successfully added to " + roomName);
                           Toast.makeText(view.getContext(), "Now you become a member of " + roomName, Toast.LENGTH_SHORT).show();

                           //secondly calling a method to update the room details to the user profile..........
                           addingRoomToUserProfile(roomName,id);

                       }
                       else
                       {
                           Log.i(TAG,"User not added to " + roomName + task.getException());
                           Toast.makeText(view.getContext(), "Some Error : " +task.getException(), Toast.LENGTH_SHORT).show();
                       }

                   }
               });

    }

    public void addingRoomToUserProfile(String roomName, final String id)
    {
        DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        documentReference.update("rooms." + roomName, "member")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.i(TAG,"Room added to user profile successfully");

                //at last deleting the notification as this has been responded.........
                  deleteNotification(id);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"Error : " + e.getMessage());

            }
        });
    }


    //method to delete notification once it has been responded............
    public void deleteNotification(String id)
    {
       fStore.collection("notifications").document(id)
               .delete()
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Log.i(TAG,"Notification Deleted Successfully");
                       notifyProgressBar.setVisibility(View.INVISIBLE);
                      // Toast.makeText(view.getContext(), "Notification Deleted Successfully", Toast.LENGTH_SHORT).show();
                       showNotifications();
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.i(TAG,"Error : " + e.getMessage());
               Toast.makeText(view.getContext(), "Some Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
               notifyProgressBar.setVisibility(View.INVISIBLE);
           }
       });
    }


}