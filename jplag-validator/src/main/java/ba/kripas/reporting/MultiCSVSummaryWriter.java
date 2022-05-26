package ba.kripas.reporting;

import ba.kripas.JarConfig;
import ba.kripas.Summary;
import ba.kripas.dataset.Project;
import ba.kripas.dataset.SubmissionPairType;
import ba.kripas.jplag.JPlagComparisonWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

public class MultiCSVSummaryWriter implements IResultWriter{
    private final String targetPath;

    public MultiCSVSummaryWriter(String targetPath) {
        this.targetPath = targetPath;
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
        summary.getJarResults().forEach(jarRunResult -> {
            jarRunResult.getProjectResult().forEach(projectRunResult -> {
                var fileName = buildFileName(jarRunResult.getConfig(), projectRunResult.getProject());
                var content = buildFileContent(projectRunResult.getProject(), projectRunResult.getComparisons());
                writeSingleFile(new File(parentDirectory, fileName), content);
            });
        });
    }

    private String buildFileContent(Project project, List<JPlagComparisonWrapper> comparisons) {
        StringBuilder sb = new StringBuilder(buildTitleLine());
        comparisons.forEach(comparison -> {
            var actualType = project.GetPairType(comparison.getFirstSubmissionName(), comparison.getSecondSubmissionName());
            sb.append(buildEntryLine(comparison, actualType));
        });
        return sb.toString();
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

    private String buildFileName(JarConfig jarConfig, Project project) {
        return jarConfig.getCommitId().substring(0,6) + "-" + jarConfig.getJarFile().getName()  + "-" + project.getName() + ".csv";
    }

    private String buildTitleLine() {
        return "first_submission,second_submission,similarity,minimal_similarity,maximal_similarity,type\n";
    }

    private String buildEntryLine(JPlagComparisonWrapper comparison, SubmissionPairType type) {
        var minimalSimilarity = comparison.getMinimalSimilarity();
        var maximalSimilarity = comparison.getMaximalSimilarity();
        var similarity = comparison.getSimilarity();

        var firstSubmissionName = comparison.getFirstSubmissionName();
        var secondSubmissionName = comparison.getSecondSubmissionName();

        if (Math.min(Math.min(minimalSimilarity,maximalSimilarity),similarity) < 0.01 && type == SubmissionPairType.NO_PLAGIARISM)
            return "";

        return String.format("%s,%s,%.2f,%.2f,%.2f,%s\n",
                firstSubmissionName,
                secondSubmissionName,
                similarity,
                minimalSimilarity,
                maximalSimilarity,
                type.toString());
    }

}
