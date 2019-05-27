package com.example.shoppapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    private TextView textViewLogin;
    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private Button buttonCreateUser;
    private Button buttonSignOut;

    public HomeFragment() { } // Required empty public constructor

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.homefragment, container, false);

        textViewLogin = (TextView) rootView.findViewById(R.id.textViewLogin);
        editTextLoginEmail = (EditText) rootView.findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = (EditText) rootView.findViewById(R.id.editTextLoginPassword);
        buttonLogin = (Button) rootView.findViewById(R.id.buttonLogin);
        buttonCreateUser = (Button) rootView.findViewById(R.id.buttonCreateLogin);
        buttonSignOut = (Button) rootView.findViewById(R.id.buttonSignOut);

        // -- When clicking the "login" button -- //
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // -- If EditText email has not been set -- //
                if(TextUtils.isEmpty(editTextLoginEmail.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter mail", Toast.LENGTH_SHORT).show();
                }

                // -- If EditText password has not been set -- //
                else if(TextUtils.isEmpty(editTextLoginPassword.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter password", Toast.LENGTH_SHORT).show();
                }

                // -- If email and password has been entered correctly -- //
                else {
                    textViewLogin.setText(getResources().getString(R.string.textViewLogin));
                    String loginEmail = editTextLoginEmail.getText().toString().trim();
                    String loginPassword = editTextLoginPassword.getText().toString().trim();
                    ((MainActivity) getActivity()).firebaseLoginUser(loginEmail, loginPassword);
                }
            }

        });

        // -- When clicking the "create user" button -- //
        buttonCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // -- If email EditText email has not been set -- //
                if(TextUtils.isEmpty(editTextLoginEmail.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter mail", Toast.LENGTH_SHORT).show();
                }

                // -- If email EditText password has not been set -- //
                else if(TextUtils.isEmpty(editTextLoginPassword.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter password", Toast.LENGTH_SHORT).show();
                }

                else {

                    String loginEmail = editTextLoginEmail.getText().toString().trim();
                    String loginPassword = editTextLoginPassword.getText().toString().trim();
                    ((MainActivity)getActivity()).FirebaseCreateLoginUser(loginEmail, loginPassword);

                }

            }

        });

        // -- When clicking the "sign out button -- //
        buttonSignOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).firebaseSignOut();
                textViewLogin.setText(getResources().getString(R.string.textViewLogin));
            }

        });

        return rootView;

    }
}
