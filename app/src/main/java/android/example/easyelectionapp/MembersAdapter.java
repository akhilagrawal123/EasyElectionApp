package android.example.easyelectionapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {

    ArrayList<Members> membersArrayList;


    public MembersAdapter(ArrayList<Members> membersArrayList) {
        this.membersArrayList = membersArrayList;
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_members,parent,false);

        return new MembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {

        Members currentMember = membersArrayList.get(position);
        holder.username.setText(currentMember.getName());
        if(currentMember.getProfilePic() != null)
        {
            Picasso.get().load(Uri.parse(currentMember.getProfilePic())).into(holder.profile);
        }

    }

    @Override
    public int getItemCount() {
        return membersArrayList.size();
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile;
        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.userNameInMemberList);
            profile = (ImageView) itemView.findViewById(R.id.profilePicInMemberList);

        }
    }
}
