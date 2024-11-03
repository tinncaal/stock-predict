package org.cata.lseg.stockpredict.engine;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.cata.lseg.stockpredict.config.AppConfig;
import org.cata.lseg.stockpredict.exception.NotEnoughDataException;
import org.cata.lseg.stockpredict.model.PricePoint;
import org.cata.lseg.stockpredict.model.StockSeries;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static org.cata.lseg.stockpredict.utils.Utils.meanOfSubArray;
import static org.cata.lseg.stockpredict.utils.Utils.round;

@Component
@ConditionalOnProperty(name = "app.predictor", havingValue = "ma")
public class MAPredictor implements Predictor {

    private final AppConfig appConfig;

    public MAPredictor (AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public StockSeries predict(StockSeries stockSeries) {
        if (stockSeries.getPricePointList().isEmpty()) {
            throw new NotEnoughDataException("Stock series is empty");
        }

        var maSize = 2;
        var predictedStockSeries = new StockSeries(stockSeries.getSymbol());
        var lastSampleDate = stockSeries.getLastPricePoint().date();

        var data = stockSeries.getPricePointList().stream()
                .mapToDouble(PricePoint::price)
                .toArray();

        var dataDeviationFromMA = dataDeviationFromMA(data, maSize);

        var predictedSamplesCount = appConfig.getPredictedSamplesCount();
        var predictPriceArray = getPredictPrice(data, dataDeviationFromMA, maSize, predictedSamplesCount);

        for (int i = 0; i < predictPriceArray.length; i++) {
            predictedStockSeries.addPricePoint(lastSampleDate.plusDays(i), round(predictPriceArray[i]));
        }

        return predictedStockSeries;
    }

    private double[] dataDeviationFromMA(double[] data, int maSize) {
        var deviation = new double[data.length];
        for (int i = maSize; i < data.length; i++) {
            deviation[i] = data[i] - meanOfSubArray(data, i - maSize, i);
        }

        return deviation;
    }

    private double[] getPredictPrice(double[] data, double[] deviation, int maSize, int predictedSamplesCount) {
        var predict = new double[predictedSamplesCount];
        var ds = new DescriptiveStatistics();
        var lastDataPoint = data[data.length - 1];

        // MA based on last deviation
        for (int i = 0; i < predictedSamplesCount; i++) {
            ds.clear();
            // adjust the window as moving forward
            var start = data.length - maSize + i;
            for (int j = 0; j < maSize; j++) {
                ds.addValue(deviation[(start + j) % deviation.length]);
            }

            // last data point + avg deviation
            predict[i] = lastDataPoint + ds.getMean();
        }

        return predict;
    }
}
