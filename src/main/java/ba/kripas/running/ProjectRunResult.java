package ba.kripas.running;

import ba.kripas.dataset.Project;
import ba.kripas.jplag.JPlagComparisonWrapper;
import ba.kripas.jplag.JPlagResultWrapper;

import java.util.List;

public class ProjectRunResult {
    private final JarConfig jarConfig;
    private final Project project;

    private final long jplagRuntimeInMillis;
    private final long actualRuntimeInMillis;
    private final int submissionCount;

    private final List<JPlagComparisonWrapper> comparisons;

    public JarConfig getJarConfig() {
        return jarConfig;
    }

    public Project getProject() {
        return project;
    }

    public long getJPlagRuntimeInMillis() {
        return jplagRuntimeInMillis;
    }

    public long getActualRuntimeInMillis() {
        return actualRuntimeInMillis;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public List<JPlagComparisonWrapper> getComparisons() {
        return comparisons;
    }

    public ProjectRunResult(JarConfig jarConfig, Project project, JPlagResultWrapper jPlagResultWrapper) {
        this.jarConfig = jarConfig;
        this.project = project;
        this.comparisons = jPlagResultWrapper.getComparisons();
        this.jplagRuntimeInMillis = jPlagResultWrapper.getJplagDurationInMillis();
        this.actualRuntimeInMillis = jPlagResultWrapper.getActualDurationInMillis();
        this.submissionCount = jPlagResultWrapper.getNumberOfSubmissions();
    }
}
