package com.example.finproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finproject.R;
import com.example.finproject.activities.MainActivity;
import com.example.finproject.adapters.StockAdapter;

public class SearchEmptyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StockAdapter stockAdapter = ((MainActivity) getActivity()).getStockAdapter();
        RecyclerView listViewStocks = ((MainActivity) getActivity()).getListViewStocks();

        listViewStocks.setVisibility(View.GONE);
        stockAdapter.setIsSelectAllStocks(false);
        stockAdapter.setIsFiltered(true);

        return inflater.inflate(R.layout.fragment_search_empty, container, false);
    }
}
