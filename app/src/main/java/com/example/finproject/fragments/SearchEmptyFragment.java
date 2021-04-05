package com.example.finproject.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finproject.R;
import com.example.finproject.activities.MainActivity;
import com.example.finproject.activities.StockActivity;
import com.example.finproject.adapters.PopularRequestsAdapter;
import com.example.finproject.adapters.StockAdapter;
import com.example.finproject.models.StockListElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class SearchEmptyFragment extends Fragment implements PopularRequestsAdapter.OnStockListener {

    private RecyclerView recyclerViewPopularStocks;
    private PopularRequestsAdapter popularRequestsAdapter;
    private ArrayList<StockListElement> popularStocks;
    private final int REQUEST_CODE = 1;
    private int pos = -1;
    private StockAdapter stockAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_empty, container, false);
        stockAdapter = ((MainActivity) Objects.requireNonNull(getActivity())).getStockAdapter();
        RecyclerView listViewStocks = ((MainActivity) getActivity()).getRecyclerViewStocks();

        listViewStocks.setVisibility(View.GONE);
        stockAdapter.setIsSelectAllStocks(false);
        stockAdapter.setIsFiltered(true);

        popularStocks = ((MainActivity) getActivity()).getPopularStocks();
        initRecyclerViewPopularStocks(view);

        return view;
    }

    @SuppressLint("DefaultLocale")
    public void onPopularStockClick(View view, int position) {
        Intent intent = new Intent(getContext(), StockActivity.class);
        StockListElement currentStock = popularRequestsAdapter.getStockListElement(position);

        BigDecimal zero = new BigDecimal("0");
        String textChange;
        int colorOfChange;
        BigDecimal change = currentStock.getStock().getQuote().getChange();
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
        
        intent.putExtra("is popular stocks", true);
        intent.putExtra("stock name", currentStock.getStock().getName());
        intent.putExtra("stock symbol", currentStock.getStock().getSymbol());
        intent.putExtra("stock star", currentStock.getIsStarSelected());
        intent.putExtra("pos", position);
        intent.putExtra("stock price", "$" + String.format("%.2f", currentStock.getStock().getQuote().getPrice().doubleValue()));
        intent.putExtra("stock change", textChange + " (" + Math.abs(currentStock.getStock().getQuote().getChangeInPercent().doubleValue()) + "%)");
        intent.putExtra("stock change color", colorOfChange);
        pos = position;
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void initRecyclerViewPopularStocks(ViewGroup view) {
        recyclerViewPopularStocks = view.findViewById(R.id.list_popular_requests);
        recyclerViewPopularStocks.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        popularRequestsAdapter = new PopularRequestsAdapter(getContext(), this, popularStocks);
        recyclerViewPopularStocks.setAdapter(popularRequestsAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((MainActivity) getActivity()).onActivityResult(requestCode, resultCode, data);
    }
}
