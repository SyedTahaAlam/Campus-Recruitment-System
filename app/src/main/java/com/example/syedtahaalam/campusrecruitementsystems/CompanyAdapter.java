package com.example.syedtahaalam.campusrecruitementsystems;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CompanyAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public CompanyAdapter(Context context,FragmentManager fm) {

        super(fm);
        mContext = context;
    }


    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0){

            return new StudentsFragment();
        }
        else if (position==1){
            return new CandidatesFragment();
        }
        else{
            return new PostJobFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position==0){

            return mContext.getString(R.string.students);
        }
        else if (position==1){
            return mContext.getString(R.string.candidates);
        }
        else{
            return mContext.getString(R.string.post_job);
        }

    }
}
