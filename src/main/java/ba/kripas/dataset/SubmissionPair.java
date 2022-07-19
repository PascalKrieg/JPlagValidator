package ba.kripas.dataset;

import java.util.Objects;

public class SubmissionPair {
    private final String firstSubmission;
    private final String secondSubmission;

    private final SubmissionPairType type;

    public SubmissionPair(String firstSubmission, String secondSubmission, SubmissionPairType type) {
        var comparison = firstSubmission.compareTo(secondSubmission);
        if (comparison == 0) {
            throw new IllegalArgumentException("Tried to create submission pair between the same submission or duplicate submission name in dataset");
        }

        if (comparison < 0) {
            this.firstSubmission = firstSubmission;
            this.secondSubmission = secondSubmission;
        } else {
            this.secondSubmission = secondSubmission;
            this.firstSubmission = firstSubmission;
        }

        this.type = type;
    }

    public String getFirstSubmission() {
        return firstSubmission;
    }

    public String getSecondSubmission() {
        return secondSubmission;
    }

    public SubmissionPairType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubmissionPair that = (SubmissionPair) o;
        return firstSubmission.equals(that.firstSubmission) && secondSubmission.equals(that.secondSubmission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstSubmission, secondSubmission);
    }
}
