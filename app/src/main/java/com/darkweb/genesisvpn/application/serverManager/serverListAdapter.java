package com.darkweb.genesisvpn.application.serverManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import com.jwang123.flagkit.FlagKit;
import java.util.ArrayList;

public class serverListAdapter extends RecyclerView.Adapter<serverListAdapter.listViewHolder>
{
    private AppCompatActivity m_context;
    private ArrayList<serverListRowModel> m_server_model = new ArrayList<>();
    private enums.SERVER m_type;

    serverListAdapter(AppCompatActivity p_context, ArrayList<serverListRowModel> p_server_model,enums.SERVER p_type) {
        this.m_context = p_context;
        m_type = p_type;
        updateList(p_server_model);
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
        LinearLayout layout;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(serverListRowModel model) {

            heaaderText = itemView.findViewById(R.id.header);
            descriptionText = itemView.findViewById(R.id.description);
            flags = itemView.findViewById(R.id.icon);
            layout = itemView.findViewById(R.id.server);

            heaaderText.setText(model.getHeader());
            descriptionText.setText(model.getDescription());

            flags.setBackground(FlagKit.drawableWithFlag(m_context, model.getCountryModel().getCountry()));

            layout.setOnClickListener(view -> {
                if(m_type == enums.SERVER.ALL){
                    serverListModel.getInstance().getRecentModel().add(model);
                    if(serverListModel.getInstance().getRecentModel().size()>4){
                        serverListModel.getInstance().getRecentModel().remove(0);
                    }
                }
                sharedControllerManager.getInstance().getHomeController().onChooseServer(model.getCountryModel());
                m_context.onBackPressed();
            });
        }
    }
}
