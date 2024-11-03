package org.cata.lseg.stockpredict.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cata.lseg.stockpredict.model.StockSeries;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(name = "StockSeries", description = "A container class for series of data and price")
@JsonPropertyOrder({"symbol", "countPoints", "generatedDate", "pricePointList"})
public class StockSeriesDto extends StockSeries {

    private int countPoints = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedDate = LocalDateTime.now();

    public StockSeriesDto(String symbol) {
        super(symbol);
    }
}
