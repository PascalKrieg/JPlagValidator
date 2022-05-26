package ba.kripas.jplag;

import java.util.List;

public class JPlagResultWrapper {
    private final List<JPlagComparisonWrapper> comparisons;

    private final long durationInMillis;

    private final int numberOfSubmissions;

    public List<JPlagComparisonWrapper> getComparisons() {
        return comparisons;
    }

    public long getDurationInMillis() {
        return durationInMillis;
    }

    public int getNumberOfSubmissions() {
        return numberOfSubmissions;
    }

    public JPlagResultWrapper(List<JPlagComparisonWrapper> comparisons, long durationInMillis, int numberOfSubmissions) {
        this.comparisons = comparisons;
        this.durationInMillis = durationInMillis;
        this.numberOfSubmissions = numberOfSubmissions;
    }
}
