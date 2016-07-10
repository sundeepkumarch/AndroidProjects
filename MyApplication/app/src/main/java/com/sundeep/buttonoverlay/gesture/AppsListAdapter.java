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

public class AppsListAdapter  extends ArrayAdapter<AppListItem> {

    private List<AppListItem> appsList;
    private Context mContext;
    private static LayoutInflater inflater = null;
    private AppsHolder holder;

    public AppsListAdapter(Context context, int resource, List<AppListItem> list) {
        super(context, resource, list);
        this.appsList = list;
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
            convertView = inflater.inflate(R.layout.app_item, parent, false);
            if (holder == null) {
                holder = new AppsHolder();
            }
            holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView.findViewById(R.id.app_name);

            convertView.setTag(holder);
        } else {
            holder = (AppsHolder) convertView.getTag();
        }

        AppListItem model = appsList.get(position);
        if (model != null) {
            holder.appIcon.setImageDrawable(model.getIcon());
            holder.appName.setText(model.getAppname());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return appsList.size();
    }

    public class AppsHolder{
        public ImageView appIcon;
        public TextView appName;
    }
}
