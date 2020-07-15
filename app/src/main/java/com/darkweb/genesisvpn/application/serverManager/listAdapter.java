package com.darkweb.genesisvpn.application.serverManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.jwang123.flagkit.FlagKit;

public class listAdapter extends RecyclerView.Adapter<listAdapter.listViewHolder>
{
    private AppCompatActivity m_context;

    listAdapter(AppCompatActivity p_context) {
        this.m_context = p_context;
    }

    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(listModel.getInstance().getModel().get(position));
    }

    @Override
    public int getItemCount() {
        return listModel.getInstance().getModel().size();
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

        void bindListView(listRowModel model) {

            heaaderText = itemView.findViewById(R.id.header);
            descriptionText = itemView.findViewById(R.id.description);
            flags = itemView.findViewById(R.id.flag);
            layout = itemView.findViewById(R.id.server);

            heaaderText.setText(model.getHeader());
            descriptionText.setText(model.getDescription());

            flags.setBackground(FlagKit.drawableWithFlag(m_context, model.getCountryModel().getCountry()));

            layout.setOnClickListener(view -> {
                proxyController.getInstance().onChooseServer(model.getCountryModel());
                m_context.onBackPressed();
            });
        }
    }
}
