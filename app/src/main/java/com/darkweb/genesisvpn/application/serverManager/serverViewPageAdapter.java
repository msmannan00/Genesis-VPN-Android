package com.darkweb.genesisvpn.application.serverManager;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class serverViewPageAdapter extends FragmentStateAdapter {

    public serverViewPageAdapter(@NonNull FragmentActivity fragmentActivity, serverListModel p_list_model) {
        super(fragmentActivity);
        serverFragment.m_list_model = p_list_model;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return serverFragment.getInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
