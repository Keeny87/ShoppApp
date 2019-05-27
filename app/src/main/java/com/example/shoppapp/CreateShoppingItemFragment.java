package com.example.shoppapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.inputmethod.InputMethodManager;
import android.support.design.widget.Snackbar;

import java.util.ArrayList;

public class CreateShoppingItemFragment extends Fragment {

    // -- Class properties -- //
    private Button buttonCreateShoppingItem;
    private EditText shoppingItemName, shoppingItemBrand, shoppingItemLocation, shoppingItemStore, shoppingItemPrice;
    private CheckBox shoppingItemOnSale;
    private ScrollView CreateShoppingItemScrollView;
    private TextView textViewCreateShoppingListFor;

    // -- RecyclerView -- //
    protected RecyclerView mProductToShoppingListRecyclerView;
    private ArrayList<String> productNames;
    private ArrayList<String> productBrands;
    private ArrayList<String> productLocations;
    private ArrayList<String> productRegularPrices;
    private ArrayList<String> productStores;

    // -- Spinner. Show products matching to specific string -- //
    private String selectedProductList;
    private NDSpinner productToShoppingListSpinner;
    private ArrayList<String> spinnerItems = new ArrayList<String>();
    private boolean firstRun = true;

    // -- Empty constructor for this class -- //
    public CreateShoppingItemFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // -- This corresponds to the onCreate that we have in our normal activities -- //
        final View rootView = inflater.inflate(R.layout.createshoppingitemfragment,container,false);

        // -- Bind the scrollview from xml file to property in this class -- //
        CreateShoppingItemScrollView = (ScrollView) rootView.findViewById(R.id.createshoppingitemscrollview);

        // -- RecyclerView -- //
        mProductToShoppingListRecyclerView = (RecyclerView) rootView.findViewById(R.id.productToShoppingItem_recyclerview);
        mProductToShoppingListRecyclerView.setHasFixedSize(true);
        mProductToShoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // -- Spinner. Setting spinner value to blank on init -- //
        selectedProductList = "";
        spinnerItems.add("Choose product list");
        productNames = new ArrayList<String>();
        productBrands = new ArrayList<String>();
        productLocations = new ArrayList<String>();
        productRegularPrices = new ArrayList<String>();
        productStores = new ArrayList<String>();

