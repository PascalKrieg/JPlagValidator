package ba.kripas.running;

import ba.kripas.JarConfig;
import ba.kripas.dataset.Project;

import java.util.List;

public class JarRunResult {

    private final JarConfig config;
    private final List<ProjectRunResult> projectResult;
    private final List<Project> errorProjects;

    public List<ProjectRunResult> getProjectResult() {
        return projectResult;
    }

    public List<Project> getErrorProjects() {
        return errorProjects;
    }

    public JarConfig getConfig() {
        return config;
    }

    public JarRunResult(JarConfig config, List<ProjectRunResult> projectResult, List<Project> errorProjects) {
        this.config = config;
        this.projectResult = projectResult;
        this.errorProjects = errorProjects;
    }
}
