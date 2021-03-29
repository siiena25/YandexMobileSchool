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

public class SearchResultFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView listViewStocks = ((MainActivity) getActivity()).getListViewStocks();
        listViewStocks.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }
}
