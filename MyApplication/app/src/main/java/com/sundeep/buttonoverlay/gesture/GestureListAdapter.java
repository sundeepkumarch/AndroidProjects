package com.sundeep.buttonoverlay.gesture;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.List;

public class GestureListAdapter extends RecyclerView.Adapter<GestureListAdapter.RecyclerViewHolder>{

    private List<GestureHolder> mGestureList;
    private Context mContext;


    public GestureListAdapter(ArrayList<GestureHolder> gestureList, Context context) {
        this.mGestureList = gestureList;
        this.mContext = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item, null);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        GestureHolder gestureHolder = mGestureList.get(position);
        holder.gestureName.setText(gestureHolder.getGestureName());
        try {
            holder.gestureImage.setImageBitmap(gestureHolder.getGesture().toBitmap(50, 50, 3, Color.YELLOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mGestureList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView gestureName;
        public ImageView gestureImage;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            gestureName = (TextView)itemView.findViewById(R.id.gesture_name);
            gestureImage = (ImageView)itemView.findViewById(R.id.gesture_image);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
