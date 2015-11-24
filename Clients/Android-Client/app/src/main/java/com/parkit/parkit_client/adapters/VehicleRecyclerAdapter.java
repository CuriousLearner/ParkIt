package com.parkit.parkit_client.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.rest.models.parkit.Vehicle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vikram on 22/11/15.
 */
public class VehicleRecyclerAdapter extends RecyclerView.Adapter<VehicleRecyclerAdapter.ViewHolder> {



    // data set for recycler
    ArrayList<Vehicle> vehiclesData;



    // inner view holder class
    // provides a reference to the views for a single data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // inner view references for each item view
        TextView vehicleTypeText, vehicleNumberText;
        ImageView vehicleRCImageView;

        public ViewHolder(View vehicleCardView) {
            super(vehicleCardView);
            // This constructor is passed the item layout view
            // Get a handle for each field view from the item layout view
            vehicleTypeText = (TextView) vehicleCardView.findViewById(R.id.text_vehicle_type);
            vehicleNumberText = (TextView) vehicleCardView.findViewById(R.id.text_vehicle_number);
            vehicleRCImageView = (ImageView) vehicleCardView
                    .findViewById(R.id.image_view_vehicle_rc);
        }
    }

    // constructor which is provided the data set to display
    public VehicleRecyclerAdapter(ArrayList<Vehicle> vehicles) {
        this.vehiclesData = vehicles;
    }


    // Create new views ( called by layout manager )
    @Override
    public VehicleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(Constants.LOG_TAG, "Creating new view holder");
        // create a new view
        // using your custom item layout
        View vehicleCardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_vehicle_layout, parent, false);

        // set view params
        vehicleCardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        // create custom view holder using this view
        ViewHolder vehicleViewHolder = new ViewHolder(vehicleCardView);


        return vehicleViewHolder;
    }



    // replace the contents of the view holder
    // with new item data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(Constants.LOG_TAG, "Binding view holder for item at position : "+position);
        // get item data
        Vehicle vehicleItemData = vehiclesData.get(position);

        // replace view holder data with new item data
        holder.vehicleNumberText.setText(vehicleItemData.vehicle_number);
        holder.vehicleTypeText.setText(vehicleItemData.vehicle_type);
        // load rc image into image view
        Picasso.with(holder.vehicleRCImageView.getContext())
                .load(vehicleItemData.vehicle_rc_link)
                .into(holder.vehicleRCImageView);

    }

    // return the size of the data set to be displayed ( invoked by layout manager )
    @Override
    public int getItemCount() {
        Log.d(Constants.LOG_TAG, "getItemCount() called : returning "+
                ((vehiclesData == null) ? 0 : vehiclesData.size()));
        return ((vehiclesData == null) ? 0 : vehiclesData.size());
    }

    public void updateVehiclesData(ArrayList<Vehicle> vehiclesData) {
        Log.d(Constants.LOG_TAG, "Updating data set");
        this.vehiclesData = vehiclesData;
        this.notifyDataSetChanged();
    }
}
