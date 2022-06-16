package ba.kripas.evaluation;

import ba.kripas.dataset.SubmissionPairType;
import ba.kripas.jplag.JPlagComparisonWrapper;
import ba.kripas.running.JarRunResult;
import ba.kripas.running.ProjectRunResult;

import java.util.ArrayList;
import java.util.List;

public class JarRunEvaluator {
    private int falsePositives = 0;
    private int falseNegatives = 0;
    private int truePositives = 0;
    private int trueNegatives = 0;

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
        return new EvaluationMetrics(totalRuntime, truePositives, falsePositives, trueNegatives, falseNegatives);
    }

    private void resetValues() {
        falsePositives = 0;
        falseNegatives = 0;

        truePositives = 0;
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
            if (actualType == SubmissionPairType.NO_PLAGIARISM) {
                falsePositives++;
            } else {
                truePositives++;
            }
        } else {
            // Not suspicious
            if (actualType == SubmissionPairType.NO_PLAGIARISM) {
                // Not Suspicious and is not plagiarism
                trueNegatives++;
            } else {
                // Not Suspicious and is not plagiarism
                falseNegatives++;
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















