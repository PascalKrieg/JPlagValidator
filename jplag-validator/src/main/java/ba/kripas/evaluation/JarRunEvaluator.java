package ba.kripas.evaluation;

import ba.kripas.dataset.SubmissionPairType;
import ba.kripas.jplag.JPlagComparisonWrapper;
import ba.kripas.running.JarRunResult;
import ba.kripas.running.ProjectRunResult;

import java.util.ArrayList;
import java.util.List;

public class JarRunEvaluator {
    private int falsePositives = 0;
    private int trueNegatives = 0;

    private int falseNegativesCommon = 0;
    private int falseNegativesMossad = 0;

    private int truePositivesCommon = 0;
    private int truePositivesMossad = 0;

    private int totalRuntime = 0;

    private List<SubmissionPairType> excludedPairTypes = new ArrayList<>();

    public JarRunEvaluator(List<SubmissionPairType> excludedPairTypes) {
        this.excludedPairTypes = excludedPairTypes;
    }

    public JarRunEvaluator() { }

    public EvaluationMetrics getForThreshold(ProjectRunResult projectResult, float threshold) {
        resetValues();

        processProject(projectResult, threshold);

        return buildMetrics();
    }

    public EvaluationMetrics getForPercentile(ProjectRunResult projectResult, float percentile) {
        resetValues();

        var threshold = calculatePercentile(projectResult, percentile);
        processProject(projectResult, threshold);

        return buildMetrics();
    }

    public EvaluationMetrics getForThreshold(JarRunResult result, float threshold) {
        resetValues();

        for (var projectResult : result.getProjectResult()) {
            processProject(projectResult, threshold);
        }
        return buildMetrics();
    }

    public EvaluationMetrics getForPercentile(JarRunResult result, float percentile) {
        resetValues();

        for (var projectResult : result.getProjectResult()) {
            var threshold = calculatePercentile(projectResult, percentile);
            processProject(projectResult, threshold);
        }
        return buildMetrics();
    }

    private EvaluationMetrics buildMetrics() {
        return new EvaluationMetrics(totalRuntime, falsePositives, trueNegatives, falseNegativesCommon, falseNegativesMossad, truePositivesCommon, truePositivesMossad);
    }

    private void resetValues() {
        falsePositives = 0;
        falseNegativesMossad = 0;
        falseNegativesCommon = 0;

        truePositivesMossad = 0;
        truePositivesCommon = 0;
        trueNegatives = 0;

        totalRuntime = 0;
    }

    private void processProject(ProjectRunResult projectResult, float threshold) {
        totalRuntime += projectResult.getRuntimeInMillis();

        var project = projectResult.getProject();

        for (var comparison : projectResult.getComparisons()) {
            var actualType = project.GetPairType(comparison.getFirstSubmissionName(), comparison.getSecondSubmissionName());

            if (excludedPairTypes.contains(actualType))
                continue;

            evaluateComparison(comparison, actualType, threshold);
        }
    }

    private void evaluateComparison(JPlagComparisonWrapper comparison, SubmissionPairType actualType, float threshold) {
        if (comparison.getSimilarity() > threshold || comparison.isSuspicious()) {
            // Suspicious
            switch (actualType) {
                case NO_PLAGIARISM -> falsePositives++;
                case COMMON_PLAG -> truePositivesCommon++;
                default -> truePositivesMossad++;
            }
        } else {
            switch (actualType) {
                case NO_PLAGIARISM -> trueNegatives++;
                case COMMON_PLAG -> falseNegativesCommon++;
                default -> falseNegativesMossad++;
            }
        }
    }

    private static float calculatePercentile(ProjectRunResult result, float percentile) {
        var total = result.getComparisons().size();
        var thresholdIndex = (int) Math.floor(total * percentile);

        var copy = new ArrayList<>(result.getComparisons());
        copy.sort(JPlagComparisonWrapper::compareTo);

        return copy.get(total - thresholdIndex).getSimilarity();
    }
}