        // -- Spinner -- //
        productToShoppingListSpinner = (NDSpinner)rootView.findViewById(R.id.productToShoppingItem_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        productToShoppingListSpinner.setAdapter(spinnerAdapter);


        // -- Get username of logged in user, then display on textview -- //
        String username = ((MainActivity)getActivity()).firebaseGetCurrentUserName();
        textViewCreateShoppingListFor = (TextView) rootView.findViewById(R.id.textViewCreateShoppingItemFor);
        textViewCreateShoppingListFor.setText(getResources().getString(R.string.createshoppingitemfor) + username);

        // ------------------------------------------------------------------ //
        // -- Spinner. Start of actions when item is selected from spinner -- //
        productToShoppingListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //selectedProductList = spinnerItems.get(position);

                    selectedProductList = spinnerItems.get(position);
                    ((MainActivity) getActivity()).setSelectedStoreOnCreateShoppingItem(selectedProductList);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myProductList = database.getReference("product-list");

                    // -- Attach a listener to read the data at our posts reference -- //
                    myProductList.addValueEventListener(new ValueEventListener() {

                        // -- Firebase. When data is changed, update the view -- //
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // -- Clearing ArrayLists, so they can be repopulated from fresh -- //
                            productNames.clear();
                            productBrands.clear();
                            productLocations.clear();
                            productRegularPrices.clear();
                            productStores.clear();

                            // -- Firebase. Running through each instance -- //
                            for (DataSnapshot productListSnap : dataSnapshot.getChildren()) {

                                // -- Storing the store name attached to the currently iterated product list -- //
                                String productList_storeName = productListSnap.getKey().toString();

                                // -- Checking whether the product list is same as selected product list -- //
                                if (productList_storeName.equals(selectedProductList)) {
                                    for (DataSnapshot productSnap : productListSnap.getChildren()) {
                                        String productName = productSnap.child("name").getValue().toString();
                                        String productBrand = productSnap.child("brand").getValue().toString();
                                        String productLocation = productSnap.child("location").getValue().toString();
                                        String productRegularPrice = productSnap.child("regularPrice").getValue().toString();
                                        String productStore = productSnap.child("store").getValue().toString();
                                        productNames.add(productName);
                                        productBrands.add(productBrand);
                                        productLocations.add(productLocation);
                                        productRegularPrices.add(productRegularPrice);
                                        productStores.add(productStore);
                                    }
                                }

                            }

                            // -- Use adapter to translate the ArrayList data to be shown in RecyclerView -- //
                            CreateShoppingItem_ProductToShoppingList_Adapter adapter = new CreateShoppingItem_ProductToShoppingList_Adapter(getContext(), productNames, productBrands, productLocations, productRegularPrices, productStores, adapterInterface);
                            mProductToShoppingListRecyclerView.setAdapter(adapter);

                            // -- Checking whether shopping list is run for the first time. If true, then selected spinner should display store for which recyclerView items are retrieved -- //
                            if(firstRun == true){

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                        getContext()
                                        ,android.R.layout.simple_spinner_dropdown_item
                                        , spinnerItems
                                );

                                productToShoppingListSpinner.setAdapter(spinnerAdapter);
                                firstRun = false;
                            }
                        }

                        // -- Firebase. If the retrieval of data fails -- //
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });

                    // -- Repopulate the recyclerView when spinner item is clicked -- //
                    CreateShoppingItem_ProductToShoppingList_Adapter adapter
                            = new CreateShoppingItem_ProductToShoppingList_Adapter(getContext(), productNames, productBrands, productLocations, productRegularPrices, productStores, adapterInterface);
                    mProductToShoppingListRecyclerView.setAdapter(adapter);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // -- Spinner. End of actions when item is selected from spinner -- //
        // ---------------------------------------------------------------- //

        // -------------------------------------------------------------------- //
        // -- Firebase. Read from the database. Sending data to recyclerView -- //
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myStoreList = database.getReference("product-list");

        myStoreList.addValueEventListener(new ValueEventListener() {

            // -- Firebase. On initial and when data change occurs, the latest data is retrieved -- //
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // -- Clearing ArrayLists, so they can be repopulated from fresh -- //
                spinnerItems.clear();

                // -- Firebase. Running through each instance -- //
                for(DataSnapshot storeListSnap : dataSnapshot.getChildren()){

                    // -- Firebase. Retrieve store names and add to spinner -- //
                    String shoppingItemList_storeName = storeListSnap.getKey().toString();
                    spinnerItems.add(shoppingItemList_storeName);

                }

                // -- Adapter to translate the ArrayList data to be shown in RecyclerView -- //
                CreateShoppingItem_ProductToShoppingList_Adapter adapter = new CreateShoppingItem_ProductToShoppingList_Adapter(getContext(), productNames, productBrands, productLocations, productRegularPrices, productStores, adapterInterface);
                mProductToShoppingListRecyclerView.setAdapter(adapter);

            }

            // -- Firebase. If data retrieval fails -- //
            @Override
            public void onCancelled(DatabaseError error) {
            }

        });
        // -- Firebase. Read from the database. Sending data to recyclerView -- //
        // -------------------------------------------------------------------- //

        // -- Retrieving EditText & button from layout and binding to local variables -- //
        shoppingItemName = (EditText)rootView.findViewById(R.id.createshoppingitemname);
        shoppingItemBrand = (EditText)rootView.findViewById(R.id.createshoppingitembrand);
        shoppingItemLocation = (EditText)rootView.findViewById(R.id.createshoppingitemlocation);
        shoppingItemStore = (EditText)rootView.findViewById(R.id.createshoppingitemstore);
        shoppingItemPrice = (EditText)rootView.findViewById(R.id.createshoppingitemprice);
        shoppingItemOnSale = (CheckBox)rootView.findViewById(R.id.createshoppingitemonsale);
        buttonCreateShoppingItem = (Button) rootView.findViewById(R.id.createshoppingitembutton);

        // -- Setting on click listener, so clicks on "Create Product" button are received -- //
        buttonCreateShoppingItem.setOnClickListener(new View.OnClickListener() {

            // -- Clicking "Create shopping item" button -- //
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(shoppingItemName.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter shopping item name", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(shoppingItemStore.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter shopping item store", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(shoppingItemPrice.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter shopping item price", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(shoppingItemPrice.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter shopping item price", Toast.LENGTH_SHORT).show();
                }

                else {

                    // -- Retrieving EditText values into local variables -- //
                    String newShoppingItemName = shoppingItemName.getText().toString();
                    String newShoppingItemBrand = shoppingItemBrand.getText().toString();
                    String newShoppingItemLocation = shoppingItemLocation.getText().toString();
                    String newShoppingItemStore = shoppingItemStore.getText().toString();
                    String newShoppingItemPrice = shoppingItemPrice.getText().toString();
                    String newShoppingItemOnSale = "false";
                    if (shoppingItemOnSale.isChecked()) {
                        newShoppingItemOnSale = "true";
                    }

                    // -- Firebase. Calling mainActivity method to add shopping item to database -- //
                    ((MainActivity) getActivity()).AddShoppingItemToDatabase(
                            newShoppingItemName,
                            newShoppingItemBrand,
                            newShoppingItemLocation,
                            newShoppingItemStore,
                            newShoppingItemPrice,
                            newShoppingItemOnSale,
                            "false"
                    );

                    // -- Snackbar. Hide the keyboard when clicking the button -- //
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                    // -- Snackbar. Event after creating shopping item -- //
                    Snackbar snackbar = Snackbar.make(rootView, newShoppingItemName + getResources().getString(R.string.isaddedtoshoppinglist), Snackbar.LENGTH_LONG)

                            // -- Code to run if user clicks "Remove item" on the snackbar -- //
                            .setAction("Remove item", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String ShoppingItemID = ((MainActivity) getActivity()).getSnackbarShoppingItemID();
                                    String ShoppingItemStore = ((MainActivity) getActivity()).getSnackbarShoppingItemStore();

                                    boolean isItemRemove = ((MainActivity) getActivity()).removeShoppingItemForLoggedInUser(ShoppingItemID, ShoppingItemStore);

                                    // -- Snackbar. Make message in case error occurs when removing shopping item -- //
                                    Snackbar snackbar = Snackbar.make(rootView, getResources().getString(R.string.errorremovingnewlycreatedshoppingitem), Snackbar.LENGTH_SHORT);
                                    if(isItemRemove){
                                        // -- Snackbar. If item is removed successfully, then return success message in snackbar -- //
                                        String ShoppingItemName = ((MainActivity) getActivity()).getSnackbarShoppingItemName();
                                        snackbar = Snackbar.make(rootView, ShoppingItemName + getResources().getString(R.string.isremovedfromshoppinglist), Snackbar.LENGTH_SHORT);
                                    }

                                    // -- Snackbar. Show the "Shopping item has been removed" message -- //
                                    snackbar.show();
                                }
                            });

                    // -- Snackbar. Show the "Shopping item has been created" message -- //
                    snackbar.show();
                }

            }

        });

        return rootView;
    }

    // -- Implementing interface to update fragment textEdit from adapter
    CreateShoppingItem_ProductToShoppingList_Adapter.AdapterInterface adapterInterface = new CreateShoppingItem_ProductToShoppingList_Adapter.AdapterInterface() {
        @Override
        // -- Data retrieved from adapter when clicking on RecyclerView -- //
        //public void OnItemClicked(int item_id) {
        public void OnItemClicked(Product myProduct) {

            // -- Insert retrieved data into EditText fields -- //
            shoppingItemName.setText(myProduct.getName());
            shoppingItemBrand.setText(myProduct.getBrand());
            shoppingItemLocation.setText(myProduct.getLocation());
            shoppingItemStore.setText(myProduct.getStore());
            shoppingItemPrice.setText(myProduct.getRegularPrice());

            // -- Scroll to top of scrollView after an existing products values has been entered into editText fields -- //
            CreateShoppingItemScrollView.smoothScrollTo(0, 0);

        }
    };

    @Override
    public void onAttach(Context context) { super.onAttach(context);
    }

    @Override
    public void onDetach() { super.onDetach(); }

}
