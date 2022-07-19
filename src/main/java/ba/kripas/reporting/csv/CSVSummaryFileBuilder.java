package ba.kripas.reporting.csv;

import ba.kripas.evaluation.JarRunEvaluator;
import ba.kripas.running.JarConfig;
import ba.kripas.running.JarRunResult;
import ba.kripas.running.ProjectRunResult;
import ba.kripas.running.Summary;

import java.text.DecimalFormat;

public class CSVSummaryFileBuilder {
    private final DecimalFormat formatter;
    private final JarRunEvaluator evaluator;

    public CSVSummaryFileBuilder(DecimalFormat formatter) {
        this.formatter = formatter;
        this.evaluator = new JarRunEvaluator();
    }

    public String buildFileName() {
        return "summary.csv";
    }


    public String buildFullContent(Summary summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTitleLine());
        for (var jarResult : summary.getJarResults()) {
            for (var projectResulst : jarResult.getProjectResult()) {
                sb.append(buildEntryLine(jarResult.getConfig(), projectResulst));
            }
        }
        return sb.toString();
    }

    public String buildSummarizedContent(Summary summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildSummarizedTitleLine());
        for (var jarResult : summary.getJarResults()) {
            sb.append(buildSummarizedEntryLine(jarResult));
        }
        return sb.toString();
    }

    private String buildTitleLine() {
        return "commit,jar_file,config_id,project,submissions,runtime_jplag,runtime_actual," 
                + "precision,recall,f_measure,balanced_accuracy,mossad_detection," 
                + "no_plag_mean,no_plag_median,common_mean,common_median,mossad_mean,mossad_median," 
                + "true_positives,true_negatives,false_positives,false_negatives\n";
    }

    private String buildEntryLine(JarConfig jarConfig, ProjectRunResult projectRunResult) {
        //var evaluation = evaluator.getForThreshold(projectRunResult, 30f);
        var evaluation = evaluator.getForThreshold(projectRunResult, 40f);

        var commitId = jarConfig.getCommitId();

        if (commitId.length() > 9) {
            commitId = commitId.substring(0, 9);
        }


        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%d,%d\n",
                commitId,
                jarConfig.getJarFile().getName(),
                jarConfig.getConfigId(),
                projectRunResult.getProject().getName(),
                projectRunResult.getSubmissionCount(),
                projectRunResult.getJPlagRuntimeInMillis(),
                projectRunResult.getActualRuntimeInMillis(),
                formatter.format(evaluation.getPrecision()),
                formatter.format(evaluation.getRecall()),
                formatter.format(evaluation.getFMeasure()),
                formatter.format(evaluation.getBalancedAccuracy()),
                formatter.format(evaluation.getMossadDetectionRate()),
                formatter.format(evaluation.getNoPlagiarismStatistics().getMean()),
                formatter.format(evaluation.getNoPlagiarismStatistics().getPercentile(50)),
                formatter.format(evaluation.getCommonPlagStatistics().getMean()),
                formatter.format(evaluation.getCommonPlagStatistics().getPercentile(50)),
                formatter.format(evaluation.getMossadStatistics().getMean()),
                formatter.format(evaluation.getMossadStatistics().getPercentile(50)),
                evaluation.getTruePositives(),
                evaluation.getTrueNegatives(),
                evaluation.getFalsePositives(),
                evaluation.getFalseNegatives());
    }

    private String buildSummarizedTitleLine() {
        return "jar_file,commit,config_id,"
                + "precision,recall,f_measure,balanced_accuracy,mossad_detection,"
                + "no_plag_mean,no_plag_median,common_mean,common_median,mossad_mean,mossad_median\n";
    }

    private String buildSummarizedEntryLine(JarRunResult jarResult) {
        var totalSubmissions = 0;

        for (ProjectRunResult projectResult : jarResult.getProjectResult()) {
            totalSubmissions += projectResult.getSubmissionCount();
        }

        double averagePrecision = 0;
        double averageRecall = 0;
        double averageFMeasure = 0;
        double averageBalancedAccuracy = 0;
        double averageMossadDetection = 0;
        double averageNoPlagMean = 0;
        double averageNoPlagMedian = 0;
        double averageCommonMean = 0;
        double averageCommonMedian = 0;
        double averageMossadMean = 0;
        double averageMossadMedian = 0;

        for (ProjectRunResult projectResult : jarResult.getProjectResult()) {
            var weight = projectResult.getSubmissionCount();

            var evaluation = evaluator.getForThreshold(projectResult, 40f);
            averagePrecision += weight * evaluation.getPrecision();
            averageRecall += weight * evaluation.getRecall();
            averageFMeasure += weight * evaluation.getFMeasure();
            averageBalancedAccuracy = weight * evaluation.getBalancedAccuracy();
            averageMossadDetection = weight * evaluation.getMossadDetectionRate();
            averageNoPlagMean = weight * evaluation.getNoPlagiarismStatistics().getMean();
            averageNoPlagMedian = weight * evaluation.getNoPlagiarismStatistics().getPercentile(50);
            averageCommonMean = weight * evaluation.getCommonPlagStatistics().getMean();
            averageCommonMedian = weight * evaluation.getCommonPlagStatistics().getPercentile(50);
            averageMossadMean = weight * evaluation.getMossadStatistics().getMean();
            averageMossadMedian = weight * evaluation.getMossadStatistics().getPercentile(50);
        }

        averagePrecision /= totalSubmissions;
        averageRecall /= totalSubmissions;
        averageFMeasure /= totalSubmissions;
        averageBalancedAccuracy /= totalSubmissions;
        averageMossadDetection /= totalSubmissions;
        averageNoPlagMean /= totalSubmissions;
        averageNoPlagMedian /= totalSubmissions;
        averageCommonMean /= totalSubmissions;
        averageCommonMedian /= totalSubmissions;
        averageMossadMean /= totalSubmissions;
        averageMossadMedian /= totalSubmissions;

        var jarConfig = jarResult.getConfig();

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                jarConfig.getJarFile().getName(),
                jarConfig.getCommitId(),
                jarConfig.getConfigId(),
                formatter.format(averagePrecision),
                formatter.format(averageRecall),
                formatter.format(averageFMeasure),
                formatter.format(averageBalancedAccuracy),
                formatter.format(averageMossadDetection),
                formatter.format(averageNoPlagMean),
                formatter.format(averageNoPlagMedian),
                formatter.format(averageCommonMean),
                formatter.format(averageCommonMedian),
                formatter.format(averageMossadMean),
                formatter.format(averageMossadMedian));
    }

}
