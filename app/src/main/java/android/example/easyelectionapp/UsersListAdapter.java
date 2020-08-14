package android.example.easyelectionapp;

import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserListViewHolder> implements Filterable {

    ArrayList<AllUsers> allUsersArrayList;
    ArrayList<AllUsers> exampleAllUsersArrayList;

    private OnUserItemListener onUserItemListener;

    public UsersListAdapter(ArrayList<AllUsers> allUsersArrayList, OnUserItemListener onUserItemListener) {
        this.allUsersArrayList = allUsersArrayList;
        exampleAllUsersArrayList = new ArrayList<>(allUsersArrayList);
        this.onUserItemListener = onUserItemListener;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_userslist,parent,false);

        return new UserListViewHolder(view,onUserItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        AllUsers currentUser = allUsersArrayList.get(position);
        holder.username.setText(currentUser.getUserName());
        if(currentUser.getProfilePic() != null) {
            Picasso.get().load(Uri.parse(currentUser.getProfilePic())).into(holder.userImage);
        }



    }

    @Override
    public int getItemCount() {
        return allUsersArrayList.size();
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView username;
        public ImageView userImage;
        OnUserItemListener onUserItemListener;
        public UserListViewHolder(@NonNull View itemView, OnUserItemListener onUserItemListener) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.userNameInUserList);
            userImage = (ImageView) itemView.findViewById(R.id.profilePicInUserList);
            this.onUserItemListener = onUserItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            onUserItemListener.OnUserItemClick(getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {

            onUserItemListener.OnUserItemLongClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnUserItemListener
    {
        void OnUserItemClick(int position);

       void OnUserItemLongClick(int position);
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<AllUsers> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(exampleAllUsersArrayList);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(AllUsers user : exampleAllUsersArrayList)
                {
                    if(user.getUserName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allUsersArrayList.clear();
            allUsersArrayList.addAll((ArrayList) results.values);
            notifyDataSetChanged();

        }
    };
}
