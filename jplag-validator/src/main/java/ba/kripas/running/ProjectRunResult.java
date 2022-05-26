package ba.kripas.running;

import ba.kripas.JarConfig;
import ba.kripas.dataset.Project;
import ba.kripas.jplag.JPlagComparisonWrapper;
import ba.kripas.jplag.JPlagResultWrapper;

import java.util.List;

public class ProjectRunResult {
    private final JarConfig jarConfig;
    private final Project project;

    private final long runtimeInMillis;
    private final int submissions;

    private final List<JPlagComparisonWrapper> comparisons;

    public JarConfig getJarConfig() {
        return jarConfig;
    }

    public Project getProject() {
        return project;
    }

    public long getRuntimeInMillis() {
        return runtimeInMillis;
    }

    public int getSubmissions() {
        return submissions;
    }

    public List<JPlagComparisonWrapper> getComparisons() {
        return comparisons;
    }

    public ProjectRunResult(JarConfig jarConfig, Project project, JPlagResultWrapper jPlagResultWrapper) {
        this.jarConfig = jarConfig;
        this.project = project;
        this.comparisons = jPlagResultWrapper.getComparisons();
        this.runtimeInMillis = jPlagResultWrapper.getDurationInMillis();
        this.submissions = jPlagResultWrapper.getNumberOfSubmissions();
    }
}
