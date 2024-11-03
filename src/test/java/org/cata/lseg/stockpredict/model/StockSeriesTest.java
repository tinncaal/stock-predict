package org.cata.lseg.stockpredict.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockSeriesTest {

    PricePoint pp1 = new PricePoint(LocalDate.of(2023, 10, 28), 148.0);
    PricePoint pp2 = new PricePoint(LocalDate.of(2023, 10, 29), 150.0);

    private StockSeries stockSeries;

    @BeforeEach
    public void setUp() {
        stockSeries = new StockSeries("AAPL");
    }

    @Test
    void testConstructor() {
        assertEquals("AAPL", stockSeries.getSymbol());
        assertNotNull(stockSeries.getPricePointList());
        assertTrue(stockSeries.getPricePointList().isEmpty());
    }

    @Test
    void testAddPricePoint() {
        var date = LocalDate.of(2023, 10, 29);
        var price = 150.0D;

        stockSeries.addPricePoint(date, price);
        List<PricePoint> prices = stockSeries.getPricePointList();

        assertEquals(1, prices.size());
        assertEquals(date, prices.get(0).date());
        assertEquals(price, prices.get(0).price(), 0.0001);
    }

    @Test
    void testAppendPricePoint() {
        var stockPriceList = List.of(pp1, pp2);
        stockSeries.appendPricePoint(stockPriceList);
        var prices = stockSeries.getPricePointList();

        assertEquals(2, prices.size());
        assertEquals(pp1, prices.get(0));
        assertEquals(pp2, prices.get(1));
    }

    @Test
    void testGetLastPricePoint() {
        var stockPriceList = List.of(pp1, pp2);
        stockSeries.appendPricePoint(stockPriceList);
        PricePoint lastPrice = stockSeries.getLastPricePoint();

        assertEquals(pp2, lastPrice);
    }

    @Test
    void testGetLastPricePointEmptyList() {
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            stockSeries.getLastPricePoint();
        });

        assertNotNull(exception);
    }
}