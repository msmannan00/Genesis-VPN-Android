package com.darkweb.genesisvpn.application.appManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.strings;
import java.util.ArrayList;

public class appListAdapter extends RecyclerView.Adapter<appListAdapter.listViewHolder>
{
    private FragmentActivity m_context;
    private ArrayList<String> m_disabled_packages;
    private ArrayList<appListRowModel> m_app_model = new ArrayList<>();
    private ArrayList<appListRowModel> m_app_model_async;
    private ViewPager2 m_pager;
    boolean isLoaded = false;

    appListAdapter(FragmentActivity p_context, ArrayList<String> p_disabled_packages, ArrayList<appListRowModel> p_app_model, ViewPager2 p_pager) {
        this.m_context = p_context;
        m_pager = p_pager;
        m_disabled_packages = p_disabled_packages;
        m_app_model_async = p_app_model;
        m_pager.setVisibility(View.INVISIBLE);
        updateAsync();
    }

    public void updateAsync(){
        new Thread(){
            public void run(){
                try {
                    sleep(400);
                    for(int counter=0;counter<m_app_model_async.size();counter++){
                        int finalCounter = counter;
                        m_context.runOnUiThread(() -> new Handler().postDelayed(() -> {
                            m_app_model.add(m_app_model_async.get(finalCounter));
                            appListAdapter.this.notifyItemRangeChanged(finalCounter, 1);
                        },(long) 0));

                        if(!isLoaded && counter==20 && m_pager.getVisibility() == View.INVISIBLE){
                            isLoaded = true;
                            m_context.runOnUiThread(() -> new Handler().postDelayed(() -> {
                                m_pager.setVisibility(View.VISIBLE);
                                m_pager.animate().cancel();
                                m_pager.setAlpha(0);
                                m_pager.animate().setDuration(250).alpha(1);
                            },(long) 400));
                            Log.i("DUCK1", "DIC");

                        }
                        sleep(0);
                    }
                    if(!isLoaded){
                        m_pager.setAlpha(0);
                        m_context.runOnUiThread(() -> new Handler().postDelayed(() -> {
                            m_pager.setVisibility(View.VISIBLE);
                            m_pager.animate().cancel();
                            m_pager.setAlpha(0);
                            m_pager.animate().setDuration(250).alpha(1);
                        },(long) 400));
                        Log.i("DUCK2", "DIC");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull appListAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(m_app_model.get(position));
    }

    @Override
    public int getItemCount() {
        return m_app_model.size();
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView heaaderText;
        TextView descriptionText;
        TextView connected;
        de.hdodenhof.circleimageview.CircleImageView icon;
        LinearLayout layout;
        Switch m_switch;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(appListRowModel model) {
            heaaderText = itemView.findViewById(R.id.app_header);
            descriptionText = itemView.findViewById(R.id.app_description);
            connected = itemView.findViewById(R.id.app_connected);
            icon = itemView.findViewById(R.id.app_icon);
            layout = itemView.findViewById(R.id.app);
            m_switch =  itemView.findViewById(R.id.is_enabled);

            heaaderText.setText(model.getHeader());
            descriptionText.setText(model.getDescription());
            icon.setImageDrawable(model.getIcon());
            final float scale = m_context.getResources().getDisplayMetrics().density;
            int pixels = (int) (70 * scale + 0.5f);
            icon.setMinimumWidth(pixels);
            icon.setMinimumHeight(pixels);

            if(!m_disabled_packages.contains(model.getDescription())){
                m_switch.setChecked(true);
                connected.setText(strings.AF_CONNECTED);
                connected.setTextColor(m_context.getResources().getColor(R.color.green));
            } else{
                m_switch.setChecked(false);
                connected.setText(strings.AF_UNCONNECTED);
                connected.setTextColor(m_context.getResources().getColor(R.color.colorAccent));
            }

            layout.setOnClickListener(view -> {
                Switch m_checkbox_current = (Switch) ((LinearLayout)view).getChildAt(2);
                boolean isChecked = !m_checkbox_current.isChecked();
                m_checkbox_current.performClick();
                if(!isChecked){
                    if(!m_disabled_packages.contains(model.getDescription())){
                        m_disabled_packages.add(model.getDescription());
                    }
                }else {
                    m_disabled_packages.remove(model.getDescription());
                }

                if(!isChecked){
                    animateConnectText(connected, strings.AF_UNCONNECTED, m_context.getResources().getColor(R.color.colorAccent));
                } else{
                    animateConnectText(connected, strings.AF_CONNECTED, m_context.getResources().getColor(R.color.green));
                }
            });
        }
    }

    void animateConnectText(TextView p_connected, String p_text, int p_color){
        m_context.runOnUiThread(() -> {
            try{
                if(p_connected.getAlpha()>=0.7f){
                    p_connected.animate().cancel();
                    p_connected.animate().alpha(0f).setDuration(250).withEndAction(() -> {
                        p_connected.setText(p_text);
                        p_connected.setTextColor(p_color);
                        p_connected.animate().setDuration(250).alpha(1);
                    }).start();
                }else {
                    p_connected.animate().cancel();
                    p_connected.setAlpha(0.3f);
                    p_connected.setText(p_text);
                    p_connected.setTextColor(p_color);
                    p_connected.animate().setDuration(250).alpha(1).start();
                }
            }catch (Exception ex){
            }
        });
    }
}
