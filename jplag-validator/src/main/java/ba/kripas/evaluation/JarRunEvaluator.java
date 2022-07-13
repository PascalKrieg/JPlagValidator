package ba.kripas.evaluation;

import ba.kripas.dataset.SubmissionPairType;
import ba.kripas.jplag.JPlagComparisonWrapper;
import ba.kripas.running.ProjectRunResult;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
        var noPlagiarismStats = getStatistics(projectResult, SubmissionPairType.NO_PLAGIARISM);
        var mossadStats = getStatistics(projectResult, SubmissionPairType.MOSSAD_PLAG);
        var commonPlagStats = getStatistics(projectResult, SubmissionPairType.COMMON_PLAG);

        return buildMetrics(noPlagiarismStats, commonPlagStats, mossadStats);
    }

    private DescriptiveStatistics getStatistics(ProjectRunResult projectResult, SubmissionPairType type) {
        var project = projectResult.getProject();

        var statistics = new DescriptiveStatistics();

        projectResult.getComparisons()
                .stream()
                .filter(comparison -> project.getPairType(comparison.getFirstSubmissionName(), comparison.getSecondSubmissionName()) == type)
                .mapToDouble(JPlagComparisonWrapper::getSimilarity)
                .forEach(statistics::addValue);

        return statistics;
    }

    public EvaluationMetrics getForPercentile(ProjectRunResult projectResult, float percentile) {
        resetValues();

        var threshold = calculatePercentile(projectResult, percentile);
        processProject(projectResult, threshold);

        var noPlagiarismStats = getStatistics(projectResult, SubmissionPairType.NO_PLAGIARISM);
        var mossadStats = getStatistics(projectResult, SubmissionPairType.MOSSAD_PLAG);
        var commonPlagStats = getStatistics(projectResult, SubmissionPairType.COMMON_PLAG);

        return buildMetrics(noPlagiarismStats, commonPlagStats, mossadStats);
    }

    private EvaluationMetrics buildMetrics(DescriptiveStatistics noPlagiarismStats, DescriptiveStatistics commonPlagStats, DescriptiveStatistics mossadStats) {
        return new EvaluationMetrics(noPlagiarismStats, commonPlagStats, mossadStats,
                falsePositives, trueNegatives, falseNegativesCommon, falseNegativesMossad,
                truePositivesCommon, truePositivesMossad);
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
        totalRuntime += projectResult.getActualRuntimeInMillis();

        var project = projectResult.getProject();

        for (var comparison : projectResult.getComparisons()) {
            var actualType = project.getPairType(comparison.getFirstSubmissionName(), comparison.getSecondSubmissionName());

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















