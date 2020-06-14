package com.example.mybank;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mybank.restclient.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    UserDTO user;
    int month;
    Bundle bundle = new Bundle();

    public ViewPagerAdapter(@NonNull Fragment fragment, UserDTO user, int month) {
        super(fragment);
        this.user = user;
        this.month = month;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                IncomeFragment inc = new IncomeFragment();
                bundle.putSerializable("user", user);
                bundle.putInt("month", month);
                inc.setArguments(bundle);
                return inc;
            case 1:
                ExpensesFragment exp = new ExpensesFragment();
                bundle.putSerializable("user", user);
                bundle.putInt("month", month);
                exp.setArguments(bundle);
                return exp;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }










    /*public ViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragmentsTitles.get(position);
    }

    public void addFrament(Fragment fragment, String title){
        fragments.add(fragment);
        fragmentsTitles.add(title);
    }*/
}
