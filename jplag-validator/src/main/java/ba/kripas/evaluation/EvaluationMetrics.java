package ba.kripas.evaluation;

public class EvaluationMetrics {
    private final long runtime;
    private final int truePositives;
    private final int falsePositives;
    private final int trueNegatives;
    private final int falseNegatives;

    public long getRuntime() {
        return runtime;
    }

    public float getPrecision() {
        if (truePositives + falsePositives == 0)
            return Float.POSITIVE_INFINITY;
        return (float)truePositives / (truePositives + falsePositives);
    }

    public float getRecall() {
        return (float) truePositives / (truePositives + falseNegatives);
    }

    public float getFMeasure() {
        var precision = getPrecision();
        var recall = getRecall();
        return 2 * precision * recall / (precision + recall);
    }

    public int getTruePositives() {
        return truePositives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public int getTrueNegatives() {
        return trueNegatives;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }

    public EvaluationMetrics(long runtime, int truePositives, int falsePositives, int trueNegatives, int falseNegatives) {
        this.runtime = runtime;
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.trueNegatives = trueNegatives;
        this.falseNegatives = falseNegatives;
    }
}
