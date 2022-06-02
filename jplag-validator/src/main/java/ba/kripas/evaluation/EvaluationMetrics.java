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

    public float getBalancedAccuracy() {
        return (getTruePositiveRate() + getTrueNegativeRate()) / 2;
    }

    public int getTotalPositives() {
        return truePositives + falsePositives;
    }

    public int getTotalNegatives() {
        return trueNegatives + falseNegatives;
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

    public float getTruePositiveRate() {
        return (float)getTruePositives() / getTotalPositives();
    }

    public float getTrueNegativeRate() {
        return (float)getTrueNegatives() / getTotalNegatives();
    }

    public float getFalsePositiveRate() {
        return (float) getFalsePositives() / getTotalNegatives();
    }

    public float getFalseNegativeRate() {
        return (float) getFalseNegatives() / getTotalPositives();
    }

    public EvaluationMetrics(long runtime, int truePositives, int falsePositives, int trueNegatives, int falseNegatives) {
        this.runtime = runtime;
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.trueNegatives = trueNegatives;
        this.falseNegatives = falseNegatives;
    }
}
