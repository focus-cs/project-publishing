package com.schneider.mstt.cost.center.mirror.file;

import com.schneider.mstt.cost.center.mirror.enums.Action;
import com.schneider.mstt.cost.center.mirror.enums.Status;
import com.schneider.mstt.cost.center.mirror.exceptions.FileException;
import com.schneider.mstt.cost.center.mirror.model.CostCenter;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:${user.dir}/conf/psconnect.properties")
public class CsvParser {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String[] HEADER = {
        "Internal_CC_ID",
        "Second_Mir_GCC_ID",
        "Second_Mir_GCC_RE",
        "Status",
        "Lastupdateby",
        "Last_Update",
        "Action"
    };

    @Value("${csv.separator:;}")
    private String separator;

    public List<CostCenter> parse(Path path) throws FileException, IOException {
        List<CostCenter> costCenters = new ArrayList<>();

        try (BufferedReader bufferReader = Files.newBufferedReader(path)) {
            String header = bufferReader.readLine();

            if (!buildHeader().equals(header)) {
                throw new FileException("Missing header");
            }

            String csvLine;
            while ((csvLine = bufferReader.readLine()) != null) {
                costCenters.add(parseLine(csvLine));
            }

        } catch (FileException | IOException e) {
            throw e;
        }

        return costCenters;
    }

    private CostCenter parseLine(String csvLine) throws FileException {
        String[] splittedLine = csvLine.split(separator);

        if (splittedLine.length != HEADER.length) {
            throw new FileException("Missing field(s)");
        }

        int itemCpt = 0;
        for (String item : splittedLine) {
            if (item.isEmpty()) {
                throw new FileException("Missing field " + HEADER[itemCpt]);
            }
            itemCpt++;
        }

        Status status;
        try {
            status = Status.valueOf(splittedLine[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FileException("Wrong value for field " + HEADER[3]);
        }

        Date lastUpdate;
        try {
            lastUpdate = sdf.parse(splittedLine[5]);
        } catch (ParseException ex) {
            throw new FileException("Wrong value for field " + HEADER[5]);
        }

        Action action;
        try {
            action = Action.valueOf(splittedLine[6].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FileException("Wrong value for field " + HEADER[6]);
        }

        return CostCenter.builder()
                .internalCostCenterId(splittedLine[0])
                .secondMirrorGccId(splittedLine[1])
                .secondMirrorGccRe(splittedLine[2])
                .status(status)
                .lastUpdateBy(splittedLine[4])
                .lastUpdate(lastUpdate)
                .action(action)
                .build();

    }

    private String buildHeader() {

        StringJoiner header = new StringJoiner(separator);

        for (String headerItem : HEADER) {
            header.add(headerItem);
        }

        return header.toString();
    }

}
