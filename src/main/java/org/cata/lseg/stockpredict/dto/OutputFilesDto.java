package org.cata.lseg.stockpredict.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "OutputFiles", description = "A list of processed files path")
public class OutputFilesDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedDate = LocalDateTime.now();

    List<String> files = new ArrayList<>();

    public void addFile(String file) {
        files.add(file);
    }
}
