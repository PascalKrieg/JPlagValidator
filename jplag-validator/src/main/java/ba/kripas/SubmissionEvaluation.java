package ba.kripas;

public class SubmissionEvaluation {
    String submissionName;
    float maxSimilarity;
    boolean isSuspicious;
    //SubmissionType actualType;

    public SubmissionEvaluation(String submissionName, float maxSimilarity, boolean isSuspicious, SubmissionType actualType) {
        this.submissionName = submissionName;
        this.maxSimilarity = maxSimilarity;
        this.isSuspicious = isSuspicious;
        //this.actualType = actualType;
    }

    public String getSubmissionName() {
        return submissionName;
    }

    public float getMaxSimilarity() {
        return maxSimilarity;
    }

    public boolean isSuspicious() {
        return isSuspicious;
    }

    public boolean isPlagiarism() {
        return getActualType().equals(SubmissionType.COMMON_PLAG) || getActualType().equals(SubmissionType.MOSSAD_PLAG);
    }

    public boolean isFalsePositive() {
        return getActualType().equals(SubmissionType.REAL) && isSuspicious();
    }

    public boolean isFalseNegative() {
        return isPlagiarism() && !isSuspicious();
    }

    public SubmissionType getActualType() {
        if (submissionName.startsWith("plag_src"))
            return SubmissionType.PLAG_SOURCE;
        if (submissionName.startsWith("common"))
            return SubmissionType.COMMON_PLAG;
        if (submissionName.startsWith("mossad"))
            return SubmissionType.MOSSAD_PLAG;
        return SubmissionType.REAL;
    }
}
