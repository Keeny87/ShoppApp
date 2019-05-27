package com.example.shoppapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// -- Adapter used to translate ArrayList of strings containing store name into a RecyclerView -- //
public class CreateProduct_StoreToProduct_Adapter extends RecyclerView.Adapter<CreateProduct_StoreToProduct_Adapter.ViewHolder>{

    private static final String TAG = "StoreRecyclerViewAdapter";

    private Context mContext;
    private ArrayList<String> storeNames = new ArrayList<>();

    // -- Added adapterInterface to update editText in parent fragment -- //
    private myAdapterInterface newAdapterInterface;

    public interface myAdapterInterface{
        void OnItemClicked(Store myStore);
    }

    public CreateProduct_StoreToProduct_Adapter(Context mContext
            , ArrayList<String> storeNames
            , myAdapterInterface adapterInterface

    ) {
        this.mContext = mContext;
        this.storeNames = storeNames;
        this.newAdapterInterface = adapterInterface;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView storeName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView, final CreateProduct_StoreToProduct_Adapter.myAdapterInterface adapterInterface){
            super(itemView);

            storeName = itemView.findViewById(R.id.store_name);
            parentLayout = itemView.findViewById(R.id.product_recycler_layout);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_recyclerview_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view, newAdapterInterface);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.storeName.setText(storeNames.get(position));

        // -- When clicking on recycler item -- //
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // -- Create Product object to send to parent fragment -- //
                Store myStore = new Store();
                myStore.setName(storeNames.get(position).toString());

                // -- Send to parent fragment through interface -- //
                newAdapterInterface.OnItemClicked(myStore);

            }
        });

    }

    @Override
    public int getItemCount() {
        return storeNames.size();
    }

}
