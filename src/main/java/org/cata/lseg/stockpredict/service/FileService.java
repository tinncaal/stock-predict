package org.cata.lseg.stockpredict.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.cata.lseg.stockpredict.config.AppConfig;
import org.cata.lseg.stockpredict.exception.*;
import org.cata.lseg.stockpredict.model.PricePoint;
import org.cata.lseg.stockpredict.model.StockSeries;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileService {

    private static final String LINE_FORMAT = "%s,%s,%.2f";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final String CSV = ".csv";

    private final AppConfig appConfig;

    public FileService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public StockSeries getDataSample(String filePath) {
        var absoluthPath = Paths.get(appConfig.getDataFolder().getIn());
        absoluthPath = absoluthPath.resolve(filePath);
        log.info("Processing file: {}", absoluthPath);

        if (!absoluthPath.toFile().exists()) {
            throw new NoFilesException("Not exist: " + absoluthPath);
        }

        var startIndex = getRandomIndex(absoluthPath);
        var stockSeries = new StockSeries(getSymbol(absoluthPath));
        try {
            // FLTR,01-09-2023,16340.00
            var stockPrice = Files.readAllLines(absoluthPath)
                    .stream()
                    .skip(startIndex)
                    .limit(appConfig.getSamplesCount())
                    .map(line -> {
                        var cells = line.split(",");
                        return new PricePoint(LocalDate.parse(cells[1], DATE_FORMATTER), Double.parseDouble(cells[2]));
                    })
                    .toList();

            stockSeries.appendPricePoint(stockPrice);
        } catch (IOException e) {
            throw new FileParsingException("Reading file: " + absoluthPath + " failed", e);
        }

        return stockSeries;
    }

    public String writeToFile(StockSeries stockSeries) {
        var stringJoiner = new StringJoiner(System.lineSeparator());
        stockSeries.getPricePointList().forEach(stockPrice -> {
            String line = String.format(LINE_FORMAT, stockSeries.getSymbol(), stockPrice.date().format(DATETIME_FORMATTER), stockPrice.price());
            stringJoiner.add(line);
        });

        var outFile = Paths.get(appConfig.getDataFolder().getOut(), stockSeries.getSymbol() + CSV);
        var outFolder = Path.of(appConfig.getDataFolder().getOut());
        try {
            if (!Files.exists(outFolder)) {
                Files.createDirectories(outFolder);
            } else {
                Files.deleteIfExists(outFile);
            }

            Files.write(outFile, stringJoiner.toString().getBytes());
            log.info("File saved: {}", outFile);
        } catch (IOException e) {
            throw new FileWriteException("Writing file: " + outFile + " failed", e);
        }

        return outFile.toString();
    }

    public List<String> getCandidateFiles(int count) {
        var candidateFiles = new ArrayList<String>();
        Path rootPath = Paths.get(appConfig.getDataFolder().getIn());

        List<Path> marketFolders;
        try {
            try(Stream<Path> stream = Files.list(rootPath)) {
                marketFolders = stream.filter(Files::isDirectory).toList();
            }

            if (marketFolders.isEmpty()) {
                throw new NoMarketFoldersException("No market folder found in: " + rootPath);
            }

            for (Path dir : marketFolders) {
                try (Stream<Path> stream = Files.list(dir)) {
                    candidateFiles.addAll(stream
                            .filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(CSV))
                            .limit(count)
                            .map(Path::toString)
                            .toList());
                }
            }
        } catch (IOException e) {
            throw new FileListingException("Stock files listing error: " + rootPath, e);
        }

        return candidateFiles;
    }

    private int getRandomIndex(Path filePath) {
        int linesCount;
        try {
            linesCount = Files.readAllLines(filePath).size();
        } catch (IOException e) {
            throw new FileParsingException("Reading file: " + filePath + " failed", e);
        }

        if (linesCount < appConfig.getSamplesCount()) {
            throw new NotEnoughDataException("File to small: " + filePath);
        }

        var maxIndex = linesCount - appConfig.getSamplesCount();
        return RandomGenerator.getDefault().nextInt(maxIndex + 1);
    }

    private String getSymbol(Path filePath) {
        var fileName = filePath.toFile().getName();
        return fileName.substring(0, (int)fileName.length() - 4);
    }
}
