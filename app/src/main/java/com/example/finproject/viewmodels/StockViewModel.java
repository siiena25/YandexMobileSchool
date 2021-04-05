package com.example.finproject.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.finproject.models.StockListElement;
import com.example.finproject.repo.Repository;

import java.util.ArrayList;

public class StockViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<StockListElement>> stocks;

    @NonNull
    private final Repository repo = Repository.getInstance();

    public StockViewModel(@NonNull Application application) {
        super(application);
        this.stocks = repo.getStocks();
    }

    @NonNull
    public MutableLiveData<ArrayList<StockListElement>> getStocks() {
        stocks = repo.getStocks();
        return stocks;
    }
}
