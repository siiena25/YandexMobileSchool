package com.example.finproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finproject.R;
import com.example.finproject.adapters.StockAdapter;
import com.example.finproject.fragments.SearchEmptyFragment;
import com.example.finproject.fragments.SearchResultFragment;
import com.example.finproject.fragments.StocksAndFavouriteFragment;
import com.example.finproject.models.StockListElement;
import com.example.finproject.viewmodels.StockViewModel;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements StockAdapter.OnStockListener {

    private RecyclerView listViewStocks;
    private StockAdapter stockAdapter;
    private SearchView searchView;
    private String currentFragment = null;
    private String lastFragment = null;
    private String inputText = null;

    private static final String CURRENT_FRAGMENT = "Current fragment";
    private static final int FRAGMENT_STOCKS_AND_FAVOURITE = R.string.stocks_and_favourite;
    private static final int FRAGMENT_SEARCH_EMPTY = R.string.fragment_search_empty;
    private static final int FRAGMENT_SEARCH_RESULT = R.string.fragment_search_result;

    private static final String FRAGMENT_STOCKS_AND_FAVOURITE_TEXT = "stocks and favourite";
    private static final String FRAGMENT_SEARCH_EMPTY_TEXT = "search empty";
    private static final String FRAGMENT_SEARCH_RESULT_TEXT = "search result";

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private Set<String> getSharedFavouriteStocks;

    private final int REQUEST_CODE = 1;
    private int pos = -1;

    private SearchView.OnQueryTextListener listener;
    private StockViewModel stockViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getSharedFavouriteStocks = sharedPreferences.getStringSet("favourite stocks", null);

        initRecyclerView();
        setSearchViewListeners();

        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT);
            pos = savedInstanceState.getInt("pos");
            inputText = savedInstanceState.getString("inputText");
        }
        else {
            StocksAndFavouriteFragment stocksAndFavouriteFragment = new StocksAndFavouriteFragment();
            currentFragment = getString(FRAGMENT_STOCKS_AND_FAVOURITE);
            loadFragment(stocksAndFavouriteFragment, currentFragment);
        }

        stockViewModel = new ViewModelProvider(this).get(StockViewModel.class);
        final Observer<ArrayList<StockListElement>> stockListObserver = new Observer<ArrayList<StockListElement>>() {
            @Override
            public void onChanged(ArrayList<StockListElement> stocks) {
                stockAdapter.setStocks(stocks);
            }
        };
        stockViewModel.getStocks().observe(this, stockListObserver);
    }

    private void initRecyclerView() {
        listViewStocks = findViewById(R.id.recycler_view_stocks);
        listViewStocks.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockAdapter(this, this, getSharedFavouriteStocks);
        listViewStocks.setAdapter(stockAdapter);
    }

    private void setSearchViewListeners() {
        searchView = findViewById(R.id.search_view);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentFragment.equals(FRAGMENT_SEARCH_EMPTY_TEXT)) {
                    SearchEmptyFragment searchEmptyFragment = new SearchEmptyFragment();
                    lastFragment = currentFragment;
                    currentFragment = getString(FRAGMENT_SEARCH_EMPTY);
                    loadFragment(searchEmptyFragment, currentFragment);
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                undoFragment();
                currentFragment = FRAGMENT_STOCKS_AND_FAVOURITE_TEXT;
                stockAdapter.setIsSelectAllStocks(true);
                stockAdapter.setIsFiltered(false);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                undoFragment();
                SearchResultFragment searchResultFragment = new SearchResultFragment();
                currentFragment = getString(FRAGMENT_SEARCH_RESULT);
                loadFragment(searchResultFragment, currentFragment);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                inputText = newText;
                if (newText.isEmpty() && !currentFragment.equals(FRAGMENT_SEARCH_EMPTY_TEXT) && !currentFragment.equals(FRAGMENT_STOCKS_AND_FAVOURITE_TEXT)) {
                    if (currentFragment.equals(FRAGMENT_SEARCH_RESULT_TEXT)) {
                        undoFragment();
                    }
                    SearchEmptyFragment searchEmptyFragment = new SearchEmptyFragment();
                    currentFragment = getString(FRAGMENT_SEARCH_EMPTY);
                    loadFragment(searchEmptyFragment, currentFragment);
                } else if (!newText.isEmpty()) {
                    if (currentFragment.equals(FRAGMENT_SEARCH_EMPTY_TEXT) || currentFragment.equals(FRAGMENT_SEARCH_RESULT_TEXT)) {
                        undoFragment();
                    }
                    SearchResultFragment searchResultFragment = new SearchResultFragment();
                    currentFragment = getString(FRAGMENT_SEARCH_RESULT);
                    loadFragment(searchResultFragment, currentFragment);
                }
                stockAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public StockAdapter getStockAdapter() {
        return stockAdapter;
    }

    public RecyclerView getListViewStocks() {
        return listViewStocks;
    }

    public void onClickFavouriteTextView(View view) {
        if (stockAdapter.getItemCount() == 19) {
            int colorGrey = getResources().getColor(R.color.colorGrey);
            int colorBlack = getResources().getColor(R.color.colorBlack);
            TextView favouriteTextView = findViewById(R.id.favourite_text_view);
            TextView stocksTextView = findViewById(R.id.stock_text_view);
            if (favouriteTextView.getCurrentTextColor() == colorGrey) {
                stockAdapter.setIsSelectAllStocks(false);
                stockAdapter.setIsSelectFavouriteStocks(true);
                stockAdapter.setIsFiltered(true);
                stockAdapter.getFilter().filter("favourite");
                favouriteTextView.setTextColor(colorBlack);
                favouriteTextView.setTextSize(28);
                stocksTextView.setTextColor(colorGrey);
                stocksTextView.setTextSize(18);
            }
        }
    }

    public void onClickStocksTextView(View view) {
        int colorGrey = getResources().getColor(R.color.colorGrey);
        int colorBlack = getResources().getColor(R.color.colorBlack);
        TextView favouriteTextView = findViewById(R.id.favourite_text_view);
        TextView stocksTextView = findViewById(R.id.stock_text_view);
        if (stocksTextView.getCurrentTextColor() == colorGrey) {
            stockAdapter.setAllStocks();
            favouriteTextView.setTextColor(colorGrey);
            favouriteTextView.setTextSize(18);
            stocksTextView.setTextColor(colorBlack);
            stocksTextView.setTextSize(28);
        }
    }

    public String getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(String currentFragment) {
        this.currentFragment = currentFragment;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            undoFragment();
            if (currentFragment.equals(FRAGMENT_SEARCH_EMPTY_TEXT)) {
                listViewStocks.setVisibility(View.VISIBLE);
                System.out.println(stockAdapter.getItemCount());
            }
        }
        else {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAGMENT, currentFragment);
        outState.putInt("pos", pos);
        outState.putString("inputText", inputText);
    }

    public void loadFragment(Fragment fragment, String currentFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(currentFragment)
                .commit();
    }

    public void undoFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStockClick(View view, int position) {
        Intent intent = new Intent(this, StockActivity.class);
        StockListElement currentStock = stockAdapter.getStockListElement(position);
        intent.putExtra("stock name", currentStock.getStock().getName());
        intent.putExtra("stock symbol", currentStock.getStock().getSymbol());
        intent.putExtra("stock star", currentStock.getIsStarSelected());
        pos = position;
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onStarClick(View view, int position) {
        if (stockAdapter.getIsSelectStar(position)) {
            stockAdapter.setIsSelectStar(position, false);
        } else {
            stockAdapter.setIsSelectStar(position, true);
        }
        stockAdapter.notifyDataSetChanged();

        Set<String> favouriteStocks = stockAdapter.getFavouriteStocks();
        editor = sharedPreferences.edit();
        editor.putStringSet("favourite stocks", favouriteStocks);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    boolean isStarSelected = data.getBooleanExtra("is star selected", false);
                    if (stockAdapter.getItemCount() > 0) {
                        stockAdapter.getStockListElement(pos).setStarSelected(isStarSelected);
                        stockAdapter.notifyItemChanged(pos);
                    }
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
