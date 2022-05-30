package ba.kripas.reporting;

import ba.kripas.reporting.csv.CSVJarResultFileBuilder;
import ba.kripas.reporting.csv.CSVSummaryFileBuilder;
import ba.kripas.running.Summary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MultiCSVSummaryWriter implements IResultWriter {
    private final String targetPath;

    private final DecimalFormat formatter;

    public MultiCSVSummaryWriter(String targetPath) {
        this.targetPath = targetPath;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("###.##", symbols);
    }

    @Override
    public void writeResult(Summary summary) {
        Path directoryPath = FileSystems.getDefault().getPath(targetPath, buildDirectoryName());
        var directory = new File(directoryPath.toString());
        directory.mkdir();

        writeFiles(summary, directory);
    }

    private String buildDirectoryName() {
        return "eval-" + (new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date()));
    }

    private void writeFiles(Summary summary, File parentDirectory) {
        writeJarRunFiles(summary, parentDirectory);
        writeSummaryFile(summary, parentDirectory);
    }

    private void writeSummaryFile(Summary summary, File parentDirectory) {
        var summaryFileBuilder = new CSVSummaryFileBuilder(formatter);
        var fileName = summaryFileBuilder.buildFileName();
        var content = summaryFileBuilder.buildContent(summary);

        writeSingleFile(new File(parentDirectory, fileName), content);
    }

    private void writeJarRunFiles(Summary summary, File parentDirectory) {
        var JarResultBuilder = new CSVJarResultFileBuilder(formatter);

        summary.getJarResults().forEach(jarRunResult -> {
            jarRunResult.getProjectResult().forEach(projectRunResult -> {
                var fileName = JarResultBuilder.buildJarResultFileName(jarRunResult.getConfig(), projectRunResult.getProject());
                var content = JarResultBuilder.buildJarResultContent(projectRunResult.getProject(), projectRunResult.getComparisons());
                writeSingleFile(new File(parentDirectory, fileName), content);
            });
        });
    }

    private void writeSingleFile(File file, String content) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
