package com.example.finproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finproject.R;
import com.example.finproject.models.StockListElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import yahoofinance.Stock;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> implements Filterable {
    private final LayoutInflater inflater;
    private final Context context;
    private final OnStockListener onStockListener;

    private ArrayList<StockListElement> stocks;
    private final ArrayList<StockListElement> filterStocks;
    private final ArrayList<Integer> indexOfFilterStocks;
    private final Set<String> favStocks;

    private int colorOfChange, max = -1;
    private String textChange;
    private boolean isSelectAllStocks = true, isSelectFavouriteStocks = false, isFiltered = false, isSetFavStocks = false;

    int elementBackgroundGray = R.drawable.round_form_gray;
    int elementBackgroundWhite = R.drawable.round_form_white;
    private final int[] positionsOfPopularStocks = { 1, 3, 2, 6, 5, 10, 11, 12, 7 };

    public StockAdapter(Context context, OnStockListener onStockListener, Set<String> favStocks) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.stocks = new ArrayList<>();
        this.filterStocks = new ArrayList<>();
        this.indexOfFilterStocks = new ArrayList<>();
        this.favStocks = favStocks;
        this.onStockListener = onStockListener;

        if (favStocks != null) {
            for (String i : favStocks) {
                int index = Integer.parseInt(i);
                if (max < index) {
                    max = index;
                }
            }
        }
    }

    public void setAllStocks() {
        this.isSelectAllStocks = true;
        this.isSelectFavouriteStocks = false;
        this.isFiltered = false;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.stock_list_element, parent, false);
        return new ViewHolder(view, onStockListener);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull StockAdapter.ViewHolder holder, int position) {
        StockListElement currentStock;
        if (isSelectAllStocks) {
            currentStock = stocks.get(position);
        }
        else {
            currentStock = filterStocks.get(position);
        }

        setTextAndColorChange(currentStock.getStock());
        holder.stockStarButton.setBackground(setStarColor(position));
        holder.stockListElement.setBackgroundResource((position % 2 == 0) ? elementBackgroundGray : elementBackgroundWhite);
        holder.stockImageView.setImageResource(currentStock.getImageId());
        holder.stockTickerText.setText(currentStock.getStock().getSymbol());
        holder.stockNameText.setText(currentStock.getStock().getName());
        holder.stockPriceText.setText("$" + String.format("%.2f", currentStock.getStock().getQuote().getPrice().doubleValue()));
        holder.stockChangeText.setText(textChange + " (" + Math.abs(currentStock.getStock().getQuote().getChangeInPercent().doubleValue()) + "%)");
        holder.stockChangeText.setTextColor(context.getResources().getColor(colorOfChange));
        holder.stockStarButton.setOnClickListener(v -> onStockListener.onStarClick(v, position));
    }

    @SuppressLint("DefaultLocale")
    private void setTextAndColorChange(Stock currentStock) {
        BigDecimal zero = new BigDecimal("0");
        BigDecimal change = currentStock.getQuote().getChange();
        if (change.compareTo(zero) > 0) {
            textChange = "+$" + String.format("%.2f", change);
            colorOfChange = R.color.colorGreen;
        }
        else if (change.compareTo(zero) == 0) {
            textChange = "$" + String.format("%.2f", change);
            colorOfChange = R.color.colorBlack;
        }
        else {
            textChange = "-$" + String.format("%.2f", Math.abs(change.doubleValue()));
            colorOfChange = R.color.colorRed;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable setStarColor(int position) {
        Drawable color;
        StockListElement currentStock;
        if (isSelectAllStocks) {
            currentStock = stocks.get(position);
        }
        else {
            currentStock = filterStocks.get(position);
        }

        color = (currentStock.getIsStarSelected()) ?
                context.getResources().getDrawable(R.drawable.ic_favourite_select) :
                context.getResources().getDrawable(R.drawable.ic_favourite_unselect);
        return color;
    }

    public void setIsFiltered(boolean value) {
        this.isFiltered = value;
    }

    public void setIsSelectAllStocks(boolean value) {
        this.isSelectAllStocks = value;
    }

    public void setIsSelectFavouriteStocks(boolean value) {
        this.isSelectFavouriteStocks = value;
    }

    public void setIsSelectStar(int position, boolean value) {
        int pos = position;
        if (!isSelectAllStocks) {
            pos = indexOfFilterStocks.get(position);
        }
        stocks.get(pos).setStarSelected(value);
    }

    public boolean getIsSelectStar(int position) {
        int pos = position;
        if (!isSelectAllStocks) {
            pos = indexOfFilterStocks.get(position);
        }
        return stocks.get(pos).getIsStarSelected();
    }

    @Override
    public int getItemCount() {
        ArrayList<StockListElement> currentStockList = null;
        if (isSelectAllStocks) {
            currentStockList = stocks;
        }
        else if (isFiltered) {
            currentStockList = filterStocks;
        }
        if (currentStockList != null) {
            return currentStockList.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<StockListElement> filteredList = new ArrayList<>();
            indexOfFilterStocks.clear();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(stocks);
            }
            else {
                if (isSelectFavouriteStocks) {
                    for (int i = 0; i < stocks.size(); i++) {
                        StockListElement stock = stocks.get(i);
                        if (stock.getIsStarSelected()) {
                            filteredList.add(stock);
                            indexOfFilterStocks.add(i);
                        }
                    }
                    isSelectFavouriteStocks = false;
                    isFiltered = true;
                }
                else {
                    for (int i = 0; i < stocks.size(); i++) {
                        StockListElement stock = stocks.get(i);
                        if (stock.getStock().getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredList.add(stock);
                            indexOfFilterStocks.add(i);
                        }
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterStocks.clear();
            filterStocks.addAll((Collection<? extends StockListElement>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView stockImageView;
        final TextView stockTickerText, stockNameText, stockPriceText, stockChangeText;
        final ConstraintLayout stockListElement;
        final ImageButton stockStarButton;
        OnStockListener onStockListener;

        public ViewHolder(View view, OnStockListener onStockListener) {
            super(view);

            stockListElement = view.findViewById(R.id.stock_list_element);
            stockImageView = view.findViewById(R.id.stock_image);
            stockTickerText = view.findViewById(R.id.stock_ticker);
            stockNameText = view.findViewById(R.id.stock_name);
            stockPriceText = view.findViewById(R.id.stock_price);
            stockChangeText = view.findViewById(R.id.stock_change);
            stockStarButton = view.findViewById(R.id.stock_star);

            this.onStockListener = onStockListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStockListener.onStockClick(v, getAdapterPosition());
        }
    }

    public interface OnStockListener {
        void onStockClick(View view, int position);
        void onStarClick(View view, int position);
    }

    public StockListElement getStockListElement(int position) {
        StockListElement stock;
        if (isSelectAllStocks) {
            stock = stocks.get(position);
        }
        else {
            stock = stocks.get(indexOfFilterStocks.get(position));
        }
        return stock;
    }

    public Set<String> getFavouriteStocks() {
        Set<String> favStocks = new LinkedHashSet<>();
        for (int i = 0; i < stocks.size(); i++) {
            if (stocks.get(i).getIsStarSelected()) {
                favStocks.add(Integer.toString(i));
            }
        }
        return favStocks;
    }

    public void setStocks(ArrayList<StockListElement> stocks) {
        this.stocks = stocks;

        if (favStocks != null && !isSetFavStocks && stocks != null) {
            if (stocks.size() > max) {
                for (String index : favStocks) {
                    int i = Integer.parseInt(index);
                    if (stocks.size() > i) {
                        stocks.get(i).setStarSelected(true);
                    }
                    else return;
                }
                notifyDataSetChanged();
                isSetFavStocks = true;
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<StockListElement> getPopularStocks() {
        ArrayList<StockListElement> popularStocks = new ArrayList<>();
        for (int position = 0; position < positionsOfPopularStocks.length; position++) {
            popularStocks.add(stocks.get(positionsOfPopularStocks[position]));
        }
        return popularStocks;
    }

    public int[] getPositionsOfPopularStocks() {
        return positionsOfPopularStocks;
    }
}