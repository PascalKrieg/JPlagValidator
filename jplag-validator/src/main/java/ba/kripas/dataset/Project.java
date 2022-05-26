package ba.kripas.dataset;

import java.nio.file.Path;
import java.util.Map;

public class Project {
    private final String name;
    private final Path path;

    private final Map<String, SubmissionPairType> pairTypes;

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public Project(String name, Path path, Map<String, SubmissionPairType> pairTypes) {
        this.name = name;
        this.path = path;
        this.pairTypes = pairTypes;
    }

    private SubmissionPairType GetPairType(String first, String second) {
        var key = buildKey(first, second);
        if (!pairTypes.containsKey(key))
            return SubmissionPairType.NO_PLAGIARISM;

        return pairTypes.get(key);
    }

    private static String buildKey(String firstSubmission, String secondSubmission) {
        var comparison = firstSubmission.compareTo(secondSubmission);
        if (comparison == 0)
            throw new IllegalArgumentException("Tried to create submission pair between the same submission or duplicate submission name in dataset");

        if (comparison < 0) {
            return firstSubmission + secondSubmission;
        } else {
            return secondSubmission + firstSubmission;
        }
    }
}
