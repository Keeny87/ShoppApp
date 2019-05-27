package com.example.shoppapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "ProductRecyclerViewAdapter";

    private Context mContext;
    private ArrayList<String> productIDs = new ArrayList<>();
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productBrands = new ArrayList<>();
    private ArrayList<String> productLocations = new ArrayList<>();
    private ArrayList<String> productRegularPrices = new ArrayList<>();

    public ProductRecyclerViewAdapter(Context mContext
            , ArrayList<String> productIDs
            , ArrayList<String> productNames
            , ArrayList<String> productBrands
            , ArrayList<String> productLocations
            , ArrayList<String> productRegularPrices
    ) {
        this.mContext = mContext;
        this.productIDs = productIDs;
        this.productNames = productNames;
        this.productBrands = productBrands;
        this.productLocations = productLocations;
        this.productRegularPrices = productRegularPrices;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        TextView productBrand;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productBrand = itemView.findViewById(R.id.product_brand);
            parentLayout = itemView.findViewById(R.id.product_recycler_layout);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_recyclerview_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.productName.setText(productNames.get(position));
        viewHolder.productBrand.setText(productBrands.get(position));

        // -- When clicking on recycler item -- //
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Toast.makeText(mContext,"Clicked on product: " + productNames.get(position), Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext,
                        "Name: " + productNames.get(position) +
                        "\nBrand: " + productBrands.get(position) +
                        "\nLocation: " + productLocations.get(position) +
                        "\nRegular price: " + productRegularPrices.get(position)
                        , Toast.LENGTH_SHORT).show();
            }
        });

        // -- When clicking on recycler item -- //
        viewHolder.parentLayout.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){

                ((MainActivity) mContext).setSelectedProductOnProductList(productIDs.get(position));

                ((MainActivity) mContext).showAlertDialogButtonClickedDeleteCurrentProduct(productIDs.get(position));

                Toast.makeText(mContext,"Name: " + productNames.get(position) +
                        "\nBrand: " + productBrands.get(position) +
                        "\nLocation: " + productLocations.get(position) +
                        "\nRegular price: " + productRegularPrices.get(position)
                        , Toast.LENGTH_LONG).show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return productNames.size();
    }

}
