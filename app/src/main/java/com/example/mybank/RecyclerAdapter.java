package com.example.mybank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.restclient.dto.PaymentTransactionsDTO;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnClickListener{
    private ArrayList<PaymentTransactionsDTO> transactionsList;
    private Context context;
    private View.OnClickListener listener;

    public RecyclerAdapter(ArrayList <PaymentTransactionsDTO> transactionsList, Context context) {
        this.transactionsList = transactionsList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);

        view.setOnClickListener(this);

        RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.tvCost.setText(String.valueOf(transactionsList.get(position).getQuantity()));
        holder.tvConcept.setText(String.valueOf(transactionsList.get(position).getConcept()));
        holder.tvDate.setText(String.valueOf(transactionsList.get(position).getDatetime()));
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvCost, tvConcept, tvDate;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvConcept = itemView.findViewById(R.id.tvConcept);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }
}
