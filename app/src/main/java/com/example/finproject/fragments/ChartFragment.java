package com.example.finproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finproject.R;
import com.example.finproject.activities.StockActivity;

public class ChartFragment extends Fragment {
    TextView textViewPrice;
    TextView textViewChange;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fragment_stock_chart, container, false);
        textViewPrice = view.findViewById(R.id.fragment_stock_price);
        textViewChange = view.findViewById(R.id.fragment_stock_change);

        String stockPrice = ((StockActivity) getActivity()).getStockPrice();
        String stockChange = ((StockActivity) getActivity()).getStockChange();

        textViewPrice.setText(stockPrice);
        textViewChange.setText(stockChange);
        if (stockChange.charAt(0) == '+') {
            textViewChange.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        else if (stockChange.charAt(0) == '-') {
            textViewChange.setTextColor(getResources().getColor(R.color.colorRed));
        }
        else {
            textViewChange.setTextColor(getResources().getColor(R.color.colorBlack));
        }

        return view;
    }
}
