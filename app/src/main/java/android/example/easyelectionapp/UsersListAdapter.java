package android.example.easyelectionapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

public class UsersListAdapter extends FirestoreRecyclerAdapter {

    class UsersListHolder extends RecyclerView.ViewHolder {

        TextView userName;
        ImageView profilepic;

        public UsersListHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            ImageView = itemView.findViewById(R.id.profilePic);
        }
    }
}
