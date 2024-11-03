package org.cata.lseg.stockpredict.engine;

import org.cata.lseg.stockpredict.exception.NotEnoughDataException;
import org.cata.lseg.stockpredict.model.PricePoint;
import org.cata.lseg.stockpredict.model.StockSeries;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static org.cata.lseg.stockpredict.utils.Utils.round;

@Component
@ConditionalOnProperty(name = "app.predictor", havingValue = "basic")
public class BasicPredictor implements Predictor {

    @Override
    public StockSeries predict(StockSeries stockSeries) {
        var predictedStockSeries = new StockSeries(stockSeries.getSymbol());

        if (stockSeries.getPricePointList().isEmpty()) {
            throw new NotEnoughDataException("Stock Series is empty");
        }

        double pricePredict1 = stockSeries.getPricePointList()
                .stream()
                .map(PricePoint::price)
                .sorted(Collections.reverseOrder())
                .skip(1)
                .findFirst()
                .get();

        var lastSampleDate = stockSeries.getLastPricePoint().date();
        var lastSamplePrice = stockSeries.getLastPricePoint().price();

        // 1st
        predictedStockSeries.addPricePoint(lastSampleDate.plusDays(1), round(pricePredict1));

        // 2nd
        double pricePredict2 = pricePredict1 + 0.5 * (lastSamplePrice - pricePredict1);
        predictedStockSeries.addPricePoint(lastSampleDate.plusDays(2), round(pricePredict2));

        // 3dh
        double pricePredict3 = pricePredict2 + 0.25 * (pricePredict1 - pricePredict2);
        predictedStockSeries.addPricePoint(lastSampleDate.plusDays(3), round(pricePredict3));

        return predictedStockSeries;
    }
}
