package android.example.easyelectionapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private ArrayList<Result> results;

    public ResultAdapter(ArrayList<Result> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.result_cardview,parent,false);

        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {

        Result current = results.get(position);
        holder.name.setText(current.getName());
        holder.vote.setText(current.getVote());

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView vote;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.resultName);
            vote = itemView.findViewById(R.id.resultVote);
        }
    }
}
