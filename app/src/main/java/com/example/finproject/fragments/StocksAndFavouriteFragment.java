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

        /*TextView stocksTextView = getActivity().findViewById(R.id.stock_text_view);
        TextView favouriteTextView = getActivity().findViewById(R.id.favourite_text_view);

        int colorGrey = getResources().getColor(R.color.colorGrey);
        int colorBlack = getResources().getColor(R.color.colorBlack);

        stocksTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stocksTextView.getCurrentTextColor() == colorGrey) {
                    stockAdapter.setAllStocks();
                    favouriteTextView.setTextColor(colorGrey);
                    favouriteTextView.setTextSize(18);
                    stocksTextView.setTextColor(colorBlack);
                    stocksTextView.setTextSize(28);
                }
            }
        });

        favouriteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteTextView.getCurrentTextColor() == colorGrey) {
                    stockAdapter.setIsSelectAllStocks(false);
                    stockAdapter.setIsSelectFavouriteStocks(true);
                    stockAdapter.getFilter().filter("favourite");
                    favouriteTextView.setTextColor(colorBlack);
                    favouriteTextView.setTextSize(28);
                    stocksTextView.setTextColor(colorGrey);
                    stocksTextView.setTextSize(18);
                }
            }
        });*/

        return inflater.inflate(R.layout.fragment_stocks_and_favourite, container, false);
    }
}
