package org.cata.lseg.stockpredict.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cata.lseg.stockpredict.model.StockSeries;

@Data
@NoArgsConstructor
@Schema(name = "StockSeries", description = "A container class for series of data and price")
@JsonPropertyOrder({"symbol", "pricePointList"})
public class StockDataDto extends StockSeries {

    public StockDataDto(String symbol) {
        super(symbol);
    }
}
