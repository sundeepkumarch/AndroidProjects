package com.sundeep.buttonoverlay.gesture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sundeep.buttonoverlay.R;

import java.util.List;

public class ActionsListAdapter extends ArrayAdapter<ActionModel> {

    private List<ActionModel> mActionsList;
    private Context mContext;
    private static LayoutInflater inflater = null;
    private ActionsHolder actionsHolder;

    public ActionsListAdapter(Context context, int resource, List<ActionModel> mActionsList) {
        super(context, resource, mActionsList);
        this.mActionsList = mActionsList;
        try {
            this.mContext = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.action_item, parent, false);
            if (actionsHolder == null) {
                actionsHolder = new ActionsHolder();
            }
            actionsHolder.actionIcon = (ImageView) convertView.findViewById(R.id.action_icon);
            actionsHolder.actionTitle = (TextView) convertView.findViewById(R.id.action_title);

            convertView.setTag(actionsHolder);
        } else {
            actionsHolder = (ActionsHolder) convertView.getTag();
        }

        ActionModel model = mActionsList.get(position);
        if (model != null) {
            actionsHolder.actionIcon.setImageResource(model.getActionIcon());
            actionsHolder.actionTitle.setText(model.getActionTitle());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mActionsList.size();
    }

    public class ActionsHolder{
        public ImageView actionIcon;
        public TextView actionTitle;
    }
}
