package com.schneider.mstt.cost.center.mirror.processor;

import com.schneider.mstt.cost.center.mirror.enums.Action;
import com.schneider.mstt.cost.center.mirror.enums.ReturnCode;
import com.schneider.mstt.cost.center.mirror.exceptions.FileException;
import com.schneider.mstt.cost.center.mirror.file.CsvParser;
import com.schneider.mstt.cost.center.mirror.file.LogManager;
import com.schneider.mstt.cost.center.mirror.model.CostCenter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:${user.dir}/conf/psconnect.properties")
public class CostCenterMirrorProcessor {

    private static final Logger LOG = Logger.getLogger(CostCenterMirrorProcessor.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Value("${csv.requests.directory}")
    private String requestDirectory;
    @Value("${csv.results.directory}")
    private String resultsDirectory;
    @Value("${csv.logs.directory}")
    private String logsDirectory;

    @Autowired
    private CsvParser csvParser;

    public ReturnCode process() {

        if (!validateProperties()) {
            return ReturnCode.MISSING_PROPERTIES;
        }

        File results = new File(resultsDirectory);
        if (!results.exists()) {
            results.mkdirs();
        }

        File logs = new File(logsDirectory);
        if (!logs.exists()) {
            logs.mkdirs();
        }

        try (Stream<Path> walk = Files.walk(Paths.get(requestDirectory))) {

            List<Path> paths = walk
                    .filter(path -> path.toString().endsWith(".csv") && !path.toString().endsWith("_OK.csv") && !path.toString().endsWith("_KO.csv"))
                    .collect(Collectors.toList());

            if (paths.isEmpty()) {
                LOG.info("No CSV request file to be processed");
                return ReturnCode.SUCCESS;
            }

            for (Path path : paths) {

                LOG.info("Processing file " + path.toString());

                LogManager logManager = LogManager.builder()
                        .filename(getFileNameWithoutExtension(path.toFile()))
                        .build();

                boolean status = false;

                try {
                    List<CostCenter> costCenters = csvParser.parse(path);

                    for (CostCenter costCenter : costCenters) {

                        if (costCenter.getAction().equals(Action.CREATION)) {

                        }

                        if (costCenter.getAction().equals(Action.UPDATE)) {

                        }
                        
                    }

                } catch (FileException ex) {
                    logManager.log(ex.getMessage());
                    logManager.log("Failed to process file, the process will exit with code " + ReturnCode.FILE_ERROR.ordinal());
                    status = false;
                }

                moveFile(path, false);
                logManager.saveLogFile(status);

                LOG.info("File processed");

            }

        } catch (IOException ex) {
            LOG.error(ex);
        }

        return ReturnCode.SUCCESS;

    }

    private boolean validateProperties() {
        LOG.info("Validate properties");
        if (requestDirectory == null || requestDirectory.isEmpty()) {
            LOG.error("Missing property csv.requests.directory");
            return false;
        }
        if (resultsDirectory == null || resultsDirectory.isEmpty()) {
            LOG.error("Missing property csv.results.directory");
            return false;
        }
        if (logsDirectory == null || logsDirectory.isEmpty()) {
            LOG.error("Missing property csv.logs.directory");
            return false;
        }
        LOG.info("Properties are valid");
        return true;
    }

    private Path moveFile(Path path, boolean status) {

        String filename = new StringBuilder()
                .append(getFileNameWithoutExtension(path.toFile()))
                .append("_")
                .append(sdf.format(new Date()))
                .append("_")
                .append(status ? "OK" : "KO")
                .append(".csv")
                .toString();

        try {
            Files.move(path, Paths.get(resultsDirectory + File.separator + filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            LOG.error(ex);
        }

        return null;
    }

    private static String getFileNameWithoutExtension(File file) {
        String fileName = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                fileName = name.replaceFirst("[.][^.]+$", "");
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        return fileName;

    }

}
