package org.cata.lseg.stockpredict.dto;

import org.cata.lseg.stockpredict.model.StockSeries;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StockSeriesMapper {

    StockSeriesMapper INSTANCE = Mappers.getMapper(StockSeriesMapper.class);

    @Mapping(target = "countPoints", expression = "java(stockSeries.getPricePointList().size())")
    StockSeriesDto toDto(StockSeries stockSeries);

    StockSeries toEntity(StockSeriesDto stockSeriesDto);

    StockSeries toEntity(StockDataDto stockDataDto);
}
