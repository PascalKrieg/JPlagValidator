package ba.kripas.evaluation;

public class EvaluationMetrics {
    private final long runtime;
    private final int falsePositives;
    private final int trueNegatives;

    private final int falseNegativesCommon;
    private final int falseNegativesMossad;

    private final int truePositivesCommon;
    private final int truePositivesMossad;

    public long getRuntime() {
        return runtime;
    }

    public float getPrecision() {
        if (getTruePositives() + falsePositives == 0)
            return Float.POSITIVE_INFINITY;
        return (float)getTruePositives() / (getTotalPositives() + falsePositives);
    }

    public float getRecall() {
        return (float) getTruePositives() / (getTruePositives() + getFalseNegatives());
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
        return getTruePositives() + falsePositives;
    }

    public int getTotalNegatives() {
        return trueNegatives + getFalseNegatives();
    }

    public int getTruePositives() {
        return truePositivesCommon + truePositivesMossad;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public int getTrueNegatives() {
        return trueNegatives;
    }

    public int getFalseNegatives() {
        return falseNegativesCommon + falseNegativesMossad;
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

    public int getFalseNegativesCommon() {
        return falseNegativesCommon;
    }

    public int getFalseNegativesMossad() {
        return falseNegativesMossad;
    }

    public int getTruePositivesCommon() {
        return truePositivesCommon;
    }

    public int getTruePositivesMossad() {
        return truePositivesMossad;
    }

    public float getMossadDetectionRate() {
        return (float)truePositivesMossad / (falseNegativesMossad + truePositivesMossad);
    }

    public EvaluationMetrics(long runtime, int falsePositives, int trueNegatives, int falseNegativesCommon, int falseNegativesMossad, int truePositivesCommon, int truePositivesMossad) {
        this.runtime = runtime;
        this.falsePositives = falsePositives;
        this.trueNegatives = trueNegatives;
        this.falseNegativesCommon = falseNegativesCommon;
        this.falseNegativesMossad = falseNegativesMossad;
        this.truePositivesCommon = truePositivesCommon;
        this.truePositivesMossad = truePositivesMossad;
    }
}
