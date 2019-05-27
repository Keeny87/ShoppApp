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

public class ShoppingItem_RecyclerView_Adapter extends RecyclerView.Adapter<ShoppingItem_RecyclerView_Adapter.ViewHolder>{

    private static final String TAG = "ShoppingItem_RecyclerView_Adapter";

    private Context mContext;
    private ArrayList<String> shoppingItem_IDs = new ArrayList<>();
    private ArrayList<String> shoppingItem_Names = new ArrayList<>();
    private ArrayList<String> shoppingItem_Brands = new ArrayList<>();
    private ArrayList<String> shoppingItem_Locations = new ArrayList<>();
    private ArrayList<String> shoppingItem_Prices = new ArrayList<>();
    private ArrayList<String> shoppingItem_OnSale = new ArrayList<>();
    private ArrayList<String> shoppingItem_InBasket = new ArrayList<>();

    public ShoppingItem_RecyclerView_Adapter(Context mContext
            , ArrayList<String> shoppingItem_IDs
            , ArrayList<String> shoppingItem_Names
            , ArrayList<String> shoppingItem_Brands
            , ArrayList<String> shoppingItem_Locations
            , ArrayList<String> shoppingItem_Prices
            , ArrayList<String> shoppingItem_OnSale
            , ArrayList<String> shoppingItem_InBasket
    ) {
        this.mContext = mContext;
        this.shoppingItem_IDs = shoppingItem_IDs;
        this.shoppingItem_Names = shoppingItem_Names;
        this.shoppingItem_Brands = shoppingItem_Brands;
        this.shoppingItem_Locations = shoppingItem_Locations;
        this.shoppingItem_Prices = shoppingItem_Prices;
        this.shoppingItem_OnSale = shoppingItem_OnSale;
        this.shoppingItem_InBasket = shoppingItem_InBasket;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView shoppingItem_Name;
        TextView shoppingItem_Brand;
        TextView shoppingItem_Location;
        TextView shoppingItem_Price;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            shoppingItem_Name = itemView.findViewById(R.id.shoppingItem_name);
            shoppingItem_Brand = itemView.findViewById(R.id.shoppingItem_brand);
            shoppingItem_Location = itemView.findViewById(R.id.shoppingItem_location);
            shoppingItem_Price = itemView.findViewById(R.id.shoppingItem_price);
            parentLayout = itemView.findViewById(R.id.shoppingitem_recycler_layout);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppingitem_recyclerview_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        try {

            //viewHolder.shoppingItem_ID.setText(shoppingItem_IDs.get(position));
            viewHolder.shoppingItem_Name.setText(shoppingItem_Names.get(position));
            viewHolder.shoppingItem_Brand.setText(shoppingItem_Brands.get(position));
            viewHolder.shoppingItem_Location.setText(shoppingItem_Locations.get(position));
            viewHolder.shoppingItem_Price.setText(shoppingItem_Prices.get(position));

            // -- When clicking on recycler item -- //
            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    putShoppingItemIntoBasketListOrShoppingList(shoppingItem_IDs.get(position), shoppingItem_InBasket.get(position));

                }
            });

            // -- When clicking on recycler item -- //
            viewHolder.parentLayout.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view){

                    ((MainActivity) mContext).setSelectedShoppingItemOnShoppingList(shoppingItem_IDs.get(position));

                    ((MainActivity) mContext).showAlertDialogButtonClickedDeleteCurrentShoppingItem(shoppingItem_IDs.get(position));

                    String productOnSale = "";
                    if(shoppingItem_OnSale.get(position).equals("true")){
                        productOnSale = mContext.getResources().getString(R.string.shoppingitemOnSaleYes);;
                    }
                    else{
                        productOnSale = mContext.getResources().getString(R.string.shoppingitemOnSaleNo);
                    }

                    Toast.makeText(mContext,
                                    "Name: " + shoppingItem_Names.get(position) +
                                    "\nBrand: " + shoppingItem_Brands.get(position) +
                                    "\nLocation: " + shoppingItem_Locations.get(position) +
                                    "\nPrice: " + shoppingItem_Prices.get(position) +
                                    "\nOn sale: " + productOnSale
                            , Toast.LENGTH_LONG).show();

                    return true;
                }
            });
        }
        catch (Exception myException){
            String test = myException.toString();
        }
    }

    public void putShoppingItemIntoBasketListOrShoppingList(String shoppingItemKey, String shoppingItemInBasket){

        boolean myShoppingItemInBasket = false;
        if(shoppingItemInBasket.equals("true")){
            myShoppingItemInBasket = true;
        }

        ((MainActivity)mContext).putShoppingItemIntoBasket(shoppingItemKey, myShoppingItemInBasket);

    }

    @Override
    public int getItemCount() {
        return shoppingItem_Names.size();
    }

}
