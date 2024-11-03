package org.cata.lseg.stockpredict.engine;

import org.cata.lseg.stockpredict.model.StockSeries;

public interface Predictor {
    StockSeries predict(StockSeries stockSeries);
}
