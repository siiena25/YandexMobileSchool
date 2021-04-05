package com.example.finproject.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.finproject.R;
import com.example.finproject.models.StockListElement;

import java.io.IOException;
import java.util.ArrayList;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class Repository {
    private static Repository instance;

    String[] tickers = { "YNDX", "AAPL", "GOOGL", "AMZN", "BAC", "MSFT", "TSLA", "MA", "APPN",
            "APPF", "FSLR", "BABA", "FB", "NOK", "GM", "BIDU", "INTC", "AMD", "V" };

    int[] images = { R.drawable.ic_yndx, R.drawable.ic_aapl, R.drawable.ic_googl, R.drawable.ic_amzn,
            R.drawable.ic_bac, R.drawable.ic_msft, R.drawable.ic_tsla, R.drawable.ic_ma,
            R.drawable.ic_appn, R.drawable.ic_appf, R.drawable.ic_fslr, R.drawable.ic_baba,
            R.drawable.ic_fb, R.drawable.ic_nokia, R.drawable.ic_gm, R.drawable.ic_bidu,
            R.drawable.ic_intc, R.drawable.ic_amd, R.drawable.ic_v };

    private int i = 0;

    @NonNull
    private static final MutableLiveData<ArrayList<StockListElement>> stocks = new MutableLiveData<>();

    private ArrayList<StockListElement> updatedStocks;

    public static Repository getInstance() {
        if (instance == null) {
            synchronized (Repository.class) {
                if(instance == null) {
                    instance = new Repository();
                }
            }
        }
        return instance;
    }

    @NonNull
    public MutableLiveData<ArrayList<StockListElement>> getStocks() {
        stocks.setValue(updatedStocks);
        return stocks;
    }

    public void loadStocks() {
        updatedStocks = new ArrayList<>();
        i = 0;
        final Stock[] stock = new Stock[1];
        new Thread(() -> {
            for (String ticker : tickers) {
                try {
                    stock[0] = YahooFinance.get(ticker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StockListElement stockListElement = new StockListElement();
                stockListElement.setStock(stock[0]);
                updatedStocks.add(stockListElement);
                updatedStocks.get(i).setImageId(images[i]);
                i++;
            }
        }).start();
    }
}
