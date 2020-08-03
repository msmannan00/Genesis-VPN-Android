package com.darkweb.genesisvpn.application.serverManager;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import com.jwang123.flagkit.FlagKit;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class serverListAdapter extends RecyclerView.Adapter<serverListAdapter.listViewHolder>
{
    private AppCompatActivity m_context;
    private ArrayList<serverListRowModel> m_server_model = new ArrayList<>();
    private enums.SERVER m_type;
    private ArrayList<serverListRowModel> m_server_model_async;
    private ViewPager2 m_pager;
    public static boolean m_type_response;
    boolean isLoaded = false;

    serverListAdapter(AppCompatActivity p_context, ArrayList<serverListRowModel> p_server_model,enums.SERVER p_type, ViewPager2 p_pager, boolean p_response_type) {
        this.m_context = p_context;
        m_type = p_type;
        m_pager = p_pager;
        m_server_model_async = p_server_model;
        m_type_response = p_response_type;
        updateList(new ArrayList<>());
        updateAsync();
    }

    public void updateAsync(){
        m_pager.setVisibility(View.INVISIBLE);;
        new Thread(){
            public void run(){
                try {
                    sleep(400);
                    for(int counter=0;counter<m_server_model_async.size();counter++){
                        int finalCounter = counter;
                        m_context.runOnUiThread(() -> new Handler().postDelayed(() -> {
                            m_server_model.add(m_server_model_async.get(finalCounter));
                            serverListAdapter.this.notifyItemRangeChanged(finalCounter, 1);
                        },(long) 0));

                        if(!isLoaded && counter==20 && m_pager.getVisibility() == View.INVISIBLE ){
                            isLoaded = true;
                            m_context.runOnUiThread(() -> new Handler().postDelayed(() -> {
                                m_pager.setVisibility(View.VISIBLE);
                                m_pager.animate().cancel();
                                m_pager.setAlpha(0);
                                m_pager.animate().setDuration(250).alpha(1);
                            },(long) 400));
                        }
                        sleep(0);
                    }
                    if(!m_type_response){
                        serverListRowModel m_auto_model = new serverListRowModel("Auto Best Location", strings.CS_COUNTRY_CODE + "Auto" + strings.LINE_BREAK + strings.CS_RELAY_SERVERS + "Optimal",null,null);
                        m_server_model.add(0, m_auto_model);
                    }
                    if(!isLoaded){
                        m_pager.setAlpha(0);
                        m_context.runOnUiThread(() -> new Handler().postDelayed(() -> {
                            m_pager.setVisibility(View.VISIBLE);
                            m_pager.animate().cancel();
                            m_pager.setAlpha(0);
                            m_pager.animate().setDuration(250).alpha(1);
                        },(long) 400));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void updateList(ArrayList<serverListRowModel> p_server_model){
        m_server_model = p_server_model;
    }

    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listViewHolder holder, int position) {
        holder.bindListView(m_server_model.get(position));
    }

    @Override
    public int getItemCount() {
        return m_server_model.size();
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView heaaderText;
        TextView descriptionText;
        ImageView flags;
        ImageView check;
        LinearLayout layout;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(serverListRowModel model) {

            heaaderText = itemView.findViewById(R.id.header);
            descriptionText = itemView.findViewById(R.id.description);
            flags = itemView.findViewById(R.id.icon);
            check = itemView.findViewById(R.id.tick);
            layout = itemView.findViewById(R.id.server);

            heaaderText.setText(model.getHeader());
            descriptionText.setText(model.getDescription());

            if(model.getCountryModel()!=null){
                if(model.getCountryModel().getCountry().equals(proxyController.getInstance().getServerName())){
                    check.setVisibility(View.VISIBLE);
                }else {
                    check.setVisibility(View.INVISIBLE);
                }
            }else {
                if(proxyController.getInstance().getServerName().equals(strings.EMPTY_STR) || proxyController.getInstance().getServerName().equals(strings.OPTIMAL_SERVER)){
                    check.setVisibility(View.VISIBLE);
                }else {
                    check.setVisibility(View.INVISIBLE);
                }
            }

            if(model.getCountryModel() != null){
                flags.setImageDrawable(FlagKit.drawableWithFlag(m_context, model.getCountryModel().getCountry()));
            }else {
                try {
                    Resources res = m_context.getResources();
                    flags.setImageDrawable(Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_star)));
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
            }

            layout.setOnClickListener(view -> {
                if(model.getCountryModel() == null){
                    sharedControllerManager.getInstance().getHomeController().onChooseServer(strings.OPTIMAL_SERVER);
                    m_context.onBackPressed();
                }else {
                    if(!m_type_response){
                        serverListModel.getInstance().addModel(model);
                        sharedControllerManager.getInstance().getHomeController().onChooseServer(model.getCountryModel().getCountry());
                        m_context.onBackPressed();
                    }else {
                        status.DEFAULT_SERVER = model.getCountryModel().getCountry();
                        pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DEFAULT_SERVER, status.DEFAULT_SERVER), enums.PREFERENCES_ETYPE.SET_STRING);
                        status.AUTO_OPTIMAL_LOCATION = false;
                        m_context.onBackPressed();
                    }
                }
            });
        }
    }
}
