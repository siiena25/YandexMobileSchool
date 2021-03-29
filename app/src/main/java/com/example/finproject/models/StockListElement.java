package com.example.finproject.models;

import yahoofinance.Stock;

public class StockListElement {
    private Stock stock;
    private boolean isStarSelected = false;
    private int imageId;

    public Stock getStock() {
        return stock;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean getIsStarSelected() {
        return isStarSelected;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setStarSelected(boolean starSelected) {
        isStarSelected = starSelected;
    }
}
