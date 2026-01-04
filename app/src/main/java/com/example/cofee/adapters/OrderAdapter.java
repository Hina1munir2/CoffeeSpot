package com.example.coffee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffee.R;
import com.example.coffee.models.Order;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orderList;
    private OrderClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OrderClickListener {
        void onOrderClick(int position);
        void onReorderClick(int position);
        void onTrackClick(int position);
        void onCancelClick(int position);
    }

    public OrderAdapter(Context context, List<Order> orderList, OrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        String shortOrderId = order.getId().length() > 8 ? 
            order.getId().substring(0, 8).toUpperCase() : order.getId().toUpperCase();
        holder.orderIdText.setText("Order #" + shortOrderId);
        
        if (order.getOrderDate() != null) {
            holder.orderDateText.setText(dateFormat.format(order.getOrderDate()));
        }
        
        holder.orderTotalText.setText(String.format("$%.2f", order.getTotal()));
        holder.orderStatusText.setText(order.getStatus());
        
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            StringBuilder itemsText = new StringBuilder();
            itemsText.append(order.getItems().size()).append(" item");
            if (order.getItems().size() > 1) itemsText.append("s");
            
            if (order.getItems().size() > 0) {
                itemsText.append(" • ").append(order.getItems().get(0).getProductName());
                if (order.getItems().size() > 1) {
                    itemsText.append(", +").append(order.getItems().size() - 1).append(" more");
                }
            }
            
            holder.orderItemsText.setText(itemsText.toString());
        }
        
        holder.reorderButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReorderClick(position);
            }
        });
        
        holder.trackButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(position);
            }
        });
        
        holder.cancelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(position);
            }
        });
        
        holder.cancelButton.setVisibility(
            order.getStatus().equals("Processing") ? View.VISIBLE : View.GONE
        );
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText;
        TextView orderDateText;
        TextView orderTotalText;
        TextView orderStatusText;
        TextView orderItemsText;
        Button reorderButton;
        Button trackButton;
        Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            orderItemsText = itemView.findViewById(R.id.orderItemsText);
            reorderButton = itemView.findViewById(R.id.reorderButton);
            trackButton = itemView.findViewById(R.id.trackButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}