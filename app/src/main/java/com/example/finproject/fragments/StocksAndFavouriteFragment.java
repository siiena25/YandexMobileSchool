package com.example.finproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finproject.R;
import com.example.finproject.activities.MainActivity;
import com.example.finproject.adapters.StockAdapter;

public class StocksAndFavouriteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StockAdapter stockAdapter = ((MainActivity) getActivity()).getStockAdapter();
        RecyclerView listViewStocks = ((MainActivity) getActivity()).getListViewStocks();
        listViewStocks.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_stocks_and_favourite, container, false);
    }
}
