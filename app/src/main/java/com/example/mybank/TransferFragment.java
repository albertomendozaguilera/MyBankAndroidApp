package com.example.mybank;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mybank.restclient.dto.UserDTO;

public class TransferFragment extends Fragment {

    UserDTO user;
    CardView btAccount, btPhone;

    public TransferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        if (getArguments() != null){
            user = (UserDTO) getArguments().getSerializable("user");
        }

        btAccount = view.findViewById(R.id.btAccount);
        btAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), AddTransferByAccount.class);
                System.out.println(user.getName());
                i.putExtra("user", user);
                startActivity(i);
            }
        });
        btPhone = view.findViewById(R.id.btPhone);
        btPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("user", user);
                startActivity(i);
            }
        });
        return view;
    }
}
