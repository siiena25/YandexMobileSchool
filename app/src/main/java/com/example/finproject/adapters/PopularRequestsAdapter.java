package com.example.finproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finproject.R;
import com.example.finproject.models.StockListElement;

import java.util.ArrayList;

import yahoofinance.Stock;

public class PopularRequestsAdapter extends RecyclerView.Adapter<PopularRequestsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final OnStockListener onStockListener;
    private ArrayList<StockListElement> popularStocks;

    public PopularRequestsAdapter(Context context, OnStockListener onStockListener, ArrayList<StockListElement> popularStocks) {
        this.inflater = LayoutInflater.from(context);
        this.popularStocks = popularStocks;
        this.onStockListener = onStockListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.popular_requests_list_element, parent, false);
        return new PopularRequestsAdapter.ViewHolder(view, onStockListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stock stock = popularStocks.get(position).getStock();
        holder.stockTickerText.setText(stock.getSymbol());
    }

    @Override
    public int getItemCount() {
        return popularStocks.size();
    }

    public StockListElement getStockListElement(int position) {
        return popularStocks.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView stockTickerText;
        OnStockListener onStockListener;

        public ViewHolder(View view, OnStockListener onStockListener) {
            super(view);
            stockTickerText = view.findViewById(R.id.popular_stock_ticker);
            this.onStockListener = onStockListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStockListener.onPopularStockClick(v, getAdapterPosition());
        }
    }

    public interface OnStockListener {
        void onPopularStockClick(View view, int position);
    }
}
