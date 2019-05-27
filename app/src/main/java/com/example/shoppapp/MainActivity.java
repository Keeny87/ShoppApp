package com.example.shoppapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// -- Remember to use Test Lab, used by teacher Martin on 1st of May -- //
public class MainActivity extends AppCompatActivity {

    // -- Firebase. Variable for database reference -- //
    private DatabaseReference mDatabaseReference;

    // -- Toolbar used in drawerLayout -- //
    private Toolbar toolbar;

    // -- Navigationview -- //
    private NavigationView navigationView;

    // -- Our layout for the navigationdrawer -- //
    private DrawerLayout drawerLayout;

    // -- Counting for how many times the timer has been hit -- //
    private int timerCounter = 0;

    // -- Selected shopping item from shopping list recycler. This is put here to update to basket -- //
    private String selectedStoreOnShoppingList;

    // -- Selected shopping item from shopping list recycler. This is put here to update to basket -- //
    private String selectedShoppingItemOnShoppingList;

    // -- Selected product store from product list -- //
    private String selectedStoreOnProductList;

    // -- Selected product from product list -- //
    private String selectedProductOnProductList;

    // -- Selected product store from product list -- //
    private String selectedStoreOnCreateShoppingItem;

    // -- Firebase authentication to login firebase users -- //
    private FirebaseAuth firebaseauth;

    private String SnackbarShoppingItemID;
    private String SnackbarShoppingItemName;
    private String SnackbarShoppingItemStore;
    private String SnackbarProductID;
    private String SnackbarProductName;
    private String SnackbarProductStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -- Firebase. Getting auth instance to communicate with firebase -- //
        firebaseauth = FirebaseAuth.getInstance();

        // -- Selected shopping item from spinner in shopping list fragment -- //
        selectedStoreOnShoppingList = "";

        // -- Set selected shopping item on shopping list -- //
        selectedShoppingItemOnShoppingList = "";

        // -- Firebase. Create database reference -- //
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // -- Call super class oncreate -- //
        super.onCreate(savedInstanceState);

        // -- Setting the content view -- //
        setContentView(R.layout.activity_main);

        // -- Initializing Toolbar and setting it as the actionbar -- //
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // -- Initializing the navigation view -- //
        navigationView = findViewById(R.id.navigation_view);

        // -- Listener. Listens for when navigation drawer items are clicked -- //
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {

                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        // -- If clicked menu item is not in checked state make it in checked state -- //
                        if (menuItem.isChecked())
                            menuItem.setChecked(false);
                        else
                            menuItem.setChecked(true);

                        // -- When drawer item is clicked, the drawer should close -- //
                        drawerLayout.closeDrawers();

                        // -- Check which navigation drawer item was clicked -- //
                        Fragment fragment = null;
                        String title= "";
                        switch (menuItem.getItemId()) {

                            // -- "Log in / Sign up" navigation item was clicked -- //
                            case R.id.main:
                                fragment = new HomeFragment();
                                title = getResources().getString(R.string.navdrawerlogInOrSignUp);
                                break;

                            // -- "Create shopping item" navigation item was clicked -- //
                            case R.id.createshoppingitem:
                                // -- Check if firebase user is signed in. If user is signed in, then retrieve fragment -- //
                                if(firebaseauth.getCurrentUser() != null){
                                    fragment = new CreateShoppingItemFragment();
                                    title = getResources().getString(R.string.navdrawercreateshoppingitem);
                                }
                                // -- If firebase user is not signed in, then do not retrieve fragment. User is navigated to original fragment -- //
                                else{
                                    Toast.makeText(getApplicationContext(), "User needs to be logged in to create shopping items", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            // -- "Shopping item list" navigation item was clicked -- //
                            case R.id.shoppingitemlist:
                                // -- Check if firebase user is signed in. If user is signed in, then retrieve fragment for shopping list -- //
                                if(firebaseauth.getCurrentUser() != null){
                                    fragment = new ShoppingItem_Recycler_Fragment();
                                    title = getResources().getString(R.string.navdrawershoppinglist);
                                }
                                // -- If firebase user is not signed in, then do not retrieve fragment. User is navigated to original fragment -- //
                                else{
                                    Toast.makeText(getApplicationContext(), "User needs to be logged in to access shopping list", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            // -- "Create product" navigation item was clicked -- //
                            case R.id.createproduct:
                                fragment = new CreateProductFragment();
                                title = getResources().getString(R.string.navdrawercreateproduct);
                                break;

                            // -- "Product list" navigation item was clicked -- //
                            case R.id.productlist:
                                fragment = new ProductRecyclerFragment();
                                title = getResources().getString(R.string.navdrawerproductlist);
                                break;

                            // -- If no navigation item is found return true -- //
                            default:
                                return true;

                        }

                        // -- This is run after fragment has been chosen -- //
                        if (fragment != null) {
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame, fragment);
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle(title);
                        }
                        return true;
                    }
                });

        // -- Initializing Drawer Layout and ActionBarToggle -- //
        drawerLayout = findViewById(R.id.drawer);

        // -- Overriding Drawer Navigation methods "onDrawerClosed" and "onDrawerOpened" -- //
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // -- Add listener to drawer layout -- //
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // -- Calling sync state to show hamburger icon -- //
        actionBarDrawerToggle.syncState();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // -- Setting startup fragment should be used when starting the app -- //
        fragmentTransaction.replace(R.id.frame, new HomeFragment());
        fragmentTransaction.commit(); //set starting fragment

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        firebaseauth.signOut();
        super.onStop();
    }

