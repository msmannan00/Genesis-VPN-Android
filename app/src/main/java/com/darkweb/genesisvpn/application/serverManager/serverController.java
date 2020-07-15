package com.darkweb.genesisvpn.application.serverManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import java.util.List;

public class serverController extends AppCompatActivity {

    /*LOCAL VARIABLE DECLARATION*/

    private RecyclerView m_list_view;
    private serverViewController m_view_controller;
    private serverModel m_model;

    /*INITIALIZATION*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_view);
        initializeModel();
        initializeViews();
        initializeList();
    }

    public void initializeModel(){
        m_model = new serverModel(this, new serverModelCallback());
        m_view_controller = new serverViewController(this, new serverViewCallback());
    }

    public void initializeViews(){
        m_list_view = findViewById(R.id.listview);
    }

    public void initializeList(){
        listAdapter adapter = new listAdapter(this);
        m_list_view.setAdapter(adapter);
        m_list_view.setLayoutManager(new LinearLayoutManager(this));
    }


    /*EVENT HANDLER*/

    public void onBackPressed(View view)
    {
        m_model.quit();
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class serverModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

    public class serverViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

}
