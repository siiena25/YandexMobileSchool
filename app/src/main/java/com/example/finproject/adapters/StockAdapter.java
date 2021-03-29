package com.example.finproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> implements Filterable {
    private final LayoutInflater inflater;
    private final Context context;
    private final OnStockListener onStockListener;

    private ArrayList<StockListElement> stocks;
    private ArrayList<StockListElement> filterStocks;
    private ArrayList<Integer> indexOfFilterStocks;
    private Set<String> favStocks;

    private int colorOfChange, i = 0, max = -1;
    private String textChange;
    private boolean isSelectAllStocks = true, isSelectFavouriteStocks = false, isFiltered = false, isSetFavStocks = false;

    String[] tickers = { "YNDX", "AAPL", "GOOGL", "AMZN", "BAC", "MSFT", "TSLA", "MA", "APPN",
            "APPF", "FSLR", "BABA", "FB", "NOK", "GM", "BIDU", "INTC", "AMD", "V" };

    int[] images = { R.drawable.ic_yndx, R.drawable.ic_aapl, R.drawable.ic_googl, R.drawable.ic_amzn,
            R.drawable.ic_bac, R.drawable.ic_msft, R.drawable.ic_tsla, R.drawable.ic_ma,
            R.drawable.ic_appn, R.drawable.ic_appf, R.drawable.ic_fslr, R.drawable.ic_baba,
            R.drawable.ic_fb, R.drawable.ic_nokia, R.drawable.ic_gm, R.drawable.ic_bidu,
            R.drawable.ic_intc, R.drawable.ic_amd, R.drawable.ic_v };

    int elementBackgroundGray = R.drawable.round_form_gray;
    int elementBackgroundWhite = R.drawable.round_form_white;

    public StockAdapter(Context context, OnStockListener onStockListener, Set<String> favStocks) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.stocks = new ArrayList<>();
        this.filterStocks = new ArrayList<>();
        this.indexOfFilterStocks = new ArrayList<>();
        this.favStocks = favStocks;
        this.onStockListener = onStockListener;

        for (String ticker : tickers) {
            ParseStocks parseStocks = new ParseStocks(ticker);
            parseStocks.execute();
        }

        for (String i : favStocks) {
            int index = Integer.parseInt(i);
            if (max < index) {
                max = index;
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
        StockListElement currentStock = null;
        if (isSelectAllStocks) {
            currentStock = stocks.get(position);
        }
        else if (isFiltered) {
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
        holder.stockStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStockListener.onStarClick(v, position);
            }
        });
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
        StockListElement currentStock = null;
        if (isSelectAllStocks) {
            currentStock = stocks.get(position);
        }
        else if (isFiltered) {
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

    /*public void setItems(ArrayList<StockListElement> stocks) {
        this.stocks.addAll(stocks);
        notifyDataSetChanged();
    }

    public void clearItems() {
        stocks.clear();
        notifyDataSetChanged();
    }*/

    @Override
    public int getItemCount() {
        ArrayList<StockListElement> currentStockList = null;
        if (isSelectAllStocks) {
            currentStockList = stocks;
        }
        else if (isFiltered) {
            currentStockList = filterStocks;
        }
        return currentStockList.size();
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

    private class ParseStocks extends AsyncTask<Void, Void, Void> {
        String ticker;
        Stock stock;

        ParseStocks(String ticker) {
            this.ticker = ticker;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                stock = YahooFinance.get(ticker);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StockListElement stockListElement = new StockListElement();
            stockListElement.setStock(stock);
            stocks.add(stockListElement);
            stocks.get(i).setImageId(images[i]);
            i++;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            StockAdapter.this.notifyDataSetChanged();

            if (!isSetFavStocks) {
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
        }
    }

    public interface OnStockListener {
        void onStockClick(View view, int position);
        void onStarClick(View view, int position);
    }

    public Stock getStock(int position) {
        return stocks.get(position).getStock();
    }

    public StockListElement getStockListElement(int position) {
        return stocks.get(position);
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
}