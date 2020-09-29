package com.maruf.is.rex.audiostory.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.maruf.is.rex.audiostory.Fragments.HomeFragment;
import com.maruf.is.rex.audiostory.Fragments.LeftFragment;
import com.maruf.is.rex.audiostory.Fragments.MainFragment;
import com.maruf.is.rex.audiostory.MainActivity;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return new MainFragment();
            case 0:
                return new LeftFragment();
                default:
                    return new MainFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
