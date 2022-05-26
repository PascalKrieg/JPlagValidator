package ba.kripas.reporting;

import ba.kripas.ResultAggregate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CSVResultWriter implements IResultWriter {

    private final String targetPath;

    public CSVResultWriter(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public void writeResult(ResultAggregate result) {
        try {
            Path filePath = FileSystems.getDefault().getPath(targetPath, buildFileName());
            FileWriter writer = new FileWriter(filePath.toFile());
            writer.write("");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildFileName() {
        return "evaluation.csv";
        //return (new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date())) + ".csv";
    }

}
