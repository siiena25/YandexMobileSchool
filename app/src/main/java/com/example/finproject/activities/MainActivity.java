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
import com.example.finproject.repo.Repository;
import com.example.finproject.viewmodels.StockViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements StockAdapter.OnStockListener {

    private RecyclerView recyclerViewStocks;
    private StockAdapter stockAdapter;

    private String currentFragment = null;
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
    private ArrayList<Integer> popularStarPosition;
    private ArrayList<Boolean> popularStarIsSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getSharedFavouriteStocks = sharedPreferences.getStringSet("favourite stocks", null);

        initRecyclerViewStocks();
        setSearchViewListeners();

        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT);
            if (currentFragment.equals(FRAGMENT_SEARCH_RESULT_TEXT)) {
                stockAdapter.setIsSelectAllStocks(false);
                stockAdapter.setIsFiltered(true);
            }
            pos = savedInstanceState.getInt("pos");
            inputText = savedInstanceState.getString("inputText");
        }
        else {
            Repository.getInstance().loadStocks();
            StocksAndFavouriteFragment stocksAndFavouriteFragment = new StocksAndFavouriteFragment();
            currentFragment = getString(FRAGMENT_STOCKS_AND_FAVOURITE);
            loadFragment(stocksAndFavouriteFragment, currentFragment);
        }

        StockViewModel stockViewModel = new ViewModelProvider(this).get(StockViewModel.class);
        stockViewModel.getStocks().observe(this, new Observer<ArrayList<StockListElement>>() {
            @Override
            public void onChanged(ArrayList<StockListElement> stocks) {
                stockAdapter.setStocks(stocks);
            }
        });

        stockAdapter.setStocks(stockViewModel.getStocks().getValue());

        popularStarPosition = new ArrayList<>();
        popularStarIsSelected = new ArrayList<>();
    }

    public Set<String> getSharedFavouriteStocks() {
        return getSharedFavouriteStocks;
    }

    public ArrayList<StockListElement> getPopularStocks() {
        return stockAdapter.getPopularStocks();
    }

    private void initRecyclerViewStocks() {
        recyclerViewStocks = findViewById(R.id.recycler_view_stocks);
        recyclerViewStocks.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockAdapter(this, this, getSharedFavouriteStocks);
        recyclerViewStocks.setAdapter(stockAdapter);
    }

    private void setSearchViewListeners() {
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentFragment.equals(FRAGMENT_SEARCH_EMPTY_TEXT)) {
                    SearchEmptyFragment searchEmptyFragment = new SearchEmptyFragment();
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
                System.out.println(popularStarPosition);
                if (popularStarPosition.size() != 0) {
                    for (int index = 0; index < popularStarPosition.size(); index++) {
                        stockAdapter.setIsSelectStar(popularStarPosition.get(index), popularStarIsSelected.get(index));
                        stockAdapter.notifyItemChanged(popularStarPosition.get(index));
                    }
                    popularStarPosition.clear();
                    popularStarIsSelected.clear();
                }
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

    public StockAdapter getStockAdapter() {
        return stockAdapter;
    }

    public RecyclerView getRecyclerViewStocks() {
        return recyclerViewStocks;
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            undoFragment();
            if (currentFragment.equals(FRAGMENT_SEARCH_EMPTY_TEXT)) {
                recyclerViewStocks.setVisibility(View.VISIBLE);
            }
        }
        else finish();
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

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    public void onStockClick(View view, int position) {
        Intent intent = new Intent(this, StockActivity.class);
        StockListElement currentStock = stockAdapter.getStockListElement(position);

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

        intent.putExtra("is popular stocks", false);
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

    public void onStarClick(View view, int position) {
        stockAdapter.setIsSelectStar(position, !stockAdapter.getIsSelectStar(position));
        stockAdapter.notifyDataSetChanged();

        Set<String> favouriteStocks = stockAdapter.getFavouriteStocks();
        editor = sharedPreferences.edit();
        editor.putStringSet("favourite stocks", favouriteStocks);
        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                boolean isStarSelected = data.getBooleanExtra("is star selected", false);
                boolean isPopularStocks = data.getBooleanExtra("is popular stocks", false);
                int position = (!isPopularStocks) ? pos : stockAdapter.getPositionsOfPopularStocks()[data.getIntExtra("pos", -1)];
                if (stockAdapter.getItemCount() > 0 && !isPopularStocks) {
                    stockAdapter.getStockListElement(position).setStarSelected(isStarSelected);
                    stockAdapter.notifyItemChanged(position);
                }
                else {
                    this.popularStarPosition.add(position);
                    this.popularStarIsSelected.add(isStarSelected);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
