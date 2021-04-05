package com.example.finproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finproject.R;
import com.example.finproject.fragments.ChartFragment;
import com.example.finproject.fragments.ForecastsFragment;
import com.example.finproject.fragments.IdeasFragment;
import com.example.finproject.fragments.NewsFragment;
import com.example.finproject.fragments.SummaryFragment;

public class StockActivity extends AppCompatActivity {
    private String stockName;
    private String stockSymbol;
    private String stockPrice;
    private String stockChange;
    private int stockChangeColor;
    private boolean isStarSelected;
    private boolean isPopularStocks;
    private int pos;
    private int colorGrey;
    private int colorBlack;

    TextView textViewName;
    TextView textViewSymbol;
    ImageButton imageButtonBack;
    ImageButton imageButtonStar;
    TextView textViewCharts;
    TextView textViewSummary;
    TextView textViewNews;
    TextView textViewForecasts;
    TextView textViewIdeas;

    private String currentFragment = null;

    private static final String CURRENT_FRAGMENT = "Current fragment";
    private static final int FRAGMENT_CHART = R.string.fragment_chart;
    private static final int FRAGMENT_SUMMARY = R.string.fragment_summary;
    private static final int FRAGMENT_NEWS = R.string.fragment_news;
    private static final int FRAGMENT_FORECASTS = R.string.fragment_forecasts;
    private static final int FRAGMENT_IDEAS = R.string.fragment_ideas;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stock);

        Bundle arguments = getIntent().getExtras();
        stockName = arguments.get("stock name").toString();
        stockSymbol = arguments.get("stock symbol").toString();
        stockPrice = arguments.get("stock price").toString();
        stockChange = arguments.get("stock change").toString();
        stockChangeColor = (int) arguments.get("stock change color");
        isStarSelected = arguments.getBoolean("stock star");
        isPopularStocks = arguments.getBoolean("is popular stocks");
        pos = arguments.getInt("pos");

        colorGrey = getResources().getColor(R.color.colorGrey);
        colorBlack = getResources().getColor(R.color.colorBlack);

        textViewName = findViewById(R.id.current_stock_name);
        textViewSymbol = findViewById(R.id.current_stock_symbol);
        imageButtonStar = findViewById(R.id.current_stock_star);
        imageButtonBack = findViewById(R.id.current_stock_back_button);
        textViewCharts = findViewById(R.id.chart);
        textViewSummary = findViewById(R.id.summary);
        textViewNews = findViewById(R.id.news);
        textViewForecasts = findViewById(R.id.forecasts);
        textViewIdeas = findViewById(R.id.ideas);

        textViewName.setText(stockName);
        textViewSymbol.setText(stockSymbol);

        if (isStarSelected) { imageButtonStar.setBackground(getResources().getDrawable(R.drawable.ic_star_selected)); }

        ChartFragment chartFragment = new ChartFragment();
        currentFragment = getString(FRAGMENT_CHART);
        loadFragment(chartFragment, currentFragment);

        textViewCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewCharts.getCurrentTextColor() == colorGrey) {
                    setTextViews(textViewCharts, textViewSummary, textViewNews, textViewForecasts, textViewIdeas);
                    undoFragment();
                    ChartFragment chartFragment = new ChartFragment();
                    currentFragment = getString(FRAGMENT_CHART);
                    loadFragment(chartFragment, currentFragment);
                }
            }
        });

        textViewSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewSummary.getCurrentTextColor() == colorGrey) {
                    setTextViews(textViewSummary, textViewCharts, textViewNews, textViewForecasts, textViewIdeas);
                    undoFragment();
                    SummaryFragment summaryFragment = new SummaryFragment();
                    currentFragment = getString(FRAGMENT_SUMMARY);
                    loadFragment(summaryFragment, currentFragment);
                }
            }
        });

        textViewNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewNews.getCurrentTextColor() == colorGrey) {
                    setTextViews(textViewNews, textViewSummary, textViewCharts, textViewForecasts, textViewIdeas);
                    undoFragment();
                    NewsFragment newsFragment = new NewsFragment();
                    currentFragment = getString(FRAGMENT_NEWS);
                    loadFragment(newsFragment, currentFragment);
                }
            }
        });

        textViewForecasts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewCharts.getCurrentTextColor() == colorGrey) {
                    setTextViews(textViewForecasts, textViewSummary, textViewNews, textViewCharts, textViewIdeas);
                    undoFragment();
                    ForecastsFragment forecastsFragment = new ForecastsFragment();
                    currentFragment = getString(FRAGMENT_FORECASTS);
                    loadFragment(forecastsFragment, currentFragment);
                }
            }
        });

        textViewIdeas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewIdeas.getCurrentTextColor() == colorGrey) {
                    setTextViews(textViewIdeas, textViewSummary, textViewNews, textViewForecasts, textViewCharts);
                    undoFragment();
                    IdeasFragment ideasFragment = new IdeasFragment();
                    currentFragment = getString(FRAGMENT_IDEAS);
                    loadFragment(ideasFragment, currentFragment);
                }
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageButtonStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarSelected) {
                    imageButtonStar.setBackground(getResources().getDrawable(R.drawable.ic_star));
                    isStarSelected = false;
                }
                else {
                    imageButtonStar.setBackground(getResources().getDrawable(R.drawable.ic_star_selected));
                    isStarSelected = true;
                }
            }
        });
    }

    private void setTextViews(TextView textViewActive, TextView textView, TextView textView1, TextView textView2, TextView textView3) {
        setTextViewActive(textViewActive);
        setTextViewPassive(textView);
        setTextViewPassive(textView1);
        setTextViewPassive(textView2);
        setTextViewPassive(textView3);
    }

    private void setTextViewActive(TextView textView) {
        textView.setTextColor(colorBlack);
        textView.setTextSize(22);
    }

    private void setTextViewPassive(TextView textView) {
        textView.setTextColor(colorGrey);
        textView.setTextSize(18);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAGMENT, currentFragment);
    }

    public void loadFragment(Fragment fragment, String currentFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_stock_container, fragment)
                .addToBackStack(currentFragment)
                .commit();
    }

    public void undoFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void finish(){
        Intent intent = new Intent();
        intent.putExtra("is star selected", isStarSelected);
        intent.putExtra("is popular stocks", isPopularStocks);
        intent.putExtra("pos", pos);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public String getStockChange() {
        return stockChange;
    }

    public int getStockChangeColor() {
        return  stockChangeColor;
    }
}
