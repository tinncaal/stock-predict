package org.cata.lseg.stockpredict.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StockSeries {
    private String symbol;
    private List<PricePoint> pricePointList;

    public StockSeries(String symbol) {
        this.symbol = symbol;
        this.pricePointList = new ArrayList<>();
    }

    public void addPricePoint(LocalDate date, double price) {
        var pricePoint = new PricePoint(date, price);
        pricePointList.add(pricePoint);
    }

    public void appendPricePoint(List<PricePoint> pricePoint) {
        pricePointList.addAll(pricePoint);
    }

    @JsonIgnore
    public PricePoint getLastPricePoint() {
        return pricePointList.get(pricePointList.size() - 1);
    }
}
