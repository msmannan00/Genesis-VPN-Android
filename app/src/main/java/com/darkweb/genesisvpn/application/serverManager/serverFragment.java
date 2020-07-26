package com.darkweb.genesisvpn.application.serverManager;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class serverFragment extends Fragment {

    public static final String ARGS_KEY = "count";
    public serverFragment() {
    }

    public static serverListModel m_list_model;
    public static ViewPager2 m_pager;
    public static boolean m_type_response;

    public static serverFragment getInstance(int count){
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY, count);

        serverFragment tabFragment = new serverFragment();
        tabFragment.setArguments(args);

        return tabFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.server_view_fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int count = -1;
        if(getArguments() != null){
            count = getArguments().getInt(ARGS_KEY, -1);
        }

        if(count != -1){
            RecyclerView m_list_view = view.findViewById(R.id.server_listview);
            m_list_view.invalidate();
            m_list_view.setLayoutManager(new LinearLayoutManager(sharedControllerManager.getInstance().getHomeController()));
            serverListAdapter adapter;

            if(count == 0){
                adapter = new serverListAdapter(sharedControllerManager.getInstance().getServerController(), m_list_model.getCountryModel(), enums.SERVER.ALL, m_pager, m_type_response);
                m_list_view.setAdapter(adapter);

                if(m_list_model.getCountryModel().size()<=0){
                    m_list_view.setAlpha(0);
                }else {
                    m_list_view.setAlpha(1);
                }
            }
            else if(count == 1){
                adapter = new serverListAdapter(sharedControllerManager.getInstance().getServerController(), m_list_model.getRecentModel(), enums.SERVER.RECENT, m_pager, m_type_response);
                m_list_view.setAdapter(adapter);

                if(status.RECENT_SERVERS.size()<=0){
                    m_list_view.setAlpha(0);
                }else {
                    m_list_view.setAlpha(1);
                }
            }
        }
    }
}
