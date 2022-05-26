package ba.kripas.reporting;

import ba.kripas.JarConfig;
import ba.kripas.Summary;
import ba.kripas.dataset.SubmissionPairType;
import ba.kripas.jplag.JPlagComparisonWrapper;

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
    public void writeResult(Summary summary) {
        try {
            Path filePath = FileSystems.getDefault().getPath(targetPath, buildFileName());
            System.out.println("Target result file: " + filePath.toString());
            FileWriter writer = new FileWriter(filePath.toFile());
            writer.write(buildFileContent(summary));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildFileContent(Summary summary) {
        StringBuilder sb = new StringBuilder(buildTitleLine());
        summary.getJarResults().forEach(jarRunResult -> {
            jarRunResult.getProjectResult().forEach(projectRunResult -> {
                projectRunResult.getComparisons().forEach(comparison -> {
                    var project = projectRunResult.getProject();
                    var actualType = project.GetPairType(comparison.getFirstSubmissionName(), comparison.getSecondSubmissionName());
                    sb.append(buildEntryLine(comparison, actualType, project.getName(), jarRunResult.getConfig()));
                });
            });
        });
        return sb.toString();
    }

    private String buildFileName() {
        //return "evaluation.csv";
        return (new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date())) + ".csv";
    }

    private String buildTitleLine() {
        return "jar_file,commit,project,first_submission,second_submission,similarity,minimal_similarity,maximal_similarity,type\n";
    }

    private String buildEntryLine(JPlagComparisonWrapper comparison, SubmissionPairType type, String projectName, JarConfig jarConfig) {
        var minimalSimilarity = comparison.getMinimalSimilarity();
        var maximalSimilarity = comparison.getMaximalSimilarity();
        var similarity = comparison.getSimilarity();

        var jarName = jarConfig.getJarFile().getName();
        var firstSubmissionName = comparison.getFirstSubmissionName();
        var secondSubmissionName = comparison.getSecondSubmissionName();

        var commit = jarConfig.getCommitId().substring(0, 6);

        if (Math.min(Math.min(minimalSimilarity, maximalSimilarity), similarity) < 0.01 && type == SubmissionPairType.NO_PLAGIARISM)
            return "";

        return String.format("%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%s\n",
                jarName,
                commit,
                projectName,
                firstSubmissionName,
                secondSubmissionName,
                similarity,
                minimalSimilarity,
                maximalSimilarity,
                type.toString());
    }

}
