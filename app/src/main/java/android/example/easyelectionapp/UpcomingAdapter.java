package android.example.easyelectionapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder> {

    private static final String TAG = "Upcoming Adapter";
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");
    private ArrayList<Elections> elections;
    private OnUpcomingElectionItemListener onUpcomingElectionItemListener;

    public UpcomingAdapter(ArrayList<Elections> elections,OnUpcomingElectionItemListener onUpcomingElectionItemListener) {
        this.elections = elections;
        this.onUpcomingElectionItemListener = onUpcomingElectionItemListener;
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.election_cardview,parent,false);

        return new UpcomingViewHolder(view,onUpcomingElectionItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        Elections current = elections.get(position);
        holder.title.setText(current.getTitle());
        holder.startTime.setText(getDateFromString(current.getStartTime()).toString());
        holder.endTime.setText(getDateFromString(current.getEndTime()).toString());

    }

    @Override
    public int getItemCount() {
        return elections.size();
    }

    public static class UpcomingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public TextView title;
        public TextView startTime;
        public TextView endTime;
        OnUpcomingElectionItemListener onUpcomingElectionItemListener;
        public UpcomingViewHolder(@NonNull View itemView,OnUpcomingElectionItemListener onUpcomingElectionItemListener) {
            super(itemView);
            title = itemView.findViewById(R.id.displayTitle);
            startTime = itemView.findViewById(R.id.displayStartTime);
            endTime = itemView.findViewById(R.id.displayEndTime);
            this.onUpcomingElectionItemListener = onUpcomingElectionItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUpcomingElectionItemListener.OnUpcomingElectionItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onUpcomingElectionItemListener.OnUpcomingElectionItemLongClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnUpcomingElectionItemListener
    {
        void OnUpcomingElectionItemClick(int position);

        void OnUpcomingElectionItemLongClick(int position);
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
}
