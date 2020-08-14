package android.example.easyelectionapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private ArrayList<Candidates> candidates;
    private OnCandidateItemListener onCandidateItemListener;

    public CandidateAdapter(ArrayList<Candidates> candidates,OnCandidateItemListener onCandidateItemListener) {
        this.candidates = candidates;
        this.onCandidateItemListener = onCandidateItemListener;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.voting_cardview,parent,false);

        return new CandidateViewHolder(view,onCandidateItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidates current = candidates.get(position);
        holder.name.setText(current.getName());

    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener {
        public TextView name;
        OnCandidateItemListener onCandidateItemListener;

        public CandidateViewHolder(@NonNull View itemView,OnCandidateItemListener onCandidateItemListener) {
            super(itemView);
            name = itemView.findViewById(R.id.candidateNameText);
            this.onCandidateItemListener = onCandidateItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onCandidateItemListener.OnCandidateItemClick(getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            onCandidateItemListener.OnCandidateItemLongClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnCandidateItemListener
    {
        void OnCandidateItemClick(int position);

        void OnCandidateItemLongClick(int position);
    }
}