    public void AddProductToDatabase(String productName, String productBrand, String productLocation, String productStore, String productRegularPrice){
        Product newProduct = new Product(productName, productBrand, productLocation, productStore, productRegularPrice);
        //Toast.makeText(MainActivity.this, "Product " + productName + " is added to product list belonging to store " + productStore, Toast.LENGTH_SHORT).show();

        // -- Snackbar. Save the Unique ID given for the newly created storeID, to use in snackbar for "Remove Shopping Item" action -- //
        String ProductUniqueID = mDatabaseReference.push().getKey();
        setSnackbarProductID(ProductUniqueID);
        setSnackbarProductName(productName);
        setSnackbarProductStore(productStore);

        // -- Firebase. Adding product to product list -- //
        mDatabaseReference.child("product-list").child(productStore).child(ProductUniqueID).setValue(newProduct);

    }

    public void AddShoppingItemToDatabase(String shoppingItemName, String shoppingItemBrand, String shoppingItemLocation, String shoppingItemStore, String shoppingItemPrice, String shoppingItemOnSale, String ShoppingItemInBasket){

        // -- Parsing the String values from view to the correct data type in ShoppingItem class -- //
        double price = Double.parseDouble(shoppingItemPrice);
        boolean onSale = Boolean.parseBoolean(shoppingItemOnSale);
        boolean inBasket = Boolean.parseBoolean(ShoppingItemInBasket);

        // -- Creating shopping item to send to Firebase database -- //
        ShoppingItem newShoppingItem = new ShoppingItem(shoppingItemName, shoppingItemBrand, shoppingItemLocation, shoppingItemStore, price, onSale, inBasket);

        // -- If user is logged in, then create the shopping item at the current users shopping list -- //
        if(firebaseauth.getCurrentUser() != null) {

            // -- Retrieve currently logged in Firebase user's ID -- //
            String UserID = firebaseauth.getCurrentUser().getUid();

            // -- Snackbar. Save the Unique ID given for the newly created storeID, to use in snackbar for "Remove Shopping Item" action -- //
            String ShoppingItemUniqueID = mDatabaseReference.push().getKey();
            setSnackbarShoppingItemID(ShoppingItemUniqueID);
            setSnackbarShoppingItemName(shoppingItemName);
            setSnackbarShoppingItemStore(shoppingItemStore);

            // -- Firebase. Adding shopping item to shopping list -- //
            mDatabaseReference.child(UserID).child("shopping-list").child(newShoppingItem.getStore()).child(ShoppingItemUniqueID).setValue(newShoppingItem);

        }

    }

    // -- Delete the current shopping list belonging to logged in user -- //
    public void deleteCurrentShoppingList(String shoppingItemStore){
        String UserID = firebaseGetUserID();
        if(!UserID.equals("")){
            mDatabaseReference.child(UserID)
                .child("shopping-list")
                .child(shoppingItemStore)
                .removeValue();
        }
    }

    // -- Delete the current shopping item belonging to logged in user -- //
    public void deleteCurrentShoppingItem(String storeName, String shoppingItemID){
        String UserID = firebaseGetUserID();
        if(!UserID.equals("")){
            mDatabaseReference.child(UserID)
                    .child("shopping-list")
                    .child(storeName)
                    .child(shoppingItemID)
                    .removeValue();
        }
    }

    // -- Delete the current product list -- //
    public void deleteCurrentProductList(String productStore){
        mDatabaseReference
                .child("product-list")
                .child(productStore)
                .removeValue();
    }

