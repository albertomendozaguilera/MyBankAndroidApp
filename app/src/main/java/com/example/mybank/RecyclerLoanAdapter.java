package com.example.mybank;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.restclient.dto.LoanDTO;
import com.example.mybank.restclient.dto.UserDTO;

import java.util.ArrayList;

public class RecyclerLoanAdapter extends RecyclerView.Adapter<RecyclerLoanAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<LoanDTO> loanList;
    private Context context;
    private int selectedAccount;
    private UserDTO user;

    public RecyclerLoanAdapter(ArrayList<LoanDTO> loanList, Context context, int accountId, UserDTO user) {
        this.loanList = loanList;
        this.context = context;
        this.selectedAccount = accountId;
        this.user = user;
    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public RecyclerLoanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_loans_item, parent, false);

        view.setOnClickListener(this);

        RecyclerLoanAdapter.ViewHolder vh = new RecyclerLoanAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerLoanAdapter.ViewHolder holder, final int position) {
        holder.tvDescription.setText(String.valueOf(loanList.get(position).getDescription()));
        holder.tvQuantity.setText(String.valueOf(loanList.get(position).getQuantity()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDescription, tvQuantity;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvLoanDescription);
            tvQuantity = itemView.findViewById(R.id.tvLoanQuantity);
        }
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }
}
