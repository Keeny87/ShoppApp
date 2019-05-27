package com.example.shoppapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CreateProductFragment extends Fragment {

    // -- ScrollView property to manipulate scrollView in this class -- //
    ScrollView CreateProductScrollView;

    // -- Firebase. Variable for database reference -- //
    private DatabaseReference mDatabase;

    Button buttonCreateProduct;
    EditText productName, productBrand, productLocation, productRegularPrice, productStore, productPicture;

    // -- RecyclerView holding stores to easily fill in EditText field -- //
    protected RecyclerView mStoreToProductRecyclerView;
    private ArrayList<String> storeNames;

    // -- Empty constructor for this class -- //
    public CreateProductFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // -- This corresponds to the onCreate that we have in our normal activities -- //
        final View rootView = inflater.inflate(R.layout.createproductfragment,container,false);

        // -- Bind the scrollview from xml file to property in this class -- //
        CreateProductScrollView = (ScrollView) rootView.findViewById(R.id.createproductscrollview);

        // -- RecyclerView -- //
        mStoreToProductRecyclerView = (RecyclerView) rootView.findViewById(R.id.stores_recyclerview);
        mStoreToProductRecyclerView.setHasFixedSize(true);
        mStoreToProductRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // -- Retrieving EditText & button from layout and binding to local variables -- //
        productName = (EditText)rootView.findViewById(R.id.createproductname);
        productBrand = (EditText)rootView.findViewById(R.id.createproductbrand);
        productLocation = (EditText)rootView.findViewById(R.id.createproductlocation);
        productStore = (EditText)rootView.findViewById(R.id.createproductstore);
        productRegularPrice = (EditText)rootView.findViewById(R.id.createproductregularprice);

        buttonCreateProduct = (Button) rootView.findViewById(R.id.createproductbutton);

        // -- ArrayList to hold storeNames for the RecyclerView -- //
        storeNames = new ArrayList<String>();

        // -- Setting on click listener, so clicks on "Create Product" button are received -- //
        buttonCreateProduct.setOnClickListener(new View.OnClickListener() {

            // -- Clicking "Create Product" button -- //
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(productName.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter product name", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(productBrand.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter product brand", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(productLocation.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter product location", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(productStore.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter product store", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(productRegularPrice.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter product regular price", Toast.LENGTH_SHORT).show();
                }

                else {

                    // -- Retrieving EditText values into local variables -- //
                    String newProductName = productName.getText().toString();
                    String newProductBrand = productBrand.getText().toString();
                    String newProductLocation = productLocation.getText().toString();
                    String newProductStore = productStore.getText().toString();
                    String newProductRegularPrice = productRegularPrice.getText().toString();

                    // -- Firebase. Calling method from mainActivity as method 2 lines down is not working -- //
                    ((MainActivity) getActivity()).AddProductToDatabase(
                            newProductName,
                            newProductBrand,
                            newProductLocation,
                            newProductStore,
                            newProductRegularPrice
                    );

                    // -- Snackbar. Hide the keyboard when clicking the button -- //
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                    // -- Snackbar. Event after creating product -- //
                    Snackbar snackbar = Snackbar.make(rootView, newProductName + getResources().getString(R.string.isaddedtoproductlist), Snackbar.LENGTH_LONG)

                            // -- Code to run if user clicks "Remove item" on the snackbar -- //
                            .setAction("Remove product", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String ProductID = ((MainActivity) getActivity()).getSnackbarProductID();
                                    String ProductStore = ((MainActivity) getActivity()).getSnackbarProductStore();

                                    boolean isProductRemoved = ((MainActivity) getActivity()).removeProduct(ProductID, ProductStore);

                                    // -- Snackbar. Make message in case error occurs when removing product -- //
                                    Snackbar snackbar = Snackbar.make(rootView, getResources().getString(R.string.errorremovingnewlycreatedproduct), Snackbar.LENGTH_SHORT);
                                    if(isProductRemoved){
                                        // -- Snackbar. If item is removed successfully, then return success message in snackbar -- //
                                        String ProductName = ((MainActivity) getActivity()).getSnackbarProductName();
                                        snackbar = Snackbar.make(rootView, ProductName + getResources().getString(R.string.isremovedfromproductlist), Snackbar.LENGTH_SHORT);
                                    }

                                    // -- Snackbar. Show the "Product has been removed" message -- //
                                    snackbar.show();
                                }
                            });

                    // -- Snackbar. Show the "Product has been created" message -- //
                    snackbar.show();


                }

            }

        });

        // -- Firebase. Make a reference to retrieve store-list from the database -- //
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myProductList = database.getReference("product-list");

        // -- Listener to listen for changes on database for "product-list" -- //
        myProductList.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // -- Clear the original ArrayList for storeNames, so a new list is retrieved from database -- //
                storeNames.clear();

                // -- Running through each store in the product-list tree -- //
                for(DataSnapshot storeListSnap : dataSnapshot.getChildren()){

                    // -- Retrieving store names from product list. Adding them to ArrayList -- //
                    String storeName = storeListSnap.getKey().toString();
                    storeNames.add(storeName);

                }

                // -- Adapter to translate the ArrayList data to be shown in RecyclerView -- //
                CreateProduct_StoreToProduct_Adapter adapter = new CreateProduct_StoreToProduct_Adapter(getContext(), storeNames, adapterInterface);
                mStoreToProductRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    // -- Implementing interface to update fragment textEdit from adapter -- //
    CreateProduct_StoreToProduct_Adapter.myAdapterInterface adapterInterface = new CreateProduct_StoreToProduct_Adapter.myAdapterInterface() {
        @Override
        // -- Data retrieved from adapter when clicking on RecyclerView -- //
        //public void OnItemClicked(int item_id) {
        public void OnItemClicked(Store myStore) {

            // -- Setting EditText field to the clicked value -- //
            productStore.setText(myStore.getName());

            // -- Return to top of ScrollView after store is clicked -- //
            CreateProductScrollView.smoothScrollTo(0, 0);
        }
    };

    @Override
    public void onAttach(Context context) { super.onAttach(context);
    }

    @Override
    public void onDetach() { super.onDetach(); }

}
