package android.example.easyelectionapp;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

public class PagerAdapter extends FragmentPagerAdapter {
    private int tabNumber;



    public PagerAdapter(@NonNull FragmentManager fm, int behavior,int tabs) {
        super(fm, behavior);
        this.tabNumber = tabs;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new MyRoomsFragment();
            case 1:
                return new NotificationFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }



}
