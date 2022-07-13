package ba.kripas.reporting.csv;

import ba.kripas.dataset.Project;
import ba.kripas.dataset.SubmissionPairType;
import ba.kripas.jplag.JPlagComparisonWrapper;
import ba.kripas.running.JarConfig;

import java.text.DecimalFormat;
import java.util.List;

public class CSVJarResultFileBuilder {
    private final DecimalFormat formatter;

    private static final boolean skipTrueNegatives = true;

    public CSVJarResultFileBuilder(DecimalFormat formatter) {
        this.formatter = formatter;
    }

    public String buildJarResultFileName(JarConfig jarConfig, Project project) {
        return jarConfig.getCommitId().substring(0, 6) + "-" + jarConfig.getJarFile().getName() + "-"+ jarConfig.getConfigId() + "-" + project.getName() + ".csv";
    }

    public String buildJarResultContent(Project project, List<JPlagComparisonWrapper> comparisons) {
        StringBuilder sb = new StringBuilder(buildJarResultTitleLine());
        comparisons.forEach(comparison -> {
            var actualType = project.getPairType(comparison.getFirstSubmissionName(), comparison.getSecondSubmissionName());
            sb.append(buildJarResultEntryLine(comparison, actualType));
        });
        return sb.toString();
    }

    private String buildJarResultTitleLine() {
        return "first_submission,second_submission,similarity,minimal_similarity,maximal_similarity,type,suspicious\n";
    }

    private String buildJarResultEntryLine(JPlagComparisonWrapper comparison, SubmissionPairType type) {
        var minimalSimilarity = formatter.format(comparison.getMinimalSimilarity());
        var maximalSimilarity = formatter.format(comparison.getMaximalSimilarity());
        var similarity = formatter.format(comparison.getSimilarity());

        var firstSubmissionName = comparison.getFirstSubmissionName();
        var secondSubmissionName = comparison.getSecondSubmissionName();

        if (skipTrueNegatives && !comparison.isSuspicious() && type == SubmissionPairType.NO_PLAGIARISM
                && Math.min(Math.min(comparison.getMinimalSimilarity(), comparison.getMaximalSimilarity()), comparison.getSimilarity()) < 0.01)
            return "";

        return String.format("%s,%s,%s,%s,%s,%s,%s\n",
                firstSubmissionName,
                secondSubmissionName,
                similarity,
                minimalSimilarity,
                maximalSimilarity,
                type.toString(),
                comparison.isSuspicious());
    }
}
