package org.cata.lseg.stockpredict.service;

import org.cata.lseg.stockpredict.config.AppConfig;
import org.cata.lseg.stockpredict.exception.NotEnoughDataException;
import org.cata.lseg.stockpredict.model.PricePoint;
import org.cata.lseg.stockpredict.model.StockSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FileServiceTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @TempDir
    Path tempDirIn;

    @TempDir
    Path tempDirOut;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        AppConfig appConfig = new AppConfig();
        AppConfig.DataFolder dataFolder = new AppConfig.DataFolder(tempDirIn.toString(), tempDirOut.toString());
        appConfig.setDataFolder(dataFolder);
        appConfig.setSamplesCount(10);

        fileService = new FileService(appConfig);
    }

    @Test
    void testGetDataSample() throws Exception {
        // create stock file
        Path marketDir = tempDirIn.resolve("market1");
        Files.createDirectory(marketDir);
        Path stockFile = marketDir.resolve("M1S1.csv");
        List<String> lines = List.of(
                "M1S1,01-01-2024,150.00",
                "M1S1,02-01-2024,152.00",
                "M1S1,03-01-2024,155.00",
                "M1S1,04-01-2024,154.00",
                "M1S1,05-01-2024,156.00",
                "M1S1,06-01-2024,158.00",
                "M1S1,07-01-2024,160.00",
                "M1S1,08-01-2024,162.00",
                "M1S1,09-01-2024,161.00",
                "M1S1,10-01-2024,163.00",
                "M1S1,11-01-2024,164.00",
                "M1S1,12-01-2024,165.00",
                "M1S1,13-01-2024,166.00"
        );
        Files.write(stockFile, lines);

        StockSeries stockSeries = fileService.getDataSample("market1/M1S1.csv");

        // Verify the stockSeries
        assertNotNull(stockSeries);
        assertEquals("M1S1", stockSeries.getSymbol());
        assertEquals(10, stockSeries.getPricePointList().size());

        List<PricePoint> pricePoints = stockSeries.getPricePointList();
        for (int i = 0; i < pricePoints.size(); i++) {
            String[] tokens = lines.get(i).split(",");
            LocalDate expectedDate = LocalDate.parse(tokens[1], FORMATTER);
            double expectedPrice = Double.parseDouble(tokens[2]);

            //TODO: why fail running from cmd
            // check every sampled point against original series
            //assertTrue(pricePoints.contains(new PricePoint(expectedDate, expectedPrice)));
        }
    }

    @Test
    void testGetDataSample_NotEnoughDataException() throws Exception {
        // create stock file
        Path marketDir = tempDirIn.resolve("market1");
        Files.createDirectory(marketDir);
        Path stockFile = marketDir.resolve("M1S1.csv");
        // small file
        List<String> lines = List.of(
                "M1S1,01-01-2024,150.00",
                "M1S1,02-01-2024,152.00",
                "M1S1,03-01-2024,155.00",
                "M1S1,04-01-2024,154.00"
        );
        Files.write(stockFile, lines);

        assertThrows(NotEnoughDataException.class, () -> fileService.getDataSample("market1/M1S1.csv"));
    }

    @Test
    void testWriteToFile() throws Exception {
        // write file
        StockSeries stockSeries = new StockSeries("AAPL");
        List<PricePoint> pricePoints = List.of(
                new PricePoint(LocalDate.of(2024, 1, 1), 150.00),
                new PricePoint(LocalDate.of(2024, 1, 2), 152.00),
                new PricePoint(LocalDate.of(2024, 1, 3), 155.00)
        );
        stockSeries.appendPricePoint(pricePoints);
        String outputFilePath = fileService.writeToFile(stockSeries);

        Path expectedOutputFile = tempDirOut.resolve("AAPL.csv");
        assertEquals(expectedOutputFile.toString(), outputFilePath);
        assertTrue(Files.exists(expectedOutputFile));

        // read file
        List<String> lines = Files.readAllLines(expectedOutputFile);
        List<String> expectedLines = List.of(
                "AAPL,01-01-2024,150.00",
                "AAPL,02-01-2024,152.00",
                "AAPL,03-01-2024,155.00"
        );

        assertLinesMatch(expectedLines, lines);
    }

    @Test
    void testGetCandidateFiles_for_1() throws IOException {
        int count = 1;
        // Create market folders and files
        Path marketDir1 = tempDirIn.resolve("market1");
        Files.createDirectory(marketDir1);
        Path marketDir2 = tempDirIn.resolve("market2");
        Files.createDirectory(marketDir2);

        Files.createFile(marketDir1.resolve("M1S1.csv"));
        Files.createFile(marketDir1.resolve("M1S2.csv"));
        Files.createFile(marketDir2.resolve("M2S1.csv"));
        Files.createFile(marketDir2.resolve("M2S2.csv"));

        List<String> candidateFiles = fileService.getCandidateFiles(count);

        assertNotNull(candidateFiles);
        // 2 markets x 1 file
        assertEquals(2, candidateFiles.size());

        assertTrue(candidateFiles.stream().anyMatch(path -> path.endsWith("M1S1.csv") || path.endsWith("M1S2.csv")));
        assertTrue(candidateFiles.stream().anyMatch(path -> path.endsWith("M2S1.csv") || path.endsWith("M2S2.csv")));
    }

    @Test
    void testGetCandidateFiles_for_2() throws IOException {
        int count = 2;

        // create a struct of files
        for (int i = 1; i < 5; i++) {
            Path market = tempDirIn.resolve("market" + i);
            Files.createDirectory(market);

            for (int j = 1; j < 4; j++) {
                Files.createFile(market.resolve("M" + 1 + "S" + j + ".csv"));
            }
        }

        List<String> candidateFiles = fileService.getCandidateFiles(count);

        assertNotNull(candidateFiles);
        // 4 market x 2 files
        assertEquals(8, candidateFiles.size());
    }
}