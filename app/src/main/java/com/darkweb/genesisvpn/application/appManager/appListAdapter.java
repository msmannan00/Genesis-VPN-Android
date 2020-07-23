package com.darkweb.genesisvpn.application.appManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.strings;
import java.util.ArrayList;

public class appListAdapter extends RecyclerView.Adapter<appListAdapter.listViewHolder>
{
    private AppCompatActivity m_context;
    private ArrayList<String> m_disabled_packages;
    private ArrayList<appListRowModel> m_app_model = new ArrayList<>();

    appListAdapter(AppCompatActivity p_context, ArrayList<String> p_disabled_packages, ArrayList<appListRowModel> p_app_model) {
        this.m_context = p_context;
        m_disabled_packages = p_disabled_packages;
        updateList(p_app_model);
    }

    public void updateList(ArrayList<appListRowModel> p_app_model){
        m_app_model.clear();
        m_app_model.addAll(p_app_model);
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