    // -- Delete the current product -- //
    public void deleteCurrentProduct(String productStore, String productID){
        mDatabaseReference
                .child("product-list")
                .child(productStore)
                .child(productID)
                .removeValue();
    }

    public void putShoppingItemIntoBasket(String shoppingItemKey, boolean ItemInBasket){

        if(firebaseIsUserLoggedIn() == true) {

            String firebaseUserID = firebaseGetUserID();

            if(ItemInBasket == false) {
                mDatabaseReference.child(firebaseUserID).child("shopping-list").child(selectedStoreOnShoppingList).child(shoppingItemKey).child("inBasket").setValue("true");
            }
            else{
                mDatabaseReference.child(firebaseUserID).child("shopping-list").child(selectedStoreOnShoppingList).child(shoppingItemKey).child("inBasket").setValue("false");
            }
        }

    }

    // -- Shopping list getting store list, to set spinner products in basket -- //
    public String getSelectedStoreOnShoppingList() {
        return selectedStoreOnShoppingList;
    }

    // -- Shopping list setting store list, to set spinner products in basket -- //
    public void setSelectedStoreOnShoppingList(String selectedStoreOnShoppingList) {
        this.selectedStoreOnShoppingList = selectedStoreOnShoppingList;
    }

    // -- Shopping list getting shopping item -- //
    public String getSelectedShoppingItemOnShoppingList() {
        return selectedShoppingItemOnShoppingList;
    }

    // -- Shopping list setting store list, to set spinner products in basket -- //
    public void setSelectedShoppingItemOnShoppingList(String selectedShoppingItemOnShoppingList) {
        this.selectedShoppingItemOnShoppingList = selectedShoppingItemOnShoppingList;
    }

    // -- Get selected store on product list -- //
    public String getSelectedStoreOnProductList() {
        return selectedStoreOnProductList;
    }

    // -- Set selected store on product list -- //
    public void setSelectedStoreOnProductList(String selectedStoreOnProductList) {
        this.selectedStoreOnProductList = selectedStoreOnProductList;
    }

    // -- Get selected store on product list -- //
    public String getSelectedProductOnProductList() {
        return selectedProductOnProductList;
    }

    // -- Set selected store on product list -- //
    public void setSelectedProductOnProductList(String selectedProductOnProductList) {
        this.selectedProductOnProductList = selectedProductOnProductList;
    }

    // -- Get selected store to display on create shopping item spinner -- //
    public String getSelectedStoreOnCreateShoppingItem() {
        return selectedStoreOnCreateShoppingItem;
    }

    // -- Set selected store to display on create shopping item spinner -- //
    public void setSelectedStoreOnCreateShoppingItem(String selectedStoreOnCreateShoppingItem) {
        this.selectedStoreOnCreateShoppingItem = selectedStoreOnCreateShoppingItem;
    }

