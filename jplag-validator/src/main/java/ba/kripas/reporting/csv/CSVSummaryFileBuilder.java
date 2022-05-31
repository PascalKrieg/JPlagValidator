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
        return "commit,jar_file,config_id,project,submissions,runtime,precision,recall,f_measure,true_positives,true_negatives,false_positives,false_negatives\n";
    }

    private String buildEntryLine(JarConfig jarConfig, ProjectRunResult projectRunResult) {
        var evaluation = evaluator.getForThreshold(projectRunResult, 30f);

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%d,%d\n",
                jarConfig.getCommitId(),
                jarConfig.getJarFile().getName(),
                jarConfig.getConfigId(),
                projectRunResult.getProject().getName(),
                projectRunResult.getSubmissions(),
                projectRunResult.getRuntimeInMillis(),
                formatter.format(evaluation.getPrecision()),
                formatter.format(evaluation.getRecall()),
                formatter.format(evaluation.getFMeasure()),
                evaluation.getTruePositives(),
                evaluation.getTrueNegatives(),
                evaluation.getFalsePositives(),
                evaluation.getFalseNegatives());
    }

}
