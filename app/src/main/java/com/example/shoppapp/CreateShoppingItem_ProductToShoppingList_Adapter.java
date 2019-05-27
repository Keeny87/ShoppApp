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
public class CreateShoppingItem_ProductToShoppingList_Adapter extends RecyclerView.Adapter<CreateShoppingItem_ProductToShoppingList_Adapter.ViewHolder>{

    private static final String TAG = "ProductRecyclerViewAdapter";

    private Context mContext;
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productBrands = new ArrayList<>();
    private ArrayList<String> productLocations = new ArrayList<>();
    private ArrayList<String> productRegularPrices = new ArrayList<>();
    private ArrayList<String> productStores = new ArrayList<>();

    // -- Added adapterInterface to update editText in parent fragment -- //
    private AdapterInterface adapterInterface;

    public interface AdapterInterface{
        void OnItemClicked(Product myProduct);
    }

    public CreateShoppingItem_ProductToShoppingList_Adapter(Context mContext
            , ArrayList<String> productNames
            , ArrayList<String> productBrands
            , ArrayList<String> productLocations
            , ArrayList<String> productRegularPrices
            , ArrayList<String> productStores
            , AdapterInterface adapterInterface

    ) {
        this.mContext = mContext;
        this.productNames = productNames;
        this.productBrands = productBrands;
        this.productLocations = productLocations;
        this.productRegularPrices = productRegularPrices;
        this.productStores = productStores;
        this.adapterInterface = adapterInterface;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView productBrand;
        TextView productName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView, final CreateShoppingItem_ProductToShoppingList_Adapter.AdapterInterface adapterInterface){
            super(itemView);

            productBrand = itemView.findViewById(R.id.product_brand);
            productName = itemView.findViewById(R.id.product_name);
            parentLayout = itemView.findViewById(R.id.product_recycler_layout);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_recyclerview_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view, adapterInterface);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.productName.setText(productNames.get(position));
        viewHolder.productBrand.setText(productBrands.get(position));

        // -- When clicking on recycler item -- //
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // -- Create Product object to send to parent fragment -- //
                Product myProduct = new Product();
                myProduct.setName(productNames.get(position).toString());
                myProduct.setBrand(productBrands.get(position).toString());
                myProduct.setLocation(productLocations.get(position).toString());
                myProduct.setRegularPrice(productRegularPrices.get(position).toString());
                myProduct.setStore(productStores.get(position).toString());

                // -- Send to parent fragment through interface -- //
                adapterInterface.OnItemClicked(myProduct);

            }
        });

    }

    @Override
    public int getItemCount() {
        return productNames.size();
    }

}
