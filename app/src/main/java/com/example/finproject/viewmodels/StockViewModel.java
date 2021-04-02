package com.example.finproject.viewmodels;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finproject.R;
import com.example.finproject.adapters.StockAdapter;
import com.example.finproject.models.StockListElement;

import java.io.IOException;
import java.util.ArrayList;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class StockViewModel extends ViewModel {

    private MutableLiveData<ArrayList<StockListElement>> stocks;

    String[] tickers = { "YNDX", "AAPL", "GOOGL", "AMZN", "BAC", "MSFT", "TSLA", "MA", "APPN",
            "APPF", "FSLR", "BABA", "FB", "NOK", "GM", "BIDU", "INTC", "AMD", "V" };

    int[] images = { R.drawable.ic_yndx, R.drawable.ic_aapl, R.drawable.ic_googl, R.drawable.ic_amzn,
            R.drawable.ic_bac, R.drawable.ic_msft, R.drawable.ic_tsla, R.drawable.ic_ma,
            R.drawable.ic_appn, R.drawable.ic_appf, R.drawable.ic_fslr, R.drawable.ic_baba,
            R.drawable.ic_fb, R.drawable.ic_nokia, R.drawable.ic_gm, R.drawable.ic_bidu,
            R.drawable.ic_intc, R.drawable.ic_amd, R.drawable.ic_v };

    private int i = 0;

    public StockViewModel() {
        this.stocks = new MutableLiveData<>();
    }

    public void updateStocks(ArrayList<StockListElement> stocks) {
        this.stocks.postValue(getStocks().getValue());
    }

    public MutableLiveData<ArrayList<StockListElement>> getStocks() {
        if (stocks == null) {
            stocks = new MutableLiveData<>();
            for (String ticker : tickers) {
                ParseStocks parseStocks = new ParseStocks(ticker);
                parseStocks.execute();
            }
            System.out.println(stocks.getValue().size() + "size");
        }
        return stocks;
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
            stocks.getValue().add(stockListElement);
            stocks.getValue().get(i).setImageId(images[i]);
            i++;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            System.out.println("123");
        }
    }
}
