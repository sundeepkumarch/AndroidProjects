package com.sundeep.buttonoverlay.gesture;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.List;

public class ActionsListAdapter extends RecyclerView.Adapter<ActionsListAdapter.ActionsHolder>{

    private List<ActionModel> mActionsList;
    private Context mContext;

    public ActionsListAdapter(Context context, ArrayList<ActionModel> actionsList) {
        this.mContext = context;
        this.mActionsList = actionsList;
    }

    @Override
    public ActionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_item, null);
        ActionsHolder holder = new ActionsHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActionsHolder holder, int position) {
        ActionModel model = mActionsList.get(position);
        holder.actionIcon.setImageResource(model.getActionIcon());
        holder.actionTitle.setText(model.getActionTitle());
    }

    @Override
    public int getItemCount() {
        return mActionsList.size();
    }

    public class ActionsHolder extends RecyclerView.ViewHolder {

        public ImageView actionIcon;
        public TextView actionTitle;

        public ActionsHolder(View itemView) {
            super(itemView);
            actionIcon = (ImageView) itemView.findViewById(R.id.action_icon);
            actionTitle = (TextView) itemView.findViewById(R.id.action_title);
        }
    }
}
