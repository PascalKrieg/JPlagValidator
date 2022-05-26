package ba.kripas.running;

public class EvaluationOptions {
    private final float similarityThreshold;

    public EvaluationOptions(float similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }
}
