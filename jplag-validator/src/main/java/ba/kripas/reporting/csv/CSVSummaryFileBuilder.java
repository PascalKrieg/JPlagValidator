package ba.kripas.reporting.csv;

import ba.kripas.evaluation.JarRunEvaluator;
import ba.kripas.running.JarConfig;
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


    public String buildContent(Summary summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTitleLine());
        var evaluator = new JarRunEvaluator();
        for (var jarResult : summary.getJarResults()) {
            for (var projectResulst : jarResult.getProjectResult()) {
                sb.append(buildEntryLine(jarResult.getConfig(), projectResulst));
            }
        }
        return sb.toString();
    }

    private String buildTitleLine() {
        return "commit,jar_file,config_id,project,submissions,runtime_jplag,runtime_actual," +
                "precision,recall,f_measure,balanced_accuracy,mossad_detection," +
                "no_plag_mean,no_plag_median,common_mean,common_median,mossad_mean,mossad_median," +
                "true_positives,true_negatives,false_positives,false_negatives\n";
    }

    private String buildEntryLine(JarConfig jarConfig, ProjectRunResult projectRunResult) {
        //var evaluation = evaluator.getForThreshold(projectRunResult, 30f);
        var evaluation = evaluator.getForThreshold(projectRunResult, 40f);

        var commitId = jarConfig.getCommitId();

        if (commitId.length() > 9)
            commitId = commitId.substring(0, 9);

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%d,%d\n",
                commitId,
                jarConfig.getJarFile().getName(),
                jarConfig.getConfigId(),
                projectRunResult.getProject().getName(),
                projectRunResult.getSubmissions(),
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

}
