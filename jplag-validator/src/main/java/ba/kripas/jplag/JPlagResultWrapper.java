package ba.kripas.jplag;

import java.util.List;

public class JPlagResultWrapper {
    private final List<JPlagComparisonWrapper> comparisons;
    private final long jplagDurationInMillis;
    private final long actualDurationInMillis;
    private final int numberOfSubmissions;

    public JPlagResultWrapper(List<JPlagComparisonWrapper> comparisons, long jplagDurationInMillis, long actualDurationInMillis, int numberOfSubmissions) {
        this.comparisons = comparisons;
        this.jplagDurationInMillis = jplagDurationInMillis;
        this.actualDurationInMillis = actualDurationInMillis;
        this.numberOfSubmissions = numberOfSubmissions;
    }

    public List<JPlagComparisonWrapper> getComparisons() {
        return comparisons;
    }

    public long getJplagDurationInMillis() {
        return jplagDurationInMillis;
    }

    public long getActualDurationInMillis() {
        return actualDurationInMillis;
    }

    public int getNumberOfSubmissions() {
        return numberOfSubmissions;
    }
}
