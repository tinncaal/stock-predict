package org.cata.lseg.stockpredict.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cata.lseg.stockpredict.utils.PrintToLog;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("app")
@Data
@PrintToLog
public class AppConfig {
    private int samplesCount;
    private int predictedSamplesCount;
    private DataFolder dataFolder;
    private PredictorType predictor;

    @Data
    @AllArgsConstructor
    public static class DataFolder {
        private String in;
        private String out;
    }
}
