package ba.kripas.dataset;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class Project {
    private final String name;
    private final Path path;
    private final Language language;

    private final HashMap<String, SubmissionPairType> pairMap;

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public Language getLanguage() {
        return language;
    }

    public Project(String name, Path path, Language language, List<SubmissionPair> submissionPairs) {
        this.name = name;
        this.path = path;
        this.language = language;
        this.pairMap = new HashMap<>();
        submissionPairs.forEach(submissionPair -> {
            pairMap.put(buildKey(submissionPair), submissionPair.getType());
        });
    }

    public SubmissionPairType getPairType(String first, String second) {
        var key = buildKey(first, second);
        if (!pairMap.containsKey(key)) {
            return SubmissionPairType.NO_PLAGIARISM;
        }

        return pairMap.get(key);
    }

    private static String buildKey(SubmissionPair pair) {
        return buildKey(pair.getFirstSubmission(), pair.getSecondSubmission());
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
