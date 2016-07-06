package com.sundeep.buttonoverlay.gesture;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.List;

public class GestureAdapter extends ArrayAdapter<GestureHolder> {

    private List<GestureHolder> mGestureList;
    private Context mContext;

    public GestureAdapter(ArrayList<GestureHolder> gestureList, Context context) {
        super(context, R.layout.activity_gesture_list,gestureList);
        this.mGestureList = gestureList;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        GestureViewHolder holder = new GestureViewHolder();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gesture_list_item,null);

            TextView idView = (TextView) view.findViewById(R.id.gestureId);
            TextView nameView = (TextView) view.findViewById(R.id.gestureName);
            ImageView gestureImageView = (ImageView) view.findViewById(R.id.gestureImage);
            TextView nameViewRef = (TextView) view.findViewById(R.id.gestureNameRef);

            holder.gestureId = idView;
            holder.gestureName = nameView;
            holder.gestureImage = gestureImageView;
            holder.gestureNameRef = nameViewRef;

            final ImageView mMenuItemButton =  (ImageView)view.findViewById(R.id.menuItemOptions);
            mMenuItemButton.setClickable(true);

            view.setTag(holder);
        } else {
            holder = (GestureViewHolder) view.getTag();
        }

        GestureHolder gestureHolder = mGestureList.get(position);
        holder.gestureId.setText(String.valueOf(gestureHolder.getGesture().getID()));
        holder.gestureName.setText(gestureHolder.getGestureName());
        holder.gestureNameRef.setText(gestureHolder.getGestureName());

        try {
            holder.gestureImage.setImageBitmap(gestureHolder.getGesture().toBitmap(30, 30, 3, Color.YELLOW));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    class GestureViewHolder {
        public TextView gestureId;
        public TextView gestureName;
        public ImageView gestureImage;
        public TextView gestureNameRef;

    }
}
