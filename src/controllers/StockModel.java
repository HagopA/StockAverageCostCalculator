package controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class StockModel {

    private final SimpleIntegerProperty numberOfSharesProperty;
    private final SimpleDoubleProperty costPerShareProperty;

    public StockModel(SimpleIntegerProperty numberOfSharesProperty, SimpleDoubleProperty costPerShareProperty) {
        this.numberOfSharesProperty = numberOfSharesProperty;
        this.costPerShareProperty = costPerShareProperty;
    }

    public int getNumberOfSharesProperty() {
        return numberOfSharesProperty.get();
    }

    public double getCostPerShareProperty() {
        return costPerShareProperty.get();
    }

    public int getNumberOfShares() {
        return numberOfSharesProperty.get();
    }

    public double getCostPerShare() {
        return costPerShareProperty.get();
    }
}
