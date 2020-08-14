package android.example.easyelectionapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRoomsAdapter extends RecyclerView.Adapter<MyRoomsAdapter.MyRoomsViewHolder> {

    private static final String TAG = "Hi1";
    private ArrayList<MyRooms> myRoomsArrayList;

    private OnRoomItemListener monRoomItemListener;




    public MyRoomsAdapter(ArrayList<MyRooms> myRoomsArrayList,OnRoomItemListener onRoomItemListener) {
        this.myRoomsArrayList = myRoomsArrayList;
        this.monRoomItemListener = onRoomItemListener;

    }

    @NonNull
    @Override
    public MyRoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_rooms_cardview,parent,false);

        return new MyRoomsViewHolder(view,monRoomItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRoomsViewHolder holder, int position) {
        MyRooms currentRoom = myRoomsArrayList.get(position);
        holder.myRoomsName.setText(currentRoom.getRoomName());

        Log.i(TAG,currentRoom.getStatus() + currentRoom.getRoomName());

        if(currentRoom.getStatus().equals("admin"))
        {
            holder.adminIdentity.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return myRoomsArrayList.size();
    }

    public static class MyRoomsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView myRoomsName;
        public ImageView adminIdentity;
        OnRoomItemListener onRoomItemListener;
        public MyRoomsViewHolder(@NonNull View itemView,OnRoomItemListener onRoomItemListener1) {
            super(itemView);
            myRoomsName = itemView.findViewById(R.id.roomNameText);
            adminIdentity = itemView.findViewById(R.id.adminIdentity);
           this.onRoomItemListener = onRoomItemListener1;

           itemView.setOnClickListener(this);
           itemView.setOnLongClickListener(this);
        }


       @Override
        public void onClick(View v) {

            onRoomItemListener.OnRoomItemClick(getAdapterPosition());

        }

       @Override
        public boolean onLongClick(View v) {
            onRoomItemListener.OnRoomItemLongClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnRoomItemListener
    {
       void OnRoomItemClick(int position);

       void OnRoomItemLongClick(int position);
    }
}
