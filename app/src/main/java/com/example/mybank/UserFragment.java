package com.example.mybank;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mybank.restclient.dto.UserDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("users");
    private UserDTO user;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private CardView btLogOut;
    private TextView tvName, tvEmail, tvPhone, tvAddress;


    public UserFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (UserDTO) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        editor = preferences.edit();

        setViewsById(view);

        setInfo();

        return view;
    }

    private void setViewsById(View view) {
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.textView37);
        tvPhone = view.findViewById(R.id.textView35);
        tvPhone.setOnClickListener(this);
        tvAddress = view.findViewById(R.id.textView36);
        tvAddress.setOnClickListener(this);
        btLogOut = view.findViewById(R.id.btLogOutUser);
        btLogOut.setOnClickListener(this);
    }

    private void setInfo(){
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());

        // Read from the database
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean checkOk = false;
                for (DataSnapshot dataSnapshott : dataSnapshot.getChildren()){
                    if (dataSnapshott.getKey().equals(mAuth.getUid())){
                        if (dataSnapshott.child("phone").getValue()!=null) {
                            tvPhone.setText(dataSnapshott.child("phone").getValue().toString());
                        }else{
                            tvPhone.setHint("+ add phone");
                        }
                        if (dataSnapshott.child("address").getValue()!=null) {
                            tvAddress.setText(dataSnapshott.child("address").getValue().toString());
                        }else{
                            tvAddress.setHint("+ add address");
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }


    private void showAlertDialog(final TextView tv, String title, final String option){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(title);

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity().getApplicationContext());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                tv.setText(input.getText());
                if (option.equals("phone")){
                    dbr.child(mAuth.getUid()).child("phone").setValue(input.getText().toString());
                }else{
                    dbr.child(mAuth.getUid()).child("address").setValue(input.getText().toString());
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textView35:
                showAlertDialog(tvPhone, "Enter a phone", "phone");
                break;
            case R.id.textView36:
                showAlertDialog(tvPhone, "Enter your address", "address");
                break;
            case R.id.btLogOutUser:
                mAuth.signOut();
                editor.putString("selectedAccount","false");
                editor.putBoolean("session", false);
                editor.commit();
                getActivity().finish();
                Intent i = new Intent(getActivity().getApplicationContext(), LogIn.class);
                startActivity(i);
                break;
        }
    }
}
