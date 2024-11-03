package org.cata.lseg.stockpredict.service;

import lombok.extern.slf4j.Slf4j;
import org.cata.lseg.stockpredict.config.AppConfig;
import org.cata.lseg.stockpredict.engine.Predictor;
import org.cata.lseg.stockpredict.model.StockSeries;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PredictionService {

    private final AppConfig appConfig;
    private final Predictor predictor;

    public PredictionService(AppConfig appConfig, Predictor predictor) {
        this.appConfig = appConfig;
        this.predictor = predictor;
    }

    public StockSeries getPrediction(StockSeries stockSeries) {
        return predictor.predict(stockSeries);
    }

}
