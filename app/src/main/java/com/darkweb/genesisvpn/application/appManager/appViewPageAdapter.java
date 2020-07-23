package com.darkweb.genesisvpn.application.appManager;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class appViewPageAdapter extends FragmentStateAdapter {

    public appViewPageAdapter(@NonNull FragmentActivity fragmentActivity, appListModel p_list_model) {
        super(fragmentActivity);
        appFragment.m_list_model = p_list_model;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return appFragment.getInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
