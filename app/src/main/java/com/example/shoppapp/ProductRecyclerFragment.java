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
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductRecyclerFragment extends Fragment {

    private static final String TAG = "ProductRecyclerFragment";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected ProductRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;

    private ArrayList<String> productIDs;
    private ArrayList<String> productNames;
    private ArrayList<String> productBrands;
    private ArrayList<String> productLocations;
    private ArrayList<String> productRegularPrices;
    private ArrayAdapter<Product> ArrayAdapter;

    private ProductRecyclerViewAdapter adapter;

    // -- Show products matching to specific string -- //
    private String selectedProductList;

    // -- Created NDSpinner, as it should be possible to select the store at position 0 -- //
    NDSpinner storeListSpinner;

    // -- Spinner. Arraylist, which content should be adapted to the spinner -- //
    ArrayList<String> spinnerItems = new ArrayList<String>();

    // -- Boolean indicating first run. Used when fragment is first loaded to display first spinner item for which RecyclerView items are shown -- //
    boolean firstRun = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        spinnerItems.add("Choose product list");

        super.onCreate(savedInstanceState);

        selectedProductList = "";

        productIDs = new ArrayList<String>();
        productNames = new ArrayList<String>();
        productBrands = new ArrayList<String>();
        productLocations = new ArrayList<String>();
        productRegularPrices = new ArrayList<String>();

        View rootView = inflater.inflate(R.layout.product_recyclerview_main, container, false);
        rootView.setTag(TAG);

        // -- Spinner showing store list -- //
        storeListSpinner = (NDSpinner)rootView.findViewById(R.id.productliststorespinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        storeListSpinner.setAdapter(spinnerAdapter);

        // -- Spinner is clicked. Retrieve Firebase data -- //
        storeListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                    try{

                    selectedProductList = spinnerItems.get(position);
                    ((MainActivity) getActivity()).setSelectedStoreOnProductList(selectedProductList);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myProductList = database.getReference("product-list");

                    // -- Attach a listener to read the data at our posts reference -- //
                    myProductList.addValueEventListener(new ValueEventListener() {

                        // -- Firebase. When data is changed, update the view -- //
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // -- Clearing ArrayLists, so they can be repopulated from fresh -- //
                            productIDs.clear();
                            productNames.clear();
                            productBrands.clear();
                            productLocations.clear();
                            productRegularPrices.clear();

                            // -- Firebase. Running through each instance -- //
                            for (DataSnapshot productListSnap : dataSnapshot.getChildren()) {

                                // -- Storing the store name attached to the currently iterated product list -- //
                                String productList_storeName = productListSnap.getKey().toString();

                                // -- Checking whether the product list is same as selected product list -- //
                                if (productList_storeName.equals(selectedProductList)) {
                                    for (DataSnapshot productSnap : productListSnap.getChildren()) {
                                        String productID = productSnap.getKey().toString();
                                        String productName = productSnap.child("name").getValue().toString();
                                        String productBrand = productSnap.child("brand").getValue().toString();
                                        String productLocation = productSnap.child("location").getValue().toString();
                                        String productRegularPrice = productSnap.child("regularPrice").getValue().toString();
                                        productIDs.add(productID);
                                        productNames.add(productName);
                                        productBrands.add(productBrand);
                                        productLocations.add(productLocation);
                                        productRegularPrices.add(productRegularPrice);
                                    }
                                }

                            }

                            ProductRecyclerViewAdapter adapter = new ProductRecyclerViewAdapter(getContext(), productIDs, productNames, productBrands, productLocations, productRegularPrices);
                            mRecyclerView.setAdapter(adapter);

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
                    ProductRecyclerViewAdapter adapter = new ProductRecyclerViewAdapter(getContext(), productIDs, productNames, productBrands, productLocations, productRegularPrices);
                    mRecyclerView.setAdapter(adapter);
                }
                catch (Exception e){

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.product_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        // -------------------------------------------------------------------- //
        // -- Firebase. Read from the database. Sending data to recyclerView -- //
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myStoreList = database.getReference("product-list");

        myStoreList.addValueEventListener(new ValueEventListener() {

            // -- Firebase. On initial and when data change occurs, the latest data is retrieved -- //
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // -- Clearing ArrayList, so it can be repopulated from fresh -- //
                spinnerItems.clear();

                // -- Firebase. Running through each instance -- //
                for(DataSnapshot storeListSnap : dataSnapshot.getChildren()){

                    String shoppingItemList_storeName = storeListSnap.getKey().toString();
                    spinnerItems.add(shoppingItemList_storeName);

                }

                ProductRecyclerViewAdapter adapter = new ProductRecyclerViewAdapter(getContext(), productIDs, productNames, productBrands, productLocations, productRegularPrices);
                mRecyclerView.setAdapter(adapter);

            }

            // -- Firebase. If data retrieval fails -- //
            @Override
            public void onCancelled(DatabaseError error) {
            }

        });
        // -- Firebase. Read from the database. Sending data to recyclerView -- //
        // -------------------------------------------------------------------- //

        return rootView;
    }
}


