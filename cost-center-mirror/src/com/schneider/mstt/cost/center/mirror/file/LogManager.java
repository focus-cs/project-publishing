package com.schneider.mstt.cost.center.mirror.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import org.apache.log4j.Logger;

@Builder
public class LogManager {
    
    private static SimpleDateFormat sdfFilename = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    
    private String filename;
    private String directory;
    private List<String> logs;
    
    public void info(String line) {
        addLog("INFO" + " " + line);
    }
    
    public void error(String line) {
        addLog("ERROR" + " " + line);
    }
    
    public void debug(String line) {
        addLog("DEBUG" + " " + line);
    }
    
    private void addLog(String line) {
        if (logs == null) {
            logs = new ArrayList<>();
        }
        
        String logMessage = sdf.format(new Date()) + " " + line;
        
        logs.add(logMessage);
    }
    
    public void saveLogFile(boolean success) {
        
        String fullPath = new StringBuilder().append(directory)
                .append(directory.endsWith(File.separator) ? "" : File.separator)
                .append(filename)
                .append("_")
                .append(sdfFilename.format(new Date()))
                .append("_")
                .append(success ? "OK" : "KO")
                .append(".log")
                .toString();
                
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            
            for(String log : logs) {
                writer.write(log);
                writer.newLine();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(LogManager.class.getName()).error(ex);
        }
        
    }
}
