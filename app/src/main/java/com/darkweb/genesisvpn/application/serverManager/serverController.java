package com.darkweb.genesisvpn.application.serverManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import com.darkweb.genesisvpn.R;

public class serverController extends AppCompatActivity {

    /*LOCAL VARIABLE DECLARATION*/

    private RecyclerView listView;
    private serverViewController viewController;

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
        serverModel.getInstance().setServerInstance(this);
    }

    public void initializeViews(){
        listView = findViewById(R.id.listview);
        viewController = new serverViewController(this);
    }

    public void initializeList(){
        listAdapter adapter = new listAdapter();
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
    }


    /*EVENT HANDLER*/

    public void onBackPressed(View view)
    {
        serverEventHandler.getInstance().quit();
    }
}
