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

public class OngoingAdapter extends RecyclerView.Adapter<OngoingAdapter.OngoingViewHolder> {

    private static final String TAG = "Ongoing Adapter";
    private ArrayList<Elections> elections;
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");

    private OnOngoingElectionItemListener onOngoingElectionItemListener;

    public OngoingAdapter(ArrayList<Elections> elections, OnOngoingElectionItemListener onOngoingElectionItemListener) {
        this.elections = elections;
        this.onOngoingElectionItemListener = onOngoingElectionItemListener;
    }

    @NonNull
    @Override
    public OngoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.election_cardview,parent,false);

        return new OngoingViewHolder(view,onOngoingElectionItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingViewHolder holder, int position) {
        Elections current = elections.get(position);
        holder.title.setText(current.getTitle());
        holder.startTime.setText(getDateFromString(current.getStartTime()).toString());
        holder.endTime.setText(getDateFromString(current.getEndTime()).toString());

    }

    @Override
    public int getItemCount() {
        return elections.size();
    }

    public static class OngoingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public TextView title;
        public TextView startTime;
        public TextView endTime;
        OnOngoingElectionItemListener onOngoingElectionItemListener;
        public OngoingViewHolder(@NonNull View itemView, OnOngoingElectionItemListener onOngoingElectionItemListener) {
            super(itemView);
            title = itemView.findViewById(R.id.displayTitle);
            startTime = itemView.findViewById(R.id.displayStartTime);
            endTime = itemView.findViewById(R.id.displayEndTime);

            this.onOngoingElectionItemListener = onOngoingElectionItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            onOngoingElectionItemListener.OnOngoingElectionItemClick(getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {

            onOngoingElectionItemListener.OnOngoingElectionItemLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnOngoingElectionItemListener
    {
        void OnOngoingElectionItemClick(int position);

        void OnOngoingElectionItemLongClick(int position);
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
