package ba.kripas.dataset;

public enum SubmissionPairType {
    NO_PLAGIARISM("no_plagiarism"),
    COMMON_PLAG("common"),
    MOSSAD_PLAG("mossad"),
    MOSSAD_SIBLING("mossad_sibling"),
    HYBRID_PLAG("hybrid"),
    HYBRID_SIBLING("hybrid_sibling");

    private final String stringRepresentation;

    SubmissionPairType(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }
}
