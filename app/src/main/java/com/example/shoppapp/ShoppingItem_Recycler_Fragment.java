package com.example.shoppapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingItem_Recycler_Fragment extends Fragment {

    private static final String TAG = "ShoppingItem_Recycler_Fragment";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    private TextView textViewShowShoppingListFor;
    protected RecyclerView mRecyclerView;
    protected RecyclerView mRecyclerView2;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;

    private ArrayList<String> shoppingItem_IDs;
    private ArrayList<String> shoppingItem_Names;
    private ArrayList<String> shoppingItem_Brands;
    private ArrayList<String> shoppingItem_Locations;
    private ArrayList<String> shoppingItem_Prices;
    private ArrayList<String> shoppingItem_OnSale_List;
    private ArrayList<String> shoppingItem_InBasket_List;
    private ArrayList<String> basketItem_IDs;
    private ArrayList<String> basketItem_Names;
    private ArrayList<String> basketItem_Brands;
    private ArrayList<String> basketItem_Locations;
    private ArrayList<String> basketItem_Prices;
    private ArrayList<String> basketItem_OnSale_List;
    private ArrayList<String> basketItem_InBasket_List;
    private ArrayAdapter<Product> ArrayAdapter;
    protected ShoppingItem_RecyclerView_Adapter adapter;
    protected ShoppingItem_RecyclerView_Adapter adapter2;

    // -- Show products matching to specific string -- //
    private String selectedShoppingItemList;

    // -- Created NDSpinner, as it should be possible to select the store at position 0 -- //
    NDSpinner storeListSpinner;

    // -- Spinner. Arraylist, which content should be adapted to the spinner -- //
    ArrayList<String> spinnerItems = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // -- Boolean indicating first run. Used when fragment is first loaded to display first spinner item for which RecyclerView items are shown -- //
    boolean firstRun = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        String currentStore = ((MainActivity)getActivity()).getSelectedStoreOnShoppingList();
        spinnerItems.add("Choose shopping list");

        super.onCreate(savedInstanceState);

        selectedShoppingItemList = "";

        shoppingItem_IDs = new ArrayList<String>();
        shoppingItem_Names = new ArrayList<String>();
        shoppingItem_Brands = new ArrayList<String>();
        shoppingItem_Locations = new ArrayList<String>();
        shoppingItem_Prices = new ArrayList<String>();
        shoppingItem_OnSale_List = new ArrayList<String>();
        shoppingItem_InBasket_List = new ArrayList<String>();
        basketItem_IDs = new ArrayList<String>();
        basketItem_Names = new ArrayList<String>();
        basketItem_Brands = new ArrayList<String>();
        basketItem_Locations = new ArrayList<String>();
        basketItem_Prices = new ArrayList<String>();
        basketItem_OnSale_List = new ArrayList<String>();
        basketItem_InBasket_List = new ArrayList<String>();

        View rootView = inflater.inflate(R.layout.shoppingitem_recyclerview_main, container, false);
        rootView.setTag(TAG);

        // -- Get username of logged in user, then display on textview -- //
        String username = ((MainActivity)getActivity()).firebaseGetCurrentUserName();
        textViewShowShoppingListFor = (TextView) rootView.findViewById(R.id.textViewShowShoppingListFor);
        textViewShowShoppingListFor.setText(getResources().getString(R.string.createshoppingitemshowingshoppinglist) + username);

        // -- Spinner -- //
        storeListSpinner = (NDSpinner)rootView.findViewById(R.id.shoppingListStoreSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        storeListSpinner.setAdapter(spinnerAdapter);

        // -- Spinner is clicked -- //
        storeListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                try {

                    // -- Setting in main activity, so this can be used to set products to basket (lookup in firebase database) -- //
                    ((MainActivity) getActivity()).setSelectedStoreOnShoppingList(spinnerItems.get(position));

                    selectedShoppingItemList = ((MainActivity)getActivity()).getSelectedStoreOnShoppingList();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    // -- New shopping list to retrieve specific users shopping list -- //
                    String firebaseUserID = ((MainActivity) getActivity()).firebaseGetUserID();
                    DatabaseReference myShoppingItemList = database.getReference(firebaseUserID).child("shopping-list");

                    // -- Attach a listener to read the data at our posts reference -- //
                    myShoppingItemList.addValueEventListener(new ValueEventListener() {

                        // -- Firebase. When data is changed, update the view -- //
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // -- Clearing ArrayLists, so they can be repopulated from fresh -- //
                            shoppingItem_IDs.clear();
                            shoppingItem_Names.clear();
                            shoppingItem_Brands.clear();
                            shoppingItem_Locations.clear();
                            shoppingItem_Prices.clear();
                            shoppingItem_OnSale_List.clear();
                            shoppingItem_InBasket_List.clear();
                            basketItem_IDs.clear();
                            basketItem_Names.clear();
                            basketItem_Brands.clear();
                            basketItem_Locations.clear();
                            basketItem_Prices.clear();
                            basketItem_OnSale_List.clear();
                            basketItem_InBasket_List.clear();

                            // -- Firebase. Running through each instance -- //
                            for (DataSnapshot shoppingItemListSnap : dataSnapshot.getChildren()) {

                                // -- Storing the store name attached to the currently iterated product list -- //
                                String productList_storeName = shoppingItemListSnap.getKey().toString();

                                // -- Checking whether the product list is same as selected product list -- //
                                if (productList_storeName.equals(selectedShoppingItemList)) {

                                    // -- Iterate through Firebase shopping items -- //
                                    for (DataSnapshot shoppingItemSnap : shoppingItemListSnap.getChildren()) {

                                        // -- If shopping item's property "inBasket" is false then apply shopping item to shopping list -- //
                                        if (shoppingItemSnap.child("inBasket").getValue().toString().equals("false")) {
                                            //String shopping_Item_ID = shoppingItemSnap.getValue().toString();
                                            String shopping_Item_ID = shoppingItemSnap.getKey().toString();
                                            shoppingItem_IDs.add(shopping_Item_ID);
                                            String shopping_Item_Name = shoppingItemSnap.child("name").getValue().toString();
                                            shoppingItem_Names.add(shopping_Item_Name);
                                            String shopping_Item_Brand = shoppingItemSnap.child("brand").getValue().toString();
                                            shoppingItem_Brands.add(shopping_Item_Brand);
                                            String shopping_Item_Location = shoppingItemSnap.child("location").getValue().toString();
                                            shoppingItem_Locations.add(shopping_Item_Location);
                                            String shopping_Item_Price = shoppingItemSnap.child("price").getValue().toString();
                                            shoppingItem_Prices.add(shopping_Item_Price);
                                            String shopping_Item_OnSale = shoppingItemSnap.child("onSale").getValue().toString();
                                            shoppingItem_OnSale_List.add(shopping_Item_OnSale);
                                            String shopping_Item_InBasket = shoppingItemSnap.child("inBasket").getValue().toString();
                                            shoppingItem_InBasket_List.add(shopping_Item_InBasket);
                                        }

                                        // -- If shopping item's property "inBasket" is true then apply shopping item to basket list -- //
                                        else {
                                            //String basket_Item_ID = shoppingItemSnap.getValue().toString();
                                            String basket_Item_ID = shoppingItemSnap.getKey().toString();
                                            basketItem_IDs.add(basket_Item_ID);
                                            String basket_Item_Name = shoppingItemSnap.child("name").getValue().toString();
                                            basketItem_Names.add(basket_Item_Name);
                                            String basket_Item_Brand = shoppingItemSnap.child("brand").getValue().toString();
                                            basketItem_Brands.add(basket_Item_Brand);
                                            String basket_Item_Location = shoppingItemSnap.child("location").getValue().toString();
                                            basketItem_Locations.add(basket_Item_Location);
                                            String basket_Item_Price = shoppingItemSnap.child("price").getValue().toString();
                                            basketItem_Prices.add(basket_Item_Price);
                                            String basket_Item_OnSale = shoppingItemSnap.child("onSale").getValue().toString();
                                            basketItem_OnSale_List.add(basket_Item_OnSale);
                                            String basket_Item_InBasket = shoppingItemSnap.child("inBasket").getValue().toString();
                                            basketItem_InBasket_List.add(basket_Item_InBasket);
                                        }

                                    }
                                }

                            }

                            ShoppingItem_RecyclerView_Adapter adapter = new ShoppingItem_RecyclerView_Adapter(getContext(), shoppingItem_IDs, shoppingItem_Names, shoppingItem_Brands, shoppingItem_Locations, shoppingItem_Prices, shoppingItem_OnSale_List, shoppingItem_InBasket_List);
                            mRecyclerView.setAdapter(adapter);

                            ShoppingItem_RecyclerView_Adapter adapter2 = new ShoppingItem_RecyclerView_Adapter(getContext(), basketItem_IDs, basketItem_Names, basketItem_Brands, basketItem_Locations, basketItem_Prices, basketItem_OnSale_List, basketItem_InBasket_List);
                            mRecyclerView2.setAdapter(adapter2);

                            // -- Checking whether shopping list is run for the first time. If true, then selected spinner should display store for which recyclerView items are retrieved -- //
                            if(firstRun == true){

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                        getContext()
                                        ,android.R.layout.simple_spinner_dropdown_item
                                        , spinnerItems
                                );

                                storeListSpinner.setAdapter(spinnerAdapter);
                                firstRun = false;
                            }

                        }

                        // -- Firebase. If the retrieval of data fails -- //
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    // -- Repopulate the recyclerView when spinner item is clicked -- //
                    ShoppingItem_RecyclerView_Adapter adapter = new ShoppingItem_RecyclerView_Adapter(getContext(), shoppingItem_IDs, shoppingItem_Names, shoppingItem_Brands, shoppingItem_Locations, shoppingItem_Prices, shoppingItem_OnSale_List, shoppingItem_InBasket_List);
                    mRecyclerView.setAdapter(adapter);

                    ShoppingItem_RecyclerView_Adapter adapter2 = new ShoppingItem_RecyclerView_Adapter(getContext(), basketItem_IDs, basketItem_Names, basketItem_Brands, basketItem_Locations, basketItem_Prices, basketItem_OnSale_List, basketItem_InBasket_List);
                    mRecyclerView2.setAdapter(adapter2);
                }

                catch (Exception e){

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.shoppingItem_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView2 = (RecyclerView) rootView.findViewById(R.id.shoppingItem_recyclerview2);
        mRecyclerView2.setHasFixedSize(true);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        // -------------------------------------------------------------------- //
        // -- Firebase. Read from the database. Sending data to recyclerView -- //
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // -- New reference, retrieving store list from individual users instead of seperate global store list -- //
        String firebaseUserID = ((MainActivity)getActivity()).firebaseGetUserID();
        DatabaseReference myShoppingList = database.getReference(firebaseUserID).child("shopping-list");

        myShoppingList.addValueEventListener(new ValueEventListener() {

            // -- Firebase. On initial and when data change occurs, the latest data is retrieved -- //
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // -- Clearing ArrayLists, so they can be repopulated from fresh -- //
                spinnerItems.clear();

                // -- Firebase. Running through each instance -- //
                for(DataSnapshot storeListSnap : dataSnapshot.getChildren()){

                    String shoppingItemList_storeName = storeListSnap.getKey().toString();
                    spinnerItems.add(shoppingItemList_storeName);

                }

            }

            // -- Firebase. If data retrieval fails -- //
            @Override
            public void onCancelled(DatabaseError error) {
            }

        });
        // -- Firebase. Read from the database. Sending data to recyclerView -- //
        // -------------------------------------------------------------------- //

        selectedShoppingItemList = spinnerItems.get(0);

        return rootView;
    }

}
