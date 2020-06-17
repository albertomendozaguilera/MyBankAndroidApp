package com.example.mybank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.restclient.dto.ReceiptDTO;

import java.util.ArrayList;

public class RecyclerReceiptAdapter extends RecyclerView.Adapter<RecyclerReceiptAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<ReceiptDTO> receiptList;
    private Context context;

    public RecyclerReceiptAdapter (ArrayList<ReceiptDTO> receiptList, Context context){
        this.receiptList = receiptList;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public RecyclerReceiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_receipt_item, parent, false);

        view.setOnClickListener(this);

        RecyclerReceiptAdapter.ViewHolder vh = new RecyclerReceiptAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerReceiptAdapter.ViewHolder holder, int position) {
        holder.tvNumReceipt.setText(String.valueOf(receiptList.get(position).getReceiptNum()));
        holder.tvQuantityReceipt.setText(String.valueOf(receiptList.get(position).getReceiptAmount()));
        holder.checkPayed.setText(R.string.payed);
        if (receiptList.get(position).getPayed().equals(1)){
            holder.checkPayed.setChecked(true);
            holder.checkPayed.setEnabled(false);
        }else{
            holder.checkPayed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, holder.checkPayed.isChecked()+"", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNumReceipt, tvQuantityReceipt;
        CheckBox checkPayed;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvNumReceipt = itemView.findViewById(R.id.tvIdReceipt);
            tvQuantityReceipt = itemView.findViewById(R.id.tvQuantityReceipt);
            checkPayed = itemView.findViewById(R.id.checkPayed);
        }
    }
}
