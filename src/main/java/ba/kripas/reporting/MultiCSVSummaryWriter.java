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
        writeFullSummary(summary, parentDirectory);
        //writeShortSummary(summary, parentDirectory); // disabled until the issue with NaN and infinity messing up the average is resolved
    }

    private void writeFullSummary(Summary summary, File parentDirectory) {
        var summaryFileBuilder = new CSVSummaryFileBuilder(formatter);
        var fileName = summaryFileBuilder.buildFileName();
        var content = summaryFileBuilder.buildFullContent(summary);

        writeSingleFile(new File(parentDirectory, fileName), content);
    }

    private void writeShortSummary(Summary summary, File parentDirectory) {
        var summaryFileBuilder = new CSVSummaryFileBuilder(formatter);
        var fileName = "summary-weighted.csv";
        var content = summaryFileBuilder.buildSummarizedContent(summary);

        writeSingleFile(new File(parentDirectory, fileName), content);
    }

    private void writeJarRunFiles(Summary summary, File parentDirectory) {
        var jarResultBuilder = new CSVJarResultFileBuilder(formatter);

        summary.getJarResults().forEach(jarRunResult -> {
            jarRunResult.getProjectResult().forEach(projectRunResult -> {
                var fileName = jarResultBuilder.buildJarResultFileName(jarRunResult.getConfig(), projectRunResult.getProject());
                var content = jarResultBuilder.buildJarResultContent(projectRunResult.getProject(), projectRunResult.getComparisons());
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
