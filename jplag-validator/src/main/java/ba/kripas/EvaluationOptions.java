package ba.kripas;

import de.jplag.options.JPlagOptions;

public class EvaluationOptions {
    private final JPlagOptions jPlagOptions;
    private final float similarityThreshold;

    public EvaluationOptions(JPlagOptions jPlagOptions, float similarityThreshold) {
        this.jPlagOptions = jPlagOptions;
        this.similarityThreshold = similarityThreshold;
    }

    public JPlagOptions getJPlagOptions() {
        return jPlagOptions;
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }
}
