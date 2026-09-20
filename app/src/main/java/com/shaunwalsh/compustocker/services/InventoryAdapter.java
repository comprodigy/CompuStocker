package com.shaunwalsh.compustocker.services;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shaunwalsh.compustocker.R;
import com.shaunwalsh.compustocker.model.InventoryItem;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final List<InventoryItem> itemList;
    private final OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDelete(InventoryItem item, int position);
    }

    public InventoryAdapter(List<InventoryItem> itemList, OnItemDeleteListener deleteListener) {
        this.itemList = itemList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = itemList.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvPartNumber.setText("Part #: " + item.getPartNumber());
        holder.etQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvMinQuantity.setText(String.valueOf(item.getMinquantity()));

        holder.etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int newQty = Integer.parseInt(s.toString());
                    item.setQuantity(newQty);
                } catch (NumberFormatException e) {
                    item.setQuantity(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void removeItem(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
    }

    public void replaceItems(List<InventoryItem> items) {
        this.itemList.clear();
        if (items != null) {
            this.itemList.addAll(items);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvPartNumber, tvMinQuantity;
        EditText etQuantity;
        Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPartNumber = itemView.findViewById(R.id.tvPartNumber);
            etQuantity = itemView.findViewById(R.id.etQuantity);
            tvMinQuantity = itemView.findViewById(R.id.tvMinQuantity);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}