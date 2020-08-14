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

public class PastAdapter extends RecyclerView.Adapter<PastAdapter.PastViewHolder> {

    private static final String TAG = "Past Adapter";
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");
    private ArrayList<Elections> elections;
    private OnPastElectionItemListener onPastElectionItemListener;

    public PastAdapter(ArrayList<Elections> elections, OnPastElectionItemListener onPastElectionItemListener) {
        this.elections = elections;
        this.onPastElectionItemListener = onPastElectionItemListener;
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.election_cardview,parent,false);

        return new PastViewHolder(view,onPastElectionItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PastViewHolder holder, int position) {
        Elections current = elections.get(position);
        holder.title.setText(current.getTitle());
        holder.startTime.setText(getDateFromString(current.getStartTime()).toString());
        holder.endTime.setText(getDateFromString(current.getEndTime()).toString());

    }

    @Override
    public int getItemCount() {
        return elections.size();
    }

    public static class PastViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener{
        public TextView title;
        public TextView startTime;
        public TextView endTime;
        OnPastElectionItemListener onPastElectionItemListener;
        public PastViewHolder(@NonNull View itemView,OnPastElectionItemListener onPastElectionItemListener) {
            super(itemView);
            title = itemView.findViewById(R.id.displayTitle);
            startTime = itemView.findViewById(R.id.displayStartTime);
            endTime = itemView.findViewById(R.id.displayEndTime);
            this.onPastElectionItemListener = onPastElectionItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


        }

        @Override
        public void onClick(View v) {

            onPastElectionItemListener.OnPastElectionItemClick(getAdapterPosition());


        }

        @Override
        public boolean onLongClick(View v) {

            onPastElectionItemListener.OnPastElectionItemLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnPastElectionItemListener
    {
        void OnPastElectionItemClick(int position);

        void OnPastElectionItemLongClick(int position);
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
