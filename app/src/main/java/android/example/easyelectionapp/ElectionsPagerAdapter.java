package android.example.easyelectionapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class ElectionsPagerAdapter extends FragmentStateAdapter {

    public static final int Ongoing_Type = 0;
    public static final int Past_Type = 2;
    public static final int Upcoming_Type = 1;
    private static final String TAG = "Pager Adapter" ;
    static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");

    private ArrayList<Elections> elections, ongoingElection, pastElection, upcomingElection;
    private Context context;

    public ElectionsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Elections> elections, Context context) {
        super(fragmentManager, lifecycle);
        this.elections = elections;
        this.context = context;

        Log.i(TAG,"Reached successfully : " + elections.size());

        ongoingElection = new ArrayList<>();
        pastElection = new ArrayList<>();
        upcomingElection = new ArrayList<>();

        //getting current time and date from server format.........

        format.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        Log.i(TAG,format.format(new Date()).toString());

        for(Elections election : this.elections)
        {
            if(getDateFromString(election.getStartTime()).compareTo(getDateFromString(format.format(new Date()))) > 0)
            {
               upcomingElection.add(election);
               Log.i(TAG, "Upcoming : found 1" );
            }
            if(getDateFromString(election.getEndTime()).compareTo(getDateFromString(format.format(new Date()))) < 0)
            {
                pastElection.add(election);
                Log.i(TAG,"Past : found 1");
            }
            if(getDateFromString(election.getStartTime()).compareTo(getDateFromString(format.format(new Date()))) <= 0 && getDateFromString(election.getEndTime()).compareTo(getDateFromString(format.format(new Date()))) >= 0)
            {
                ongoingElection.add(election);
                Log.i(TAG,"Ongoing : found 1");
            }
        }


    }



    @Override

    public int getItemCount() {
        return 3;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case Ongoing_Type:
                return new OngoingFragment(Ongoing_Type,ongoingElection,context);
            case Past_Type:
                return new PastFragment(Past_Type,pastElection,context);
            case Upcoming_Type:
                return new UpcomingFragment(Upcoming_Type,upcomingElection,context);
            default:
                return null;
        }
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