    // -- Firebase user login -- //
    public void firebaseLoginUser(String email, String password) {

        // -- Checking if EditText fields has values entered -- //
        if(email != null && !email.isEmpty() && password != null && !password.isEmpty()) {

            // -- String to display in toast when logged in-- //
            final String myEmail = email;

            // -- Use firebaseauth to log in with the entered EditText values -- //
            firebaseauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // -- If user is logged in successfully -- //
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.loggedInAs) + myEmail, Toast.LENGTH_SHORT).show();
                    }

                    // -- If user login failed -- //
                    else{
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.couldNotLogInUser), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    // -- Firebase. Creating a new user -- //
    public void FirebaseCreateLoginUser(String email, String password) {

        // -- String to display in toast when creating user -- //
        final String myEmail = email;

        // -- Use firebaseauth to create with the entered EditText values -- //
        firebaseauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // -- If user is created successfully -- //
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.userIsCreated) + myEmail, Toast.LENGTH_SHORT).show();
                }

                // -- If user creation failed -- //
                else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.couldNotCreateUser), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // -- Firebase. Check if user is logged in -- //
    public boolean firebaseIsUserLoggedIn() {
        if(firebaseauth.getCurrentUser() != null) {
            return true;
        }
        return false;
    }

    // -- Firebase. Sign out logged in user -- //
    public void firebaseSignOut() {
        FirebaseAuth.getInstance().signOut();
    }


    public String firebaseGetUserID() {
        if(firebaseauth.getCurrentUser() != null) {
            return firebaseauth.getCurrentUser().getUid();
        }
        return "";
    }

    // -- Firebase. Retrieve userName of logged in user -- //
    public String firebaseGetCurrentUserName() {
        if(firebaseauth.getCurrentUser() != null) {
            String email = firebaseauth.getCurrentUser().getEmail();
            return email;
        }
        return null;
    }

    public String getSnackbarShoppingItemID() {
        return SnackbarShoppingItemID;
    }

    public void setSnackbarShoppingItemID(String snackbarShoppingItemID) {
        SnackbarShoppingItemID = snackbarShoppingItemID;
    }

    public String getSnackbarShoppingItemStore() {
        return SnackbarShoppingItemStore;
    }

    public void setSnackbarShoppingItemStore(String snackbarShoppingItemStore) {
        SnackbarShoppingItemStore = snackbarShoppingItemStore;
    }

    public String getSnackbarProductID() {
        return SnackbarProductID;
    }

    public void setSnackbarProductID(String snackbarProductID) {
        SnackbarProductID = snackbarProductID;
    }

    public String getSnackbarProductName() {
        return SnackbarProductName;
    }

    public void setSnackbarProductName(String snackbarProductName) {
        SnackbarProductName = snackbarProductName;
    }

    public String getSnackbarProductStore() {
        return SnackbarProductStore;
    }

    public void setSnackbarProductStore(String snackbarProductStore) {
        SnackbarProductStore = snackbarProductStore;
    }

    public boolean removeProduct(String snackbarShoppingItemID, String snackbarShoppingItemStore) {

        // -- Firebase. Remove selected product -- //
        mDatabaseReference
                .child("product-list")
                .child(snackbarShoppingItemStore)
                .child(snackbarShoppingItemID)
                .removeValue();

        // -- Return true to indicate that the shopping item is successfully removed -- //
        return true;
    }

    // -- Remove Shopping Item for the currently logged in Firebase user -- //
    public boolean removeShoppingItemForLoggedInUser(String snackbarShoppingItemID, String snackbarShoppingItemStore) {

        String UserID = firebaseGetUserID();

        // -- Check if any Firebase user is logged in -- //
        if(!UserID.equals("")){

            // -- Firebase. Remove selected shopping item on selected store -- //
            mDatabaseReference.child(UserID)
                    .child("shopping-list")
                    .child(snackbarShoppingItemStore)
                    .child(snackbarShoppingItemID)
                    .removeValue();

            // -- Return true to indicate that the shopping item is successfully removed -- //
            return true;
        }

        // -- Return true to indicate error and that the shopping item is not removed -- //
        return false;
    }

    public String getSnackbarShoppingItemName() {
        return SnackbarShoppingItemName;
    }

    public void setSnackbarShoppingItemName(String snackbarShoppingItemName) {
        SnackbarShoppingItemName = snackbarShoppingItemName;
    }

    public void showAlertDialogButtonClickedDeleteCurrentShoppingList(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.shoppingListAlertBoxTitleText);
        builder.setMessage(R.string.shoppingListAlertBoxText);

        builder.setPositiveButton(R.string.shoppingListAlertBoxPositiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentShoppingList(getSelectedStoreOnShoppingList());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.shoppingListDeleted) + selectedStoreOnShoppingList, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.shoppingListAlertBoxNegativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showAlertDialogButtonClickedDeleteCurrentProductList(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.productListAlertBoxTitleText);
        builder.setMessage(R.string.productListAlertBoxText);

        builder.setPositiveButton(R.string.productListAlertBoxPositiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentProductList(getSelectedStoreOnProductList());
                Toast.makeText(getApplicationContext(), "Product list deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.productListAlertBoxNegativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showAlertDialogButtonClickedDeleteCurrentProduct(String productID) {

        setSelectedProductOnProductList(productID);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.productAlertBoxTitleText);
        builder.setMessage(R.string.productAlertBoxText);

        builder.setPositiveButton(R.string.productAlertBoxPositiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentProduct(getSelectedStoreOnProductList(), getSelectedProductOnProductList());
                Toast.makeText(getApplicationContext(), R.string.productDeleted, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.productAlertBoxNegativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showAlertDialogButtonClickedDeleteCurrentShoppingItem(String shoppingItemID) {

        setSelectedShoppingItemOnShoppingList(shoppingItemID);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.shoppingitemAlertBoxTitleText);
        builder.setMessage(R.string.shoppingitemAlertBoxText);

        builder.setPositiveButton(R.string.shoppingitemAlertBoxPositiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentShoppingItem(getSelectedStoreOnShoppingList(), getSelectedShoppingItemOnShoppingList());
                Toast.makeText(getApplicationContext(), R.string.shoppingitemDeleted, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.shoppingitemAlertBoxNegativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}