package com.darkweb.genesisvpn.application.appManager;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.constants;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class appFragment extends Fragment {

    public static final String ARGS_KEY = "count";
    public appFragment() {
    }

    public static appListModel m_list_model;
    public static appFragment getInstance(int count){
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY, count);

        appFragment tabFragment = new appFragment();
        tabFragment.setArguments(args);

        return tabFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_view_fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int count = -1;
        if(getArguments() != null){
            count = getArguments().getInt(ARGS_KEY, -1);
        }

        if(count != -1){
            RecyclerView m_list_view = view.findViewById(R.id.app_listview);
            m_list_view.invalidate();
            m_list_view.setLayoutManager(new LinearLayoutManager(sharedControllerManager.getInstance().getHomeController()));
            appListAdapter adapter;

            if(count == 0){
                adapter = new appListAdapter(sharedControllerManager.getInstance().getHomeController(), m_list_model.getModel(), constants.SYSTEM_APPS);
                m_list_view.setAdapter(adapter);

                if(constants.SYSTEM_APPS.size()<=0){
                    m_list_view.setAlpha(0);
                }else {
                    m_list_view.setAlpha(1);
                }
            }
            else if(count == 1){
                adapter = new appListAdapter(sharedControllerManager.getInstance().getHomeController(), m_list_model.getModel(), constants.INSTALLED_APPS);
                m_list_view.setAdapter(adapter);

                if(constants.INSTALLED_APPS.size()<=0){
                    m_list_view.setAlpha(0);
                }else {
                    m_list_view.setAlpha(1);
                }
            }
            else if(count == 2){
                adapter = new appListAdapter(sharedControllerManager.getInstance().getHomeController(), m_list_model.getModel(), constants.RECENT_LOCATION);
                m_list_view.setAdapter(adapter);

                if(constants.RECENT_LOCATION.size()<=0){
                    m_list_view.setAlpha(0);
                }else {
                    m_list_view.setAlpha(1);
                }
            }
        }
    }
}
