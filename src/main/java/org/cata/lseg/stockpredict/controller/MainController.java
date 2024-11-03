package org.cata.lseg.stockpredict.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.cata.lseg.stockpredict.dto.OutputFilesDto;
import org.cata.lseg.stockpredict.dto.StockDataDto;
import org.cata.lseg.stockpredict.dto.StockSeriesDto;
import org.cata.lseg.stockpredict.dto.StockSeriesMapper;
import org.cata.lseg.stockpredict.exception.FileCounterException;
import org.cata.lseg.stockpredict.exception.NoFilesException;
import org.cata.lseg.stockpredict.service.FileService;
import org.cata.lseg.stockpredict.service.PredictionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE )
@Slf4j
@Validated
public class MainController {

    private final PredictionService predictionService;
    private final FileService fileService;

    public MainController(PredictionService predictionService, FileService fileService) {
        this.predictionService = predictionService;
        this.fileService = fileService;
    }

    @GetMapping("/sample")
    public StockSeriesDto getRandomSample(@RequestParam String file) {
        var stockSeries = fileService.getDataSample(file);

        return StockSeriesMapper.INSTANCE.toDto(stockSeries);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/predict", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public StockSeriesDto predict(@RequestBody StockDataDto stockSeriesDto) {
        var stockSeries = StockSeriesMapper.INSTANCE.toEntity(stockSeriesDto);
        var stockSeriesPredicted = predictionService.getPrediction(stockSeries);

        stockSeries.appendPricePoint(stockSeriesPredicted.getPricePointList());
        fileService.writeToFile(stockSeries);

        return StockSeriesMapper.INSTANCE.toDto(stockSeriesPredicted);
    }

    @GetMapping("/scan")
    @ResponseStatus(HttpStatus.CREATED)
    public OutputFilesDto scan(@RequestParam int count) {
        validateCount(count);

        var candidateFiles = fileService.getCandidateFiles(count);
        if(candidateFiles.isEmpty())
            throw new NoFilesException("No candidate files found");

        var outputFilesDto = new OutputFilesDto();
        candidateFiles.forEach(path -> {
            var stockSeries = fileService.getDataSample(path);
            var stockSeriesPredicted = predictionService.getPrediction(stockSeries);
            stockSeries.appendPricePoint(stockSeriesPredicted.getPricePointList());
            outputFilesDto.addFile(fileService.writeToFile(stockSeries));
        });

        return outputFilesDto;
    }

    private void validateCount(int count) {
        if (count < 1 || count > 2) {
            throw new FileCounterException("The count parameter must be between 1 and 2");
        }
    }
}
