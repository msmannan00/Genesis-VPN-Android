package com.darkweb.genesisvpn.application.aboutManager;

import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import java.util.List;

public class aboutController extends Fragment {

    /* PRIVATE VARIABLES */

    private aboutViewController m_view_controller;
    private aboutModel m_model;
    private View.OnClickListener m_on_click_listener;

    /* PRIVATE VIEWS */

    private ImageButton m_back_navigation;

    /* INITIALIZATION */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.about_view, null);
        initializeModel();
        initializeViews(root);
        initializeClickListener();
        initializeListeners();
        return root;
    }

    public void initializeViews(View p_root){
        m_back_navigation = p_root.findViewById(R.id.m_back_navigation);
        m_view_controller = new aboutViewController(getActivity(), new aboutViewCallback(), m_back_navigation);

    }

    public void initializeListeners(){
        m_back_navigation.setOnClickListener(m_on_click_listener);
    }

    public void initializeClickListener(){
        m_on_click_listener = v -> {
            if(v.getId()==R.id.m_back_navigation){
                m_model.quit();
            }
        };
    }

    public void initializeModel(){
        m_model = new aboutModel(getActivity(), new aboutModelCallback());
    }

    /*LAYOUT EVENT HANDLER*/

    public boolean onBackPressed()
    {
        return true;
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class aboutModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                getActivity().onBackPressed();
            }
        }
    }

    public class aboutViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }
}
