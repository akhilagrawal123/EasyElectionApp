package android.example.easyelectionapp;

import android.app.Notification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private OnNotificationItemListener onNotificationItemListener;
    private ArrayList<Notifications> notificationArrayList;

    public NotificationAdapter(OnNotificationItemListener onNotificationItemListener, ArrayList<Notifications> notificationArrayList) {
        this.onNotificationItemListener = onNotificationItemListener;
        this.notificationArrayList = notificationArrayList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notifications_cardview,parent,false);

        return new NotificationViewHolder(view,onNotificationItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notifications currentNotify = notificationArrayList.get(position);

        String senderName = currentNotify.getSenderName();
        String room = currentNotify.getRoomId();

        holder.notifyText.setText(senderName + " has invited you to join his room " + room);


    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView notifyText;
        OnNotificationItemListener onNotificationItemListener;
        public NotificationViewHolder(@NonNull View itemView,OnNotificationItemListener onNotificationItemListener) {
            super(itemView);
            notifyText = itemView.findViewById(R.id.notifyText);
            this.onNotificationItemListener = onNotificationItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);



        }

        @Override
        public void onClick(View v) {

            onNotificationItemListener.OnNotificationItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {

            onNotificationItemListener.OnNotificationItemLongClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnNotificationItemListener
    {
        void OnNotificationItemClick(int position);

        void OnNotificationItemLongClick(int position);
    }
}
